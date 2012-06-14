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
package org.eclipse.koneki.ldt.core.internal.ast.models.api;

import java.util.HashMap;

import org.eclipse.dltk.ast.ASTVisitor;

/**
 * Use to define a kind of type 'record'.
 */
public class RecordTypeDef extends TypeDef {

	private String name;
	private String documentation;
	private HashMap<String, Item> fields = new HashMap<String, Item>();

	public RecordTypeDef() {

	}

	/**
	 * @see org.eclipse.dltk.ast.ASTNode#traverse(org.eclipse.dltk.ast.ASTVisitor)
	 */
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			// traverse fields
			for (Item field : fields.values()) {
				field.traverse(visitor);
			}
			visitor.endvisit(this);
		}
	}

	public String getName() {
		return name;
	}

	public String getDocumentation() {
		return documentation;
	}

	public HashMap<String, Item> getFields() {
		return fields;
	}

	public void addField(final String fieldName, final Item item) {
		fields.put(fieldName, item);
		item.setParent(this);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String toReadableString() {
		return "#" + name; //$NON-NLS-1$
	}
}
