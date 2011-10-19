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

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-06-18 16:46:07 +0200 (jeu., 18 juin 2009) $
 * $Author: kkinfoo $
 * $Id: UnaryExpression.java 1887 2009-06-18 14:46:07Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.expressions;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;
import org.eclipse.koneki.ldt.parser.internal.IndexedNode;

// TODO: Auto-generated Javadoc
/**
 * The Class UnaryExpression.
 */
public class UnaryExpression extends Expression implements IndexedNode {

	/** The kind. */
	private int kind;

	/** The expression. */
	private Statement expression;

	private long id;

	/**
	 * Instantiates a new unary expression.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param kind
	 *            the kind
	 * @param e
	 *            the e
	 */
	public UnaryExpression(int start, int end, int kind, Statement e) {
		super(start, end);
		this.expression = e;
		this.kind = kind;
	}

	/**
	 * Gets the expression.
	 * 
	 * @return the expression
	 */
	public Statement getExpression() {
		return expression;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ast.statements.Statement#getKind()
	 */
	@Override
	public int getKind() {
		return kind;
	}

	public long getID() {
		return id;
	}

	@Override
	public java.lang.String getOperator() {
		switch ( this.getKind() ){
		case LuaExpressionConstants.E_LENGTH:
			return "#";
		case LuaExpressionConstants.E_UN_MINUS:
			return "-";
		case LuaExpressionConstants.E_BNOT:
			return " not ";
		}
		return super.getOperator();
	}

	public void setID(long id) {
		this.id = id;
	}

	/**
	 * Traverse.
	 * 
	 * @param pVisitor
	 *            the visitor
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void traverse(ASTVisitor pVisitor) throws Exception {
		if (pVisitor.visit(this)) {
			super.traverse(pVisitor);
			if (getExpression() != null) {
				getExpression().traverse(pVisitor);
			}
			pVisitor.endvisit(this);
		}
	}

	public void printNode(CorePrinter output) {
		output.formatPrintLn(this.getOperator() + getExpression().toString());
	}
}
