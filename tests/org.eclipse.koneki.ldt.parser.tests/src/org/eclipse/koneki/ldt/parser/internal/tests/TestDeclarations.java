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
package org.eclipse.koneki.ldt.parser.internal.tests;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.koneki.ldt.parser.LuaSourceParserFactory;
import org.eclipse.koneki.ldt.parser.ast.declarations.FunctionDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.TableDeclaration;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.DeclarationVisitor;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.DummyReporter;

/**
 * Checks AST inner declarations behavior
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class TestDeclarations extends TestCase {

	/**
	 * Assert there are no duplication of declaration nodes
	 */
	public void testFunctionDeclarationCount() {
		DeclarationVisitor visitor = null;
		try {
			visitor = parse("method = function() end"); //$NON-NLS-1$
		} catch (Exception e) {
			assertNotNull("Visitor not initialised", visitor); //$NON-NLS-1$
			return;
		}
		int declarationCount = visitor.getDeclarations().size();
		int functionDeclarationCount = visitor.getDeclarations(FunctionDeclaration.class).size();
		assertTrue("Unable to retrieve declaration.", declarationCount > 0); //$NON-NLS-1$
		assertEquals("Some declarations are not function ones.", functionDeclarationCount, declarationCount); //$NON-NLS-1$
	}

	/**
	 * Check that modifiers are properly set
	 */
	public void testPublicFunctionSetDeclarationModifiers() {
		DeclarationVisitor visitor = null;
		try {
			visitor = parse("method = function() end"); //$NON-NLS-1$
		} catch (Exception e) {
			assertNotNull("Visitor not initialised", visitor); //$NON-NLS-1$
			return;
		}
		assertTrue("No declarations found.", visitor.getDeclarations(FunctionDeclaration.class).size() > 0); //$NON-NLS-1$
		Declaration declaration = visitor.getDeclarations(FunctionDeclaration.class).get(0);
		assertTrue("Function should be considered as public.", declaration.isPublic()); //$NON-NLS-1$
		assertFalse("Function should not be considered as private.", declaration.isPrivate()); //$NON-NLS-1$
	}

	/**
	 * Check if status of function declaration in a local node is being considered properly
	 */
	public void testLocalFunctionSetDeclarationModifiers() {
		DeclarationVisitor visitor = null;
		try {
			visitor = parse("local method = function() end"); //$NON-NLS-1$
		} catch (Exception e) {
			assertNotNull("Visitor not initialised", visitor); //$NON-NLS-1$
			return;
		}
		assertTrue("No declarations found.", visitor.getDeclarations(FunctionDeclaration.class).size() > 0); //$NON-NLS-1$
		Declaration declaration = visitor.getDeclarations(FunctionDeclaration.class).get(0);
		assertFalse("Function should not be considered as public.", declaration.isPublic()); //$NON-NLS-1$
		assertTrue("Function should be considered as private.", declaration.isPrivate()); //$NON-NLS-1$
	}

	/**
	 * Check if status of table in a local node is being considered properly
	 */

	public void testLocalTableDeclarationModifiers() {
		DeclarationVisitor visitor = null;
		try {
			visitor = parse("local t={}"); //$NON-NLS-1$
		} catch (Exception e) {
			fail("Visitor not initialised"); //$NON-NLS-1$
		}
		try {
			Declaration declaration = visitor.getDeclarations(TableDeclaration.class).get(0);
			assertFalse("Table should not be considered as public.", declaration.isPublic()); //$NON-NLS-1$
			assertTrue("Table should be considered as private.", declaration.isPrivate()); //$NON-NLS-1$
		} catch (IndexOutOfBoundsException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Check if table fields are considered properly
	 */
	// TODO We are no longer dealing with table nested Declarations, ask users
	// if they want them back
	// public void testPublicTableFieldDeclaration() {
	// DeclarationVisitor visitor = null;
	// try {
	// visitor = parse("local t={field=nil}");
	// } catch (Exception e) {
	//			fail("Visitor not initialised"); //$NON-NLS-1$
	// }
	// // Retrieve fields from AST
	// List<Declaration> fields = visitor.getDeclarations(TableField.class);
	// assertTrue("No field declaration found.", fields.size() > 0);
	// assertEquals("Wrong field declaration count.", fields.size(), 1);
	// }

	/**
	 * Parses AST to extract declarations to test
	 * 
	 * @throws Exception
	 */
	private static DeclarationVisitor parse(String code) throws Exception {
		// Parse code
		DeclarationVisitor visitor = new DeclarationVisitor();
		DummyReporter reporter = new DummyReporter();
		ISourceParser parser = new LuaSourceParserFactory().createSourceParser();
		// LuaSourceParser parser = new AlternativeLuaSourceParser();

		// Extract declarations
		ModuleSource source = new ModuleSource("none", code);//$NON-NLS-1$
		ModuleDeclaration module = (ModuleDeclaration) parser.parse(source, reporter);
		module.traverse(visitor);
		return visitor;
	}
}
