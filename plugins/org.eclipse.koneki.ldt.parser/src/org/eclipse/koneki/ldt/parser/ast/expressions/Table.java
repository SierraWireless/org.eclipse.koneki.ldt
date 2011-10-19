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
 * @date $Date: 2009-06-18 16:46:07 +0200 (jeu., 18 juin 2009) $
 * $Author: kkinfoo $
 * $Id: Table.java 1887 2009-06-18 14:46:07Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;
import org.eclipse.koneki.ldt.parser.ast.statements.Chunk;
import org.eclipse.koneki.ldt.parser.internal.IndexedNode;

// TODO: Auto-generated Javadoc
/**
 * The Class Table.
 */
public class Table extends Expression implements IndexedNode {

    /** The statements. */
    List<Statement> statements;
    private long id;

    /**
     * Instantiates a new table.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     * @param statements
     *            the statements
     */
    public Table(int start, int end, List<Statement> statements) {
	super(start, end);
	this.statements = statements;
    }

    /**
     * Instantiates a new table.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     */
    public Table(int start, int end) {
	this(start, end, new ArrayList<Statement>());
    }

    /**
     * Adds the statement.
     * 
     * @param statement
     *            the statement
     */
    public void addStatement(Statement statement) {
	statements.add(statement);
    }

    public Chunk getChunk() {
	int start = 0, end = 0;
	if (getStatements().size() > 0) {
	    start = getStatements().get(0).matchStart();
	    end = getStatements().get(getStatements().size() - 1).matchStart();
	}
	return new Chunk(start, end, getStatements());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.dltk.ast.statements.Statement#getKind()
     */
    @Override
    public int getKind() {
	return LuaExpressionConstants.E_TABLE;
    }

    public List<Statement> getStatements() {
	return statements;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.dltk.ast.statements.Statement#traverse(org.eclipse.dltk.ast
     * .ASTVisitor)
     */
    public void traverse(ASTVisitor pVisitor) throws Exception {
	if (pVisitor.visit(this)) {
	    super.traverse(pVisitor);
	    for (Statement node : getStatements()) {
		node.traverse(pVisitor);
	    }
	    pVisitor.endvisit(this);
	}
    }

    @Override
    public long getID() {
	return id;
    }

    @Override
    public void setID(long id) {
	this.id = id;
    }
}
