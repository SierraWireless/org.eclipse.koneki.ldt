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

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Item;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;

public class LocalVar extends LuaASTNode {
	private Item var;
	private int scopeMinOffset;
	private int scopeMaxOffset;

	public LocalVar(Item var, int scopeMinOffset, int scopeMaxOffset) {
		this.var = var;
		this.scopeMinOffset = scopeMinOffset;
		this.scopeMaxOffset = scopeMaxOffset;
	}

	public Item getVar() {
		return var;
	}

	public int getScopeMinOffset() {
		return scopeMinOffset;
	}

	public int getScopeMaxOffset() {
		return scopeMaxOffset;
	}

	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			var.traverse(visitor);
			visitor.endvisit(this);
		}

	}
}
