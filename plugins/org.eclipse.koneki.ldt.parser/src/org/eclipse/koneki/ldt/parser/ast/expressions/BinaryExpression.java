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

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.koneki.ldt.parser.internal.IndexedNode;

/**
 * Defines a two operand expression.
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class BinaryExpression extends Expression implements IndexedNode {

	/** Left parent of the expression. */
	private Statement left;

	/** Right parent of the expression. */
	private Statement right;

	/** Kind of expression's operator. */
	protected int kind;

	protected long id;

	/**
	 * Defines a two operands expression.
	 * 
	 * @param left
	 *            Left parent of the expression
	 * @param kind
	 *            Token of the operator
	 * @param right
	 *            Right parent of the expression
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * 
	 * @see org.eclipse.dltk.ast.expressions.ExpressionConstants
	 */
	public BinaryExpression(int start, int end, Expression left, int kind,
			Expression right) {
		super(start, end);
		this.kind = kind;
		this.left = left;
		this.right = right;
		if (left != null) {
			this.setStart(left.sourceStart());
			assert left instanceof Expression;
		}
		if (right != null) {
			this.setEnd(right.sourceEnd());
			assert right instanceof Expression;
		}
	}

	/**
	 * Left parent of the expression.
	 * 
	 * @return Left parent of the expression
	 */
	public Statement getLeft() {
		return left;
	}

	@Override
	public java.lang.String getOperator() {
		switch (getKind()) {
		case E_CONCAT:
			return "..";
		default:
		}
		return super.getOperator();
	}

	/**
	 * Gets the right.
	 * 
	 * @return Left parent of the expression
	 */
	public Statement getRight() {
		return right;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ast.statements.Statement#getKind()
	 */
	@Override
	public int getKind() {
		return this.kind;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ast.expressions.Expression#printNode(org.eclipse.dltk
	 * .utils.CorePrinter)
	 */
	public void printNode(CorePrinter output) {
		if (this.left != null) {
			this.left.printNode(output);
		}

		output.formatPrintLn(this.getOperator());

		if (this.right != null) {
			this.right.printNode(output);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ast.statements.Statement#traverse(org.eclipse.dltk.ast
	 * .ASTVisitor)
	 */
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			super.traverse(visitor);
			if (getLeft() != null) {
				getLeft().traverse(visitor);
			}
			if (getRight() != null) {
				getRight().traverse(visitor);
			}
			visitor.endvisit(this);
		}
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public void setID(long id) {
		this.id = id;
	}
}
