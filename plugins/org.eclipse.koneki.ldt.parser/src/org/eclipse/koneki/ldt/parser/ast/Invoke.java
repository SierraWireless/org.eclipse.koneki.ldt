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
package org.eclipse.koneki.ldt.parser.ast;

import org.eclipse.dltk.ast.ASTVisitor;

public class Invoke extends LuaExpression {
	private LuaExpression record;
	private String functionName;

	public LuaExpression getRecord() {
		return record;
	}

	public void setRecord(final LuaExpression record) {
		this.record = record;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(final String functionName) {
		this.functionName = functionName;
	}

	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			record.traverse(visitor);
			visitor.endvisit(this);
		}
	}
}
