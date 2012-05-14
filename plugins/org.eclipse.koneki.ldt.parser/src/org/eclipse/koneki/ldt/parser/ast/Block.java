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
package org.eclipse.koneki.ldt.parser.ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.koneki.ldt.parser.api.external.LuaASTNode;

public class Block extends LuaASTNode {
	private List<LocalVar> localVars = new ArrayList<LocalVar>();;

	private List<LuaASTNode> content = new ArrayList<LuaASTNode>();

	public List<LocalVar> getLocalVars() {
		return localVars;
	}

	public List<LuaASTNode> getContent() {
		return content;
	}

	public void addLocalVar(final LocalVar var) {
		localVars.add(var);
		var.getVar().setParent(this);
	}

	public void addContent(final LuaASTNode node) {
		content.add(node);
	}

	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			// traverse block
			for (LuaASTNode node : content) {
				node.traverse(visitor);
			}

			// traverse local var
			for (LocalVar localvar : localVars) {
				localvar.traverse(visitor);
			}

			visitor.endvisit(this);
		}
	}
}
