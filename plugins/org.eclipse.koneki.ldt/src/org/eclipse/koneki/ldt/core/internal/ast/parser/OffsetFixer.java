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
import java.util.TreeMap;

/**
 * Lua deals with characters like C does: 8 bit clean. So does Metalua. Eclipse components such as editors handle several {@link Charset}s. Here, we
 * do the matching between Lua offsets and Java charset-aware offsets.
 */
public class OffsetFixer {

	/**
	 * Key: byte position (Lua string offset)<br/>
	 * Value: Difference between byte positions and character positions
	 */
	private final TreeMap<Integer, Integer> cache;
	private final int charactersLength;

	public OffsetFixer(final String src) {
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
		charactersLength = source.length();

		final ByteBuffer byteBuffer = ByteBuffer.allocate(Math.round(encoder.maxBytesPerChar()));
		final int averageBytesPerChar = Math.round(encoder.averageBytesPerChar());

		// Loop over all characters and check if they are encoded with more than one byte
		int bytePosition = 0;
		int delta = 0;
		source.limit(0);
		while (source.position() < charactersLength) {

			// Read next character
			source.limit(source.limit() + 1);
			encoder.encode(source, byteBuffer, false);

			// Character byte length is longer than a regular character, it is valuable to cache
			int bytesForCurrentChar = byteBuffer.position();
			bytePosition += bytesForCurrentChar;
			if (bytesForCurrentChar > averageBytesPerChar) {

				// Compute difference between encoding character and 8 bit clean
				delta += bytesForCurrentChar - averageBytesPerChar;

				// Cache byte position and difference with char position
				cache.put(bytePosition, delta);
			}
			byteBuffer.clear();
		}
	}

	public int getCharacterPosition(final int bytePosition) {

		// Compute difference from ceiling byte position
		final Entry<Integer, Integer> floorEntry = cache.floorEntry(bytePosition);
		if (floorEntry != null)
			return bytePosition - floorEntry.getValue();

		// No difference associated
		return bytePosition;
	}

	/** @return Length of {@link CharBuffer} from given {@link String}. */
	public int getCharactersLength() {
		return charactersLength;
	}
}
