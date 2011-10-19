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
 * @date $Date: 2009-07-17 14:29:28 +0200 (ven., 17 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: LogicalValue.java 2111 2009-07-17 12:29:28Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.expressions;

import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.koneki.ldt.parser.internal.IndexedNode;

/**
 * The Class LogicalValue.
 */
public class LogicalValue extends Expression implements IndexedNode {

    /** The kind. */
    private int kind;
    private long id;

    /**
     * Instantiates a new logical value.
     * 
     * @param int start the start
     * @param int end the end
     * @param int kind the kind
     */
    public LogicalValue(int start, int end, int kind) {
	super(start, end);
	this.kind = kind;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.dltk.ast.statements.Statement#getKind()
     */
    @Override
    public int getKind() {
	return kind;
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
     * @see
     * org.eclipse.dltk.ast.statements.Statement#traverse(org.eclipse.dltk.ast
     * .ASTVisitor)
     */
    // public void traverse(ASTVisitor visitor) throws Exception {
    // if (visitor.visit(this)) {
    // visitor.endvisit(this);
    // }
    // }
}
