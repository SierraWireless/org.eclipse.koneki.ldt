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
package org.eclipse.koneki.ldt.parser.ast.declarations;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;
import org.eclipse.koneki.ldt.internal.parser.IOccurrenceHolder;

/**
 * {@link Declaration} of variables provided with an initialization value.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class ScalarVariableDeclaration extends VariableDeclaration implements IOccurrenceHolder, INavigableNode {

	private Expression initialization;
	private ASTNode parentNode;

	/**
	 * @param name
	 * @param nameStart
	 * @param nameEnd
	 * @param declStart
	 * @param declEnd
	 */
	public ScalarVariableDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd, Expression init) {
		this(name, nameStart, nameEnd, declStart, declEnd);
		initialization = init;
	}

	public ScalarVariableDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd) {
		super(name, nameStart, nameEnd, declStart, declEnd);
	}

	public Expression getInitialization() {
		return initialization;
	}

	public void setInitialization(Expression init) {
		initialization = init;
	}

	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			super.traverse(visitor);
			if (getInitialization() != null) {
				getInitialization().traverse(visitor);
			}
			visitor.endvisit(this);
		}
	}

	@Override
	public ASTNode getParent() {
		return parentNode;
	}

	@Override
	public void setParent(ASTNode parent) {
		parentNode = parent;
	}

}
