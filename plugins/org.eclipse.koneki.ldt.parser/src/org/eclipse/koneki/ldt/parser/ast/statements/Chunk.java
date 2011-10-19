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
 * @date $Date: 2009-07-22 14:42:54 +0200 (mer., 22 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: Chunk.java 2151 2009-07-22 12:42:54Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.statements;

import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.statements.Block;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;

// TODO: Auto-generated Javadoc
/**
 * The Class Chunk.
 */
public class Chunk extends Block implements INavigableNode {

	private ASTNode parentNode;

	/**
	 * Instantiates a new chunk.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param statems
	 *            the statems
	 */
	public Chunk(int start, int end, List<Statement> statems) {
		super(start, end, statems);
	}

	/**
	 * Instantiates a new chunk.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public Chunk(int start, int end) {
		super(start, end);
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
