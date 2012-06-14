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
package org.eclipse.koneki.ldt.core.tests.internal.ast.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.Declaration;

/**
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 * 
 */
public class DeclarationVisitor extends ASTVisitor {
	private List<Declaration> declarations;

	public DeclarationVisitor() {
		super();
		this.declarations = new ArrayList<Declaration>();
	}

	private boolean addDeclration(Declaration d) {
		return this.declarations.add(d);
	}

	public boolean visitGeneral(ASTNode node) throws Exception {

		if (node instanceof Declaration) {
			addDeclration((Declaration) node);
		}
		return true;
	}

	public List<Declaration> getDeclarations() {
		return this.declarations;
	}

	public List<Declaration> getDeclarations(Class<? extends Declaration> c) {
		List<Declaration> list = new ArrayList<Declaration>();
		for (Declaration d : getDeclarations()) {
			if (d.getClass().equals(c)) {
				list.add(d);
			}
		}
		return list;
	}
}
