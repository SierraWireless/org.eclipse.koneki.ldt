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

import org.eclipse.koneki.ldt.parser.ast.LuaExpression;

public class ExprTypeRef extends LazyTypeRef {

	private LuaExpression expression;
	private int returnPosition;

	public ExprTypeRef(int returnPosition) {
		super();
		this.expression = null;
		this.returnPosition = returnPosition;
	}

	public LuaExpression getExpression() {
		return expression;
	}

	public int getReturnPosition() {
		return returnPosition;
	}

	public void setExpression(LuaExpression expression) {
		this.expression = expression;
	}

}
