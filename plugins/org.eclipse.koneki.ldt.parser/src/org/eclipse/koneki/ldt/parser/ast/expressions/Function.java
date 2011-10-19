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
package org.eclipse.koneki.ldt.parser.ast.expressions;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.statements.Block;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;
import org.eclipse.koneki.ldt.parser.ast.declarations.FunctionDeclaration;
import org.eclipse.koneki.ldt.parser.ast.statements.Chunk;

/**
 * The Class Function represent a function node of an DLTK AST. This node is used to describe a function definition. Even so, is not interpreted by
 * DLTK as a regular function declaration. Therefore this function node needs to be wrapped in a {@link FunctionDeclaration} in order to appear in
 * DLTK tooling.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class Function extends Chunk {

	/** The parameters are in this raw chunk. */
	private Chunk args;
	private long id;

	/**
	 * Instantiates a new function.
	 * 
	 * @param start
	 *            start offset of function's body
	 * @param end
	 *            end offset of function's body
	 * @param parameters
	 *            raw function's parameters in a {@link Chunk}
	 * @param body
	 *            function's body, must be a {@link Block}
	 */
	public Function(int start, int end, Chunk parameters, Chunk body) {
		super(start, end, body.getStatements());
		this.args = parameters;
	}

	/**
	 * Function's raw arguments in a {@link Chunk}
	 * 
	 * @return {@link Chunk} contains function's arguments, they need to be parsed and registered as {@link Argument} in a {@link FunctionDeclaration}
	 */
	public Chunk getArguments() {
		return args;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ast.statements.Block#getKind()
	 */
	@Override
	public int getKind() {
		return LuaExpressionConstants.E_FUNCTION;
	}

	public long getID() {
		return id;
	}

	public void printNode(CorePrinter output) {
		// Arguments
		getArguments().printNode(output);
		output.indent();
		super.printNode(output);
		output.dedent();
	}

	public void setID(long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.dltk.ast.statements.Block#traverse(org.eclipse.dltk.ast. ASTVisitor)
	 */
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			super.traverse(visitor);
			getArguments().traverse(visitor);
			visitor.endvisit(this);
		}
	}
}
