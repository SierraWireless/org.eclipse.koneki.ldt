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
 * @date $Date: 2009-06-15 17:55:03 +0200 (lun., 15 juin 2009) $
 * $Author: kkinfoo $
 * $Id: Invoke.java 1841 2009-06-15 15:55:03Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.expressions;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.Reference;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class Invoke.
 */
public class Invoke extends Call implements LuaExpressionConstants {

	/** Invocation name in parser terms */
	private String string;

	/**
	 * Instantiates a new invoke.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param module
	 *            the module
	 */
	public Invoke(int start, int end, Expression module, String string) {
		this(start, end, module, string, new CallArgumentsList());
	}

	/**
	 * Instantiates a new invoke.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param module
	 *            the module
	 * @param parameters
	 *            the parameters
	 */
	public Invoke(int start, int end, Expression module, String string, CallArgumentsList parameters) {
		super(start, end, module, parameters);
		this.string = string;
	}

	@Override
	public int getKind() {
		return E_INVOKE;
	}

	/**
	 * Name of `Invocation node
	 * 
	 * @return {@linkplain Reference}
	 */
	public Reference getReference() {
		return new SimpleReference(string.sourceStart(), string.sourceEnd(), string.getValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anwrt.ldt.parser.ast.expressions.Call#traverse(org.eclipse.dltk.ast .ASTVisitor)
	 */
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			super.traverse(visitor);
			string.traverse(visitor);
			visitor.endvisit(this);
		}
	}
}
