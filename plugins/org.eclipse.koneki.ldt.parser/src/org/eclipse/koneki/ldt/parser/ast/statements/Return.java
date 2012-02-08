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
package org.eclipse.koneki.ldt.parser.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;

// TODO: Auto-generated Javadoc
/**
 * The Class Return.
 */
public class Return extends Statement implements INavigableNode {

	/** The expressions contained in return {@link Statement} */
	private Chunk returnValues;
	private ASTNode parentNode;

	/**
	 * Instantiates a new return.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param values
	 *            {@link Statement}
	 */
	public Return(int start, int end, List<ASTNode> values) {
		this(start, end, new Chunk(start, end, values));
	}

	public Return(int start, int end, Chunk values) {
		super(start, end);
		this.returnValues = values;
	}

	/**
	 * Instantiates a new return.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public Return(int start, int end) {
		this(start, end, new ArrayList<ASTNode>());
	}

	/**
	 * Adds one of {@link Return} values
	 * 
	 * @param value
	 *            {@link Statement} contained in {@link Return}
	 */
	public void addReturnValue(Statement value) {
		this.returnValues.addStatement(value);
	}

	/**
	 * @see org.eclipse.dltk.ast.statements.Statement#getKind()
	 */
	@Override
	public int getKind() {
		return LuaStatementConstants.S_RETURN;
	}

	/**
	 * @see org.eclipse.dltk.ast.statements.Statement#traverse(org.eclipse.dltk.ast .ASTVisitor)
	 */
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			super.traverse(visitor);
			returnValues.traverse(visitor);
			visitor.endvisit(this);
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
