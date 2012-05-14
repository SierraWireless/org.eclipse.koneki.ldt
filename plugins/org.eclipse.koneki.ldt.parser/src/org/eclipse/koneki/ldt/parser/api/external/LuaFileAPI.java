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
package org.eclipse.koneki.ldt.parser.api.external;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.dltk.ast.ASTVisitor;

/**
 * This class defines the API of a Lua file.
 */
public class LuaFileAPI extends LuaASTNode {

	private String documentation;
	private HashMap<String, TypeDef> types = new HashMap<String, TypeDef>();
	private HashMap<String, Item> globalvars = new HashMap<String, Item>();
	private ArrayList<Return> returns = new ArrayList<Return>();

	public LuaFileAPI() {
	}

	public void addType(final String typeName, final TypeDef type) {
		types.put(typeName, type);
	}

	public void addGlobalVar(final String varName, final Item item) {
		globalvars.put(varName, item);
		item.setParent(this);
	}

	public void addReturns(final Return returnValue) {
		returns.add(returnValue);
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getDocumentation() {
		return documentation;
	}

	public HashMap<String, TypeDef> getTypes() {
		return types;
	}

	public HashMap<String, Item> getGlobalvars() {
		return globalvars;
	}

	public ArrayList<Return> getReturns() {
		return returns;
	}

	/**
	 * @see org.eclipse.dltk.ast.ASTNode#traverse(org.eclipse.dltk.ast.ASTVisitor)
	 */
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			// traverse types
			for (TypeDef typedef : types.values()) {
				typedef.traverse(visitor);
			}
			// traverse global vars
			for (Item globalvar : globalvars.values()) {
				globalvar.traverse(visitor);
			}
			visitor.endvisit(this);
		}
	}
}
