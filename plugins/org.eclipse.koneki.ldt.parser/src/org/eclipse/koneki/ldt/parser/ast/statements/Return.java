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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.statements.Statement;

// TODO: Auto-generated Javadoc
/**
 * The Class Return.
 */
public class Return extends Statement {

	/** The expressions contained in return {@link Statement} */
	private Chunk returnValues;

    /**
	 * Instantiates a new return.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param values
	 *            {@link Statement}
	 */
	public Return(int start, int end, List<Statement> values) {
		super(start, end);
		this.returnValues = new Chunk(start, end, values);
    }

    /**
     * Instantiates a new return.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     */
    public Return(int start, int end) {
		this(start, end, new ArrayList<Statement>());
    }

    /**
	 * Adds one of {@link Return} values
	 * 
	 * @param value
	 *            Statement contained in {@link Return}
	 * 
	 * @return true, if successful
	 */
	public void addReturnValue(Statement value) {
		this.returnValues.addStatement(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.dltk.ast.statements.Statement#getKind()
     */
    @Override
    public int getKind() {
		return LuaStatementConstants.S_RETURN;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.dltk.ast.statements.Statement#traverse(org.eclipse.dltk.ast
     * .ASTVisitor)
     */
    public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			super.traverse(visitor);
			returnValues.traverse(visitor);
			visitor.endvisit(this);
		}
    }
}
