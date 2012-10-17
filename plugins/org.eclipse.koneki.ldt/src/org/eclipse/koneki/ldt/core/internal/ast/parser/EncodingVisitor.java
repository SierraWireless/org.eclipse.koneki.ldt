/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.core.internal.ast.parser;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LocalVar;

/**
 * Lua deals with characters like C does: 8 bit clean. So does Metalua. Eclipse components such as editors handle several {@link Charset}s. Here, we
 * do the matching between Lua offsets and Java charset-aware offsets.
 * 
 * The idea is adjust nodes offsets for each character longer than a byte. Each exotic character offset is store in a {@link NavigableMap}, along with
 * the fixed offset.
 */
public class EncodingVisitor extends ASTVisitor {

	/**
	 * Key: Lua offset<br/>
	 * Value: Java charset encoded offset
	 */
	private final TreeMap<Integer, Integer> cache;
	private final int sourceLength;

	public EncodingVisitor(final String src) {

		/*
		 * Fetch decoder for charset
		 * 
		 * The JNI uses modified UTF-8 strings to represent various string types. Modified UTF-8 strings are the same as those used by the Java VM.
		 * Modified UTF-8 strings are encoded so that character sequences that contain only non-null ASCII characters can be represented using only
		 * one byte per character, but all Unicode characters can be represented.
		 */
		final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder(); //$NON-NLS-1$

		/*
		 * Build cache
		 */
		final CharBuffer source = CharBuffer.wrap(src);
		cache = new TreeMap<Integer, Integer>();
		sourceLength = source.length();
		final ByteBuffer byteBuffer = ByteBuffer.allocate(Math.round(encoder.maxBytesPerChar()));
		final int averageBytesPerChar = Math.round(encoder.averageBytesPerChar());

		// Loop over all characters and check if they are encoded with more than one byte
		int delta = 0;
		source.limit(0);
		while (source.position() < sourceLength) {
			source.limit(source.limit() + 1);

			int currentPosition = source.position();

			// Read next character
			encoder.encode(source, byteBuffer, false);

			// Character byte length is longer than a regular character, it is valuable to cache
			int byteLength = byteBuffer.position();
			if (byteLength > averageBytesPerChar) {

				// Compute difference between this encoding and 8 bit clean
				delta += byteLength - averageBytesPerChar;

				// Cache original offset and fixed one
				cache.put(currentPosition, currentPosition - delta);
			}
			byteBuffer.clear();
		}
	}

	public boolean visitGeneral(final ASTNode node) throws Exception {

		// Only backpatch Lua nodes
		if (!(node instanceof LuaASTNode))
			return true;

		// Update start and end offset
		final LuaASTNode luaNode = (LuaASTNode) node;
		luaNode.setStart(fixedOffet(luaNode.start()));

		// Exclude blocks which have irrelevant offsets, it can happen for scope purposes
		final int nodeEnd = luaNode.end();
		if (nodeEnd <= sourceLength)
			luaNode.setEnd(fixedOffet(nodeEnd));

		// Also fix nodes which deal with several offsets
		if (luaNode instanceof LocalVar) {
			final LocalVar localVar = (LocalVar) luaNode;
			localVar.setScopeMinOffset(fixedOffet(localVar.getScopeMinOffset()));
			localVar.setScopeMaxOffset(fixedOffet(localVar.getScopeMaxOffset()));
		}
		return true;
	}

	private int fixedOffet(final int offset) {

		// Compute from ceiling or exact offset
		final Entry<Integer, Integer> floorEntry = cache.floorEntry(offset);
		if (floorEntry != null)
			return offset - floorEntry.getKey() + floorEntry.getValue();

		// No cached value
		return offset;
	}
}
