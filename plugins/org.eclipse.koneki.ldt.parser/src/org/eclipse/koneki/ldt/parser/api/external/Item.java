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
import java.util.List;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.koneki.ldt.internal.parser.IDocumentationHolder;
import org.eclipse.koneki.ldt.parser.ast.Identifier;

/**
 * An item is an element which references a type.<br/>
 * It could be use to define a global var or a field in a record type.
 */
public class Item extends LuaASTNode implements IDocumentationHolder {
	private String name;
	private String documentation;
	private TypeRef type;
	private List<Identifier> occurrences = new ArrayList<Identifier>();
	private LuaASTNode parent;

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(LuaASTNode parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getDocumentation() {
		return documentation;
	}

	public TypeRef getType() {
		return type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public void setType(TypeRef type) {
		this.type = type;
	}

	public void addOccurrence(Identifier identifier) {
		occurrences.add(identifier);
		identifier.setDefinition(this);
	}

	public List<Identifier> getOccurrences() {
		return occurrences;
	}

	public LuaASTNode getParent() {
		return parent;
	}

	/**
	 * @see org.eclipse.dltk.ast.ASTNode#traverse(org.eclipse.dltk.ast.ASTVisitor)
	 */
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			visitor.endvisit(this);
		}
	}

}
