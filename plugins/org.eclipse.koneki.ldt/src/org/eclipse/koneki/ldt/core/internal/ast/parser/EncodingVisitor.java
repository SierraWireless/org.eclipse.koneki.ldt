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

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LocalVar;

/**
 * Here, we do the matching between Lua byte based offsets and Java character based offsets.
 */
public class EncodingVisitor extends ASTVisitor {

	private final OffsetFixer fixer;
	private final int sourceLength;

	public EncodingVisitor(final OffsetFixer offsetFixer) {
		fixer = offsetFixer;
		sourceLength = fixer.getCharactersLength();
	}

	public boolean visitGeneral(final ASTNode node) throws Exception {

		// Only backpatch Lua nodes
		if (!(node instanceof LuaASTNode))
			return true;
		// Update start and end offset
		final LuaASTNode luaNode = (LuaASTNode) node;
		luaNode.setStart(fixer.getCharacterPosition(luaNode.sourceStart()));

		// Exclude blocks which have irrelevant offsets, it can happen for scope purposes
		final int nodeEnd = fixer.getCharacterPosition(luaNode.sourceEnd());
		if (nodeEnd <= sourceLength)
			luaNode.setEnd(nodeEnd);

		// Also fix nodes which deal with several offsets
		if (luaNode instanceof LocalVar) {
			final LocalVar localVar = (LocalVar) luaNode;
			localVar.setScopeMinOffset(fixer.getCharacterPosition(localVar.getScopeMinOffset()));
			localVar.setScopeMaxOffset(fixer.getCharacterPosition(localVar.getScopeMaxOffset()));
		}
		return true;
	}
}
