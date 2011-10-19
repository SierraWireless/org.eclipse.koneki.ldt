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
package org.eclipse.koneki.ldt.parser.ast.visitor;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;

/**
 * TODO Comment this class
 */
public class IndexVisitor extends ASTVisitor {
	private final String search;
	private Expression lhs;

	/**
	 * @param lhsName
	 */
	public IndexVisitor(final String nameToFind) {
		search = nameToFind;
		lhs = null;
	}

	@Override
	public boolean visit(final Expression expression) throws Exception {
		if (expression instanceof Identifier) {
			final Identifier identifier = (Identifier) expression;
			if (identifier.getName().equalsIgnoreCase(search)) {
				setLHS(identifier);
			}
		}
		return visitGeneral(expression);
	}

	@Override
	public boolean visitGeneral(final ASTNode node) throws Exception {
		return getLHS() == null;
	}

	public Expression getLHS() {
		return lhs;
	}

	public void setLHS(final Expression node) {
		lhs = node;
	}
}
