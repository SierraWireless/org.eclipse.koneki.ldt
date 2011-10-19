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
package org.eclipse.koneki.ldt.internal.parser;

import org.eclipse.dltk.ast.ASTNode;

/**
 * This interface aims to implement a link from child {@link ASTNode}s to their {@link ASTNode} parent.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public interface INavigableNode {
	/**
	 * Provides parent of current {@link ASTNode}. Must have been initialized beforehand using {@link #setParent(ASTNode)}
	 * 
	 * @return Parent of current
	 */
	public ASTNode getParent();

	/**
	 * Initialize parent of current {@link ASTNode}.
	 * 
	 * @param parent
	 *            of current {@link ASTNode}
	 */
	public void setParent(ASTNode parent);
}
