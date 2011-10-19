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
 * @date $Date: 2009-07-29 17:56:04 +0200 (mer., 29 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: Local.java 2190 2009-07-29 15:56:04Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.statements;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.ast.statements.StatementConstants;
import org.eclipse.dltk.utils.CorePrinter;

// TODO: Auto-generated Javadoc
/**
 * The Class Local.
 */
public class Local extends BinaryStatement implements StatementConstants {

	/**
	 * Instantiates a new local.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param identifiers
	 *            the identifiers
	 * @param expressions
	 *            the expressions
	 */
	public Local(int start, int end, Chunk identifiers, Chunk expressions) {
		super(start, end, identifiers, D_VAR_DECL, expressions);
	}

	/**
	 * Instantiates a new local.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param identifiers
	 *            the identifiers
	 */
	public Local(int start, int end, Chunk identifiers) {
		this(start, end, identifiers, null);
	}

	@Override
	public void printNode(CorePrinter output) {
		StringBuffer varList = new StringBuffer();
		StringBuffer valueList = new StringBuffer();
		final String comma = ", "; //$NON-NLS-1$
		Chunk chunk = (Chunk) getLeft();
		for (Object var : chunk.getStatements()) {
			Statement state = (Statement) var;
			varList.append(state.toString());
			varList.append(comma);
		}
		if (getRight() != null) {
			chunk = (Chunk) getRight();
			for (Object e : chunk.getStatements()) {
				Statement statement = (Statement) e;
				if (e instanceof Literal) {
					Literal literal = (Literal) e;
					valueList.append(literal.getValue());
				} else {
					valueList.append(statement.toString());
				}
				valueList.append(comma);
			}
		}
		if (valueList.length() > 0) {
			valueList.insert(0, "= "); //$NON-NLS-1$
		}
		output.formatPrintLn("local " + varList + valueList); //$NON-NLS-1$  
	}

	/**
	 * <strong> Only parse nodes on left sides</strong>. They are {@link Declaration}s which contains nodes on the right as initialisation. This is a
	 * <strong>workaround</strong>.
	 * 
	 * @param visitor
	 *            Browse current AST
	 * @see org.eclipse.koneki.ldt.parser.ast.expressions.BinaryExpression#traverse(org.eclipse.dltk.ast.ASTVisitor)
	 */
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			if (getLeft() != null) {
				getLeft().traverse(visitor);
			}
			visitor.endvisit(this);
		}
	}
}
