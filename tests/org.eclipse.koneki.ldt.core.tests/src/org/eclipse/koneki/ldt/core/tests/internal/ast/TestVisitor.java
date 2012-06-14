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
 * @date $Date: 2009-07-23 12:07:30 +0200 (jeu., 23 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: TestVisitor.java 2161 2009-07-23 10:07:30Z kkinfoo $
 */
package org.eclipse.koneki.ldt.core.tests.internal.ast;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParserFactory;
import org.eclipse.koneki.ldt.core.tests.internal.ast.utils.DummyReporter;
import org.eclipse.koneki.ldt.core.tests.internal.ast.utils.SpyVisitor;

/**
 * The Class TestVisitor checks how AST is can be walked through.
 */
public class TestVisitor extends TestCase {

	/** The visitor. */
	private ASTVisitor visitor;

	/** The reporter. */
	private IProblemReporter reporter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() {
		this.visitor = new SpyVisitor();
		this.reporter = new DummyReporter();
	}

	/**
	 * Test visitor.
	 */
	public void testVisitor() {
		String errorMessage = ""; //$NON-NLS-1$
		boolean success = true;
		ISourceParser parser = new LuaSourceParserFactory().createSourceParser();
		final String source = "for k = 1,20 do end"; //$NON-NLS-1$
		try {
			ModuleDeclaration parse = (ModuleDeclaration) parser.parse(new ModuleSource(source), reporter);
			parse.traverse(visitor);
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			success = false;
			if (e.getMessage() != null) {
				errorMessage = ": " + e.getMessage(); //$NON-NLS-1$
			}
			errorMessage = ". " + ((SpyVisitor) visitor).getErrorMessage(); //$NON-NLS-1$
		}
		assertTrue("Error while walking through AST" + errorMessage, success); //$NON-NLS-1$
	}
}
