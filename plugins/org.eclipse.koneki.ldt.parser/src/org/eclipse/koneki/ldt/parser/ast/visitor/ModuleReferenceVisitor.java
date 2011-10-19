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
package org.eclipse.koneki.ldt.parser.ast.visitor;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.koneki.ldt.parser.ast.declarations.ModuleReference;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;

public class ModuleReferenceVisitor extends ASTVisitor {
	private final String search;
	private ModuleReference moduleReference;

	public ModuleReferenceVisitor(final String nameToFind) {
		search = nameToFind;
		moduleReference = null;
	}

	@Override
	public boolean visitGeneral(final ASTNode node) throws Exception {
		if (node instanceof Identifier) {
			Identifier identifier = (Identifier) node;
			if (identifier.getName().equals(search)) {
				if (identifier.getDeclaration() instanceof ModuleReference) {
					setModuleReference((ModuleReference) identifier.getDeclaration());
					return false;
				}
			}

		} else if (node instanceof ModuleReference) {
			ModuleReference moduleref = (ModuleReference) node;
			if (moduleref.getName().equals(search)) {
				setModuleReference(moduleref);
				return false;
			}
		}
		return true;
	}

	public ModuleReference getModuleReference() {
		return moduleReference;
	}

	public void setModuleReference(final ModuleReference node) {
		moduleReference = node;
	}
}
