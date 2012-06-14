/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.core.tests.internal.ast.utils;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaSourceRoot;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParserFactory;

/**
 * Test case with utility method to help to test lua parser
 */
public class AbstractParserTest extends TestCase {

	/**
	 * Parses code.
	 * 
	 * @return a luaModuleDeclaration or fails
	 */
	protected LuaSourceRoot parse(String code, IProblemReporter reporter) {
		// create parser
		ISourceParser parser = new LuaSourceParserFactory().createSourceParser();

		// create module from code.
		ModuleSource source = new ModuleSource("none", code);//$NON-NLS-1$

		// get lua module declaration
		ModuleDeclaration module = (ModuleDeclaration) parser.parse(source, reporter);
		assertEquals(module.getClass(), LuaSourceRoot.class);

		return (LuaSourceRoot) module;
	}

	/**
	 * Parses code. fails if any problems is reported
	 * 
	 * @return a luaModuleDeclaration or fails
	 */
	protected LuaSourceRoot parse(String code) {
		DummyReporter reporter = new DummyReporter();
		LuaSourceRoot module = parse(code, reporter);
		assertTrue(reporter.getProblems().isEmpty());
		return module;
	}
}
