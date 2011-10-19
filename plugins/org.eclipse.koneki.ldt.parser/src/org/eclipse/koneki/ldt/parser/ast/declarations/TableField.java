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
package org.eclipse.koneki.ldt.parser.ast.declarations;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;

public class TableField extends FieldDeclaration implements INavigableNode {

	private ASTNode parentNode;

	public TableField(String name, int nameStart, int nameEnd, int declStart, int declEnd) {
		super(name, nameStart, nameEnd, declStart, declEnd);
	}

	public TableField(SimpleReference name, int declStart, int declEnd) {
		this(name.getName(), name.sourceStart(), name.sourceEnd(), declStart, declEnd);
	}

	public int getKind() {
		return Declaration.D_ARGUMENT;
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
