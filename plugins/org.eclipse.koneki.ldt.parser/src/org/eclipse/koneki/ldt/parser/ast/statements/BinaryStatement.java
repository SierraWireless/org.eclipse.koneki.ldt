/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.parser.ast.statements;

import org.eclipse.koneki.ldt.parser.ast.expressions.BinaryExpression;

/**
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class BinaryStatement extends BinaryExpression {

	public BinaryStatement(int start, int end, Chunk left, int kind, Chunk right) {
		super(start, end, left, kind, right);
	}

	@Override
	public Chunk getLeft() {
		return (Chunk) super.getLeft();
	}

	@Override
	public Chunk getRight() {
		return (Chunk) super.getRight();
	}

}
