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
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;
import org.eclipse.koneki.ldt.parser.internal.IndexedNode;

/**
 * The Class Index represents a couple of {@linkplain Expression}s. As instance,
 * in statement <code>table.field = nil</code> Metalua sees
 * <code>`Set{ { `Index{ `Id "table", `String "field" } }, { `Nil } }</code>.
 * So, the node <code>table.field</code> is represented by an index node.
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class Index extends Identifier implements LeftHandSide,
		LuaExpressionConstants, IndexedNode {

	private long id;

	/** Left side of index */
	private Expression container;

	/**
	 * Instantiates a new index node, start and end offsets are computed from
	 * nodes provided at instantiation.
	 * 
	 * @param key
	 *            {@linkplain Expression} on left side
	 * @param value
	 *            {@linkplain Declaration} on right side
	 */
	public Index(Expression key, Declaration value) {
		this(key, (Statement) value);
	}

	/**
	 * Instantiates a new index node, start and end offsets are computed from
	 * nodes provided at instantiation.
	 * 
	 * @param key
	 *            {@linkplain Expression} on left side
	 * @param value
	 *            {@linkplain Expression} on right side
	 */
	public Index(Expression key, Expression value) {
		this(key, (Statement) value);
	}

	/**
	 * Instantiates a new index node, start and end offsets are computed from
	 * nodes provided at instantiation. This method is for internal use, because
	 * implementation is focus to use mainly {@linkplain Declaration} and final
	 * {@linkplain Expression}s as {@linkplain Literal}s
	 * 
	 * @param key
	 *            {@linkplain Expression} on left side
	 * @param value
	 *            {@linkplain Statement} on right side
	 */
	private Index(Expression key, Statement value) {
		super(value.sourceStart(), value.sourceEnd(), statementToString(value));
		this.container = key;
	}

	/**
	 * Depending in the type of statement, data about the name is not stored in
	 * the same place. This method is for finding valid names in
	 * {@linkplain Declaration}s and {@linkplain Literal}s. In case of other
	 * types, default behavior is to use regular {@link #toString()}
	 * 
	 * @param statement
	 *            {@linkplain Statement} node where we'll seek for a name
	 * @return {@linkplain String} Logic name of the node
	 */
	private static java.lang.String statementToString(Statement statement) {
		if (statement instanceof Declaration) {
			return ((Declaration) statement).getName();
		} else if (statement instanceof Literal) {
			return ((Literal) statement).getValue();
		}
		return statement.toString();
	}

	/**
	 * As mentioned in header, an `Index is just a composition of
	 * {@linkplain Expression}s. Left side of this expression can be useful to
	 * link semantic of right side to another logic unit.
	 * 
	 * @return {@linkplain Expression}, left side of Index
	 */
	public Expression getContainer() {
		return container;
	}

	public long getID() {
		return id;
	}

	@Override
	public int getKind() {
		return E_INDEX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anwrt.ldt.parser.ast.expressions.LeftHandSide#isLeftHandSide()
	 */
	@Override
	public boolean isLeftHandSide() {
		return true;
	}

	public void setID(long id) {
		this.id = id;
	}

	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			super.traverse(visitor);
			this.container.traverse(visitor);
			visitor.endvisit(this);
		}
	}
}
