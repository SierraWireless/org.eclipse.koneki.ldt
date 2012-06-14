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
package org.eclipse.koneki.ldt.core.internal.ast.models.file;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Item;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;

public class LuaInternalContent extends LuaASTNode {
	private List<Item> unknownglovalvars = new ArrayList<Item>();
	private Block content;

	public LuaInternalContent() {
		content = new Block();
	}

	public Block getContent() {
		return content;
	}

	public void setContent(final Block content) {
		this.content = content;
	}

	public List<Item> getUnknownglovalvars() {
		return unknownglovalvars;
	}

	public void addUnknownglobalvar(final Item item) {
		unknownglovalvars.add(item);
		item.setParent(this);
	}

	@Override
	public void traverse(final ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			// traverse block
			content.traverse(visitor);

			visitor.endvisit(this);
		}
	}
}
