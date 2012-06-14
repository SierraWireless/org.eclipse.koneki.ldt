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

public class Identifier extends LuaExpression {

	private Item definition;

	public Identifier() {
		super();
	}

	public Item getDefinition() {
		return definition;
	}

	public void setDefinition(Item definition) {
		this.definition = definition;
	}

	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			visitor.endvisit(this);
		}
	}
}
