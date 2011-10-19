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
package org.eclipse.koneki.ldt.parser.internal.modules.tests;

import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;
import org.eclipse.koneki.ldt.parser.ast.declarations.FunctionDeclaration;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.AbstractParserTest;

/**
 * Module Parsing Tests
 */
public class TestModules extends AbstractParserTest {

	/**
	 * parse module without any error
	 */
	public void testEmptyModule() {
		LuaSourceRoot root = parse("module(...)"); //$NON-NLS-1$
		assertNotNull("No module declaration found ", root.getDeclarationsContainer().getLuaModuleDeclaration()); //$NON-NLS-1$
	}

	/**
	 * parse module without any error
	 */
	public void testEmptyTableModule() {
		LuaSourceRoot root = parse("local M = {} return M"); //$NON-NLS-1$
		assertNotNull("No module declaration found ", root.getDeclarationsContainer().getLuaModuleDeclaration()); //$NON-NLS-1$
	}

	/**
	 * parse module with 1 function
	 */
	public void testModuleTableWithOneFunction() {
		LuaSourceRoot root = parse("local M = {} M.f1 = function () end return M"); //$NON-NLS-1$
		assertNotNull("No module declaration found", root.getDeclarationsContainer().getLuaModuleDeclaration()); //$NON-NLS-1$
		assertEquals(
				"Function not found : module function list empty.", 1, root.getDeclarationsContainer().getLuaModuleDeclaration().getMethods().length); //$NON-NLS-1$
		assertEquals(
				"Function not found : bad object type.", root.getDeclarationsContainer().getLuaModuleDeclaration().getMethods()[0].getClass(), FunctionDeclaration.class); //$NON-NLS-1$
		assertEquals(
				"Function not found : bad function name.", ((FunctionDeclaration) root.getDeclarationsContainer().getLuaModuleDeclaration().getMethods()[0]).getName(), "f1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * parse module with 1 function
	 */
	public void testModuleWithOneFunction() {
		LuaSourceRoot root = parse("module(...) function f1 () end"); //$NON-NLS-1$
		assertNotNull("No module declaration found ", root.getDeclarationsContainer().getLuaModuleDeclaration()); //$NON-NLS-1$
		assertEquals(
				"Function not found : module function list empty.", root.getDeclarationsContainer().getLuaModuleDeclaration().getMethods().length, 1); //$NON-NLS-1$
		assertEquals(
				"Function not found : bad object type.", root.getDeclarationsContainer().getLuaModuleDeclaration().getMethods()[0].getClass(), FunctionDeclaration.class); //$NON-NLS-1$
		assertEquals(
				"Function not found : bad function name.", ((FunctionDeclaration) root.getDeclarationsContainer().getLuaModuleDeclaration().getMethods()[0]).getName(), "f1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * parse module with any public function
	 */
	public void testModuleNoPublicFunction() {
		LuaSourceRoot root = parse("module(...) local function f1 () end"); //$NON-NLS-1$
		assertNotNull("No module declaration found", root.getDeclarationsContainer().getLuaModuleDeclaration()); //$NON-NLS-1$
		assertEquals("No public function could be found.", 0, root.getDeclarationsContainer().getLuaModuleDeclaration().getMethods().length); //$NON-NLS-1$
	}

	/**
	 * parse module with any public function
	 */
	public void testModuleOneGlobalFunction() {
		LuaSourceRoot root = parse("function f1 () end module(...)"); //$NON-NLS-1$
		assertNotNull("No module declaration found", root.getDeclarationsContainer().getLuaModuleDeclaration()); //$NON-NLS-1$
		assertEquals("No public function could be found.", 0, root.getDeclarationsContainer().getLuaModuleDeclaration().getMethods().length); //$NON-NLS-1$
	}
}
