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
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.FunctionDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.TableDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.VariableDeclaration;

/**
 * Implementing occurrences highlight, a link between Declaration and their reference in source code is needed. This interface is intended to be
 * implemented from {@link Declaration} which need to be linked to their occurrences.
 * 
 * @see TableDeclaration
 * @see FunctionDeclaration
 * @see VariableDeclaration
 * @see LuaModelElementOccurrencesFinder
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public interface IOccurrenceHolder {
	/**
	 * Appends a reference to a declaration
	 * 
	 * @param node
	 *            {@link ASTNode} representing occurrence to {@link Declaration}
	 */
	public void addOccurrence(ASTNode node);

	/**
	 * Provides occurrences of current declaration
	 * 
	 * @return occurrences of current {@link Declaration}
	 */
	public ASTNode[] getOccurrences();
}
