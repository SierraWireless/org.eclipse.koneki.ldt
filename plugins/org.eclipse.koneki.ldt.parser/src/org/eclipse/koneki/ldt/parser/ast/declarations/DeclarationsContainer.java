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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.statements.Statement;

/**
 * A container of declaration for a lua source file a lua source file could contains :
 * <ul>
 * <li>(0-1) module declaration</li>
 * <li>(0-n) global function</li>
 * <li>(0-n) global field</li>
 * <ul>
 */
public class DeclarationsContainer extends Statement {

	private LuaModuleDeclaration module;

	private List<Declaration> declarations = new ArrayList<Declaration>();

	public DeclarationsContainer() {
	}

	/**
	 * @return the list of all the declarations of this container. must be used to add declarations
	 */
	public List<Declaration> getDeclarations() {
		return declarations;
	}

	/**
	 * @return the list of all the global functions (read only)
	 */
	public List<FunctionDeclaration> getGlobalFunctions() {
		List<FunctionDeclaration> results = new ArrayList<FunctionDeclaration>();
		for (Declaration declaration : declarations) {
			if (declaration instanceof FunctionDeclaration) {
				FunctionDeclaration function = (FunctionDeclaration) declaration;
				if (((FunctionDeclaration) declaration).isGlobal()) {
					results.add(function);
				}
			}
		}
		return Collections.unmodifiableList(results);
	}

	/**
	 * set the module declaration
	 */
	public void setLuaModuleDeclaration(LuaModuleDeclaration luamodule) {
		module = luamodule;
	}

	/**
	 * @return get the module declaration (could be null)
	 */
	public LuaModuleDeclaration getLuaModuleDeclaration() {
		return module;
	}

	/**
	 * @see org.eclipse.dltk.ast.ASTNode#traverse(org.eclipse.dltk.ast.ASTVisitor)
	 */
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			// traverse module declaration
			if (module != null)
				module.traverse(visitor);

			// traverse declarations
			for (Declaration declaration : declarations) {
				declaration.traverse(visitor);
			}
			visitor.endvisit(this);
		}
	}

	/**
	 * @see org.eclipse.dltk.ast.statements.Statement#getKind()
	 */
	@Override
	public int getKind() {
		return 0;
	}
}
