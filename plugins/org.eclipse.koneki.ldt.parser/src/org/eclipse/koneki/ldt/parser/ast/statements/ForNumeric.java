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

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-07-22 14:42:54 +0200 (mer., 22 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: ForNumeric.java 2151 2009-07-22 12:42:54Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.statements;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.statements.Block;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;
import org.eclipse.koneki.ldt.parser.internal.IndexedNode;

// TODO: Auto-generated Javadoc
/**
 * The Class ForNumeric.
 */
public class ForNumeric extends Block implements LuaStatementConstants, IndexedNode {

    /** The optionnal. */
    private Expression optionnal;

    /** The variable. */
    private Identifier variable;

    /** The from expression. */
    private Expression fromExpression;

    /** The to expression. */
    private Expression toExpression;

    private long id;

    /**
     * Instantiates a new for numeric.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     * @param variable
     *            the variable
     * @param from
     *            the from
     * @param to
     *            the to
     * @param optional
     *            the optional
     * @param chunk
     *            the chunk
     */
    public ForNumeric(int start, int end, Identifier variable, Expression from,
	    Expression to, Expression optional, Chunk chunk) {
	super(start, end, chunk.getStatements());
	this.fromExpression = from;
	this.toExpression = to;
	this.optionnal = optional;
	this.variable = variable;
    }

    /**
     * Instantiates a new for numeric.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     * @param variable
     *            the variable
     * @param from
     *            the from
     * @param to
     *            the to
     * @param chunk
     *            the chunk
     */
    public ForNumeric(int start, int end, Identifier variable, Expression from,
	    Expression to, Chunk chunk) {
	this(start, end, variable, from, to, null, chunk);
    }

    /**
     * Gets the optionnal.
     * 
     * @return the optionnal
     */
    public Expression getOptionnal() {
	return optionnal;
    }

    /**
     * Gets the variable.
     * 
     * @return the variable
     */
    public Identifier getVariable() {
	return variable;
    }

    /**
     * Gets the from expression.
     * 
     * @return the from expression
     */
    public Expression getFromExpression() {
	return fromExpression;
    }

    /**
     * Gets the to expression.
     * 
     * @return the to expression
     */
    public Expression getToExpression() {
	return toExpression;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.dltk.ast.statements.Block#getKind()
     */
    @Override
    public int getKind() {
	return S_FOR;
    }

    public long getID() {
	return id;
    }

    public void setID(long id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.dltk.ast.statements.Block#traverse(org.eclipse.dltk.ast.
     * ASTVisitor)
     */
    public void traverse(ASTVisitor visitor) throws Exception {
	if (visitor.visit(this)) {
	    super.traverse(visitor);
	    if (optionnal != null) {
		optionnal.traverse(visitor);
	    }
	    fromExpression.traverse(visitor);
	    toExpression.traverse(visitor);
	    variable.traverse(visitor);
	    visitor.endvisit(this);
	}
    }

}
