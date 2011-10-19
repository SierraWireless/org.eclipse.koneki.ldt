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
 * $Id: Identifier.java 1887 2009-06-18 14:46:07Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.expressions;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;

/**
 * Used to define variables' names.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class Identifier extends SimpleReference implements INavigableNode {

	/** {@link Declaration} related to this identifier */
	private Declaration declaration;
	private ASTNode parentNode;

	/**
	 * Instantiates a new identifier.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param value
	 *            the value
	 */
	public Identifier(int start, int end, java.lang.String value) {
		super(start, end, value);
	}

	/**
	 * Provides related {@link Declaration}. In
	 * 
	 * <pre>
	 * function foo()end
	 * foo()
	 * </pre>
	 * 
	 * The <code>foo()</code> function call contains an {@link Identifier}. This {@link Identifier} is related to previous {@link Declaration}. This
	 * {@link Declaration} is available here.
	 * 
	 * @return Related {@link Declaration}
	 */
	public Declaration getDeclaration() {
		return declaration;
	}

	/**
	 * Indicates if variable is related to {@link Declaration}
	 * 
	 * @return true is there is a related declaration, false else way
	 */
	public boolean hasDeclaration() {
		return getDeclaration() != null;
	}

	public void printNode(CorePrinter output) {
		output.formatPrintLn(getName());
	}

	public void setDeclaration(Declaration d) {
		declaration = d;
	}

	/**
	 * Human readable name of model identifier.
	 */
	@Override
	public java.lang.String toString() {
		return getStringRepresentation();
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
