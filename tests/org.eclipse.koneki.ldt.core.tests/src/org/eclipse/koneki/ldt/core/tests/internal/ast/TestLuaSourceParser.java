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
 * $Id: TestLuaSourceParser.java 2161 2009-07-23 10:07:30Z kkinfoo $
 */
package org.eclipse.koneki.ldt.core.tests.internal.ast;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParserFactory;
import org.eclipse.koneki.ldt.core.tests.internal.ast.utils.DummyReporter;

/**
 * The Class TestLuaSourceParser. Is the privileged entrance of the package. Allows to generate AST from source from a file or even straight from
 * source code.
 */
public class TestLuaSourceParser extends TestCase {

	/** The reporter. */
	private IProblemReporter reporter;

	/** The file name. */
	private String fileName = "none"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() {
		// Dummy problem reporter
		this.reporter = new DummyReporter();
	}

	/**
	 * Test empty chunk.
	 */
	public void testEmptyChunk() {
		ModuleSource source = new ModuleSource(fileName, ""); //$NON-NLS-1$
		ModuleDeclaration module = (ModuleDeclaration) new LuaSourceParserFactory().createSourceParser().parse(source, this.reporter);
		assertFalse("AST should contain at least an empty chunk", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test single assignment.
	 */
	public void testSingleAssignment() {
		// Single assignment
		ModuleSource source = new ModuleSource(fileName, "var = 1 + 2 * 3"); //$NON-NLS-1$
		ModuleDeclaration module = (ModuleDeclaration) new LuaSourceParserFactory().createSourceParser().parse(source, this.reporter);
		assertFalse("Valid code provides empty AST", module.isEmpty());//$NON-NLS-1$
	}
}
