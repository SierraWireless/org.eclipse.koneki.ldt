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

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;

// TODO: Auto-generated Javadoc
/**
 * The Class Break.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class Break extends AbastractSimpleStatement implements INavigableNode {

	private ASTNode parentNode;

	/**
	 * Instantiates a new break.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public Break(int start, int end) {
		super(start, end, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ast.statements.Statement#getKind()
	 */
	@Override
	public int getKind() {
		return LuaStatementConstants.S_BREAK;
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
