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
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;

/**
 * Defines a two operand expression.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class BinaryExpression extends Expression implements INavigableNode {

	/** Kind of expression's operator. */
	private int kind;

	/** Left parent of the expression. */
	private Statement left;

	/** Right parent of the expression. */
	private Statement right;

	private ASTNode parentNode;

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
	public BinaryExpression(int start, int end, Expression left, int kind, Expression right) {
		super(start, end);
		this.kind = kind;
		this.left = left;
		this.right = right;
		if (left != null) {
			this.setStart(left.sourceStart());
		}
		if (right != null) {
			this.setEnd(right.sourceEnd());
		}
	}

	public BinaryExpression(int start, int end, Expression left, java.lang.String operatorName, Expression right) {
		this(start, end, left, operatorNameToKind(operatorName), right);
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
			return ".."; //$NON-NLS-1$
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
	 * @see org.eclipse.dltk.ast.expressions.Expression#printNode(org.eclipse.dltk .utils.CorePrinter)
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
	 * @see org.eclipse.dltk.ast.statements.Statement#traverse(org.eclipse.dltk.ast .ASTVisitor)
	 */
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			if (getLeft() != null) {
				getLeft().traverse(visitor);
			}
			if (getRight() != null) {
				getRight().traverse(visitor);
			}
			visitor.endvisit(this);
		}
	}

	public static int operatorNameToKind(final java.lang.String s) {

		if ("sub".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_MINUS;
		} else if ("mul".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_MULT;
		} else if ("div".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_DIV;
		} else if ("eq".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_EQUAL;
		} else if ("concat".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_CONCAT;
		} else if ("mod".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_MOD;
		} else if ("pow".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_POWER;
		} else if ("lt".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LT;
		} else if ("le".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LE;
		} else if ("and".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LAND;
		} else if ("or".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LOR;
		} else if ("not".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_BNOT;
		} else if ("len".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LENGTH;
		} else if ("unm".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_UN_MINUS;
		} else {
			// Assume it's an addition
			assert "add".equals(s) : "Unhandled operator: " + s; //$NON-NLS-1$ //$NON-NLS-2$
			return LuaExpressionConstants.E_PLUS;
		}
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
