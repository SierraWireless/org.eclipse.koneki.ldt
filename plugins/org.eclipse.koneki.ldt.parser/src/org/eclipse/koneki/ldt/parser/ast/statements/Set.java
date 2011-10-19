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
 * $Id: Set.java 2190 2009-07-29 15:56:04Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.statements;

import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.expressions.ExpressionConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class Set.
 */
public class Set extends BinaryStatement {
	/**
	 * Construct default strict assignment.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 */
	public Set(int start, int end, Chunk left, Chunk right) {
		super(start, end, left, E_ASSIGN, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BinaryExpression#getKind()
	 */
	@Override
	public int getKind() {
		return ExpressionConstants.E_ASSIGN;
	}

	/**
	 * Convert to string in pattern: "left = right".
	 * 
	 * @return the string
	 */
	public String toString() {
		return getLeft().toString() + '=' + getRight().toString();
	}

	/**
	 * Traversing child nodes, except right side one when they are used as initialization for {@link Declaration}s . This is a
	 * <strong>workaround</strong>.
	 */
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			if (getLeft() != null) {
				getLeft().traverse(visitor);
			}
			if (getRight() != null) {
				@SuppressWarnings("rawtypes")
				List left = getLeft().getChilds();
				@SuppressWarnings("rawtypes")
				List right = getRight().getChilds();
				for (int i = 0; i < right.size(); i++) {
					boolean associatedToDeclaration = left.get(i) instanceof Declaration;
					if (right.get(i) instanceof ASTNode && !associatedToDeclaration) {
						((ASTNode) right.get(i)).traverse(visitor);
					}
				}
			}
			visitor.endvisit(this);
		}
	}
}
