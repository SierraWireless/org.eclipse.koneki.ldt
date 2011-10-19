/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.parser.ast.expressions;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class Dots.
 */
public class Dots extends Expression implements LuaExpressionConstants, INavigableNode {

	private ASTNode parentNode;

	/**
	 * Instantiates a new dots.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public Dots(int start, int end) {
		super(start, end);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ast.statements.Statement#getKind()
	 */
	@Override
	public int getKind() {
		return E_DOTS;
	}

	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			visitor.endvisit(this);
		}
	}

	@Override
	public void printNode(CorePrinter output) {
		output.append("...");//$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.koneki.ldt.internal.parser.INavigableNode#getParent()
	 */
	@Override
	public ASTNode getParent() {
		return parentNode;
	}

	/**
	 * @see org.eclipse.koneki.ldt.internal.parser.INavigableNode#setParent(org.eclipse.dltk.ast.ASTNode)
	 */
	@Override
	public void setParent(ASTNode parent) {
		parentNode = parent;
	}
}
