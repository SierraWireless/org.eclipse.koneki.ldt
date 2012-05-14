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

import org.eclipse.dltk.ast.ASTVisitor;

public class Index extends LuaExpression {
	private LuaExpression left;
	private String right;

	public String getRight() {
		return right;
	}

	public void setRight(final String right) {
		this.right = right;
	}

	public LuaExpression getLeft() {
		return left;
	}

	public void setLeft(final LuaExpression left) {
		this.left = left;
	}

	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			left.traverse(visitor);
			visitor.endvisit(this);
		}
	}
}
