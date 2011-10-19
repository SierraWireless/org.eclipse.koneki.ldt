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
 * $Id: Table.java 1887 2009-06-18 14:46:07Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.expressions;

import java.util.List;

import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class Table.
 */
public class Table extends Expression implements INavigableNode {

	/** The statements. */
	private ASTListNode statements;
	private ASTNode parentNode;

	/**
	 * Instantiates a new table.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param statements
	 *            the statements
	 */
	public Table(int start, int end, ASTListNode statements) {
		super(start, end);
		this.statements = statements;
	}

	/**
	 * Instantiates a new table.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public Table(int start, int end) {
		this(start, end, new ASTListNode());
	}

	/**
	 * Adds the statement.
	 * 
	 * @param statement
	 *            the statement
	 */
	public void addStatement(Statement statement) {
		statements.addNode(statement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ast.statements.Statement#getKind()
	 */
	@Override
	public int getKind() {
		return LuaExpressionConstants.E_TABLE;
	}

	public ASTListNode getStatements() {
		return statements;
	}

	@Override
	public void traverse(ASTVisitor pVisitor) throws Exception {
		if (pVisitor.visit(this)) {
			super.traverse(pVisitor);
			getStatements().traverse(pVisitor);
			pVisitor.endvisit(this);
		}
	}

	@Override
	public void printNode(CorePrinter output) {
		output.append('{');
		List children = getStatements().getChilds();
		for (int position = 0; position < children.size(); position++) {
			if (children.get(position) instanceof Statement) {
				((Statement) children.get(position)).printNode(output);
			}
		}
		output.append('}');
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
