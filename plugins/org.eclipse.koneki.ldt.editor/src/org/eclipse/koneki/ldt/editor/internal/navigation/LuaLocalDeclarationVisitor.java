/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.editor.internal.navigation;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;

/**
 * TODO Comment this class
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class LuaLocalDeclarationVisitor extends ASTVisitor {
	private static final int SCOPE_LIMIT = 0;
	private HashMap<ASTNode, Integer> family = new HashMap<ASTNode, Integer>();
	private ArrayList<Declaration> declarations = new ArrayList<Declaration>();
	private int currentDepth;

	public LuaLocalDeclarationVisitor(final ASTNode scopePosition) {
		int depth = SCOPE_LIMIT;
		for (ASTNode node = scopePosition; node != null; node = node instanceof INavigableNode ? ((INavigableNode) node).getParent() : null) {
			family.put(node, depth--);
		}
		currentDepth = depth;
	}

	public Declaration[] getDeclarations() {
		return declarations.toArray(new Declaration[declarations.size()]);
	}

	private boolean intersting(final ASTNode astNode) {

		return family.containsKey(astNode) && family.get(astNode) <= SCOPE_LIMIT;
	}

	@Override
	public boolean visitGeneral(ASTNode node) throws Exception {
		// currentDepth++;
		if (intersting(node) && node instanceof Declaration) {
			declarations.add((Declaration) node);
		}
		return ++currentDepth <= SCOPE_LIMIT;
	}

	@Override
	public void endvisitGeneral(ASTNode node) {
		currentDepth--;
	}

}
