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
 * @date $Date$
 * $Author$
 * $Id$
 */
package org.eclipse.koneki.ldt.parser;

import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.compiler.IElementRequestor;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.SourceElementRequestVisitor;
import org.eclipse.dltk.compiler.IElementRequestor.FieldInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class LuaSourceElementRequestor.
 */
public class LuaSourceElementRequestor extends SourceElementRequestVisitor {

	public LuaSourceElementRequestor(ISourceElementRequestor requesor) {
		super(requesor);
	}

	@Override
	public boolean endvisit(Statement statement) throws Exception {
		if (statement instanceof FieldDeclaration) {
			getRequestor().exitField(statement.sourceEnd());
		}
		return super.endvisit(statement);
	}

	public IElementRequestor getRequestor() {
		return this.fRequestor;
	}

	public boolean visit(FieldDeclaration f) throws Exception {
		FieldInfo field = new FieldInfo();
		field.declarationStart = f.sourceStart();
		field.modifiers = f.getModifiers();
		field.name = f.getName();
		field.nameSourceStart = f.getNameStart();
		field.nameSourceEnd = f.getNameEnd();
		getRequestor().enterField(field);
		return true;
	}

	@Override
	public boolean visit(Statement s) throws Exception {
		if (s instanceof FieldDeclaration) {
			return visit((FieldDeclaration) s);
		}
		return super.visit(s);
	}
}
