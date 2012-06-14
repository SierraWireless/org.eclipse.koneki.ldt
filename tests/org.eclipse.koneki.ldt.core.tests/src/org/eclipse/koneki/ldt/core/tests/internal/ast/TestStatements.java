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
 * $Id: TestStatements.java 2161 2009-07-23 10:07:30Z kkinfoo $
 */
package org.eclipse.koneki.ldt.core.tests.internal.ast;

import java.util.Random;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParserFactory;
import org.eclipse.koneki.ldt.core.tests.internal.ast.utils.DummyReporter;

/**
 * The Class TestStatements aims to check full coverage of Lua's key words. In order to do so it checks every kind of statements of the language in
 * order to ensure the parser handle them.
 */
public class TestStatements extends TestCase {

	/** The file name. */
	private static final String FILENAME = "none"; //$NON-NLS-1$

	/** The reporter. */
	private IProblemReporter reporter;

	/** The module. */
	private ModuleDeclaration module;

	private ModuleDeclaration parse(final String source) {
		ModuleSource moduleSource = new ModuleSource(FILENAME, source);
		return (ModuleDeclaration) new LuaSourceParserFactory().createSourceParser().parse(moduleSource, this.reporter);
	}

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
	 * Test assignments.
	 */
	public void testAssignments() {
		String source = "local i = 1"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Numeric assignement not handled.", module.isEmpty()); //$NON-NLS-1$

		source = "i = 1"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Numeric assignement not handled.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test break.
	 */
	public void testBreak() {

		String source = "do break end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Break statement is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test chunk.
	 */
	public void testChunk() {

		String source = ""; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Empty chunk is not recognized.", module.isEmpty()); //$NON-NLS-1$

		source = "do end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Explicit chunk is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test for.
	 */
	public void testFor() {

		String source = "for k=1,1 do end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("For statement is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test for each.
	 */
	public void testForEach() {

		String source = "for k,v in pairs({}) do end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("For statement is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test if.
	 */
	public void testIf() {

		String source = "if true then end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("If statement is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test if else.
	 */
	public void testIfElse() {

		String source = "if false then else end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("If statement is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test elseif.
	 */
	public void testIfElseIf() {

		String source = "i = 0 if i == 0 then return i elseif i > 1 then return i-1 end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("`elseif statement is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	public void testSeveralAssignments() {
		String source = "local i =1,function()end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Assignement with leftovers not handled.", module.isEmpty()); //$NON-NLS-1$

		source = "local i, method=1,function()end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Multiple assignement not handled.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test several elseif.
	 */
	public void testSeveralIfElseIf() {

		/*
		 * Generate chain of else if of variable length
		 */
		Random gen = new Random(196540427);
		int elseIfCount = gen.nextInt() % 20 + 1;
		StringBuilder elseIfChain = new StringBuilder();
		for (int k = 0; k < elseIfCount; k++) {
			elseIfChain.append("elseif i > 1 then return i-1 "); //$NON-NLS-1$
		}
		String source = "i = 0 if i == 0 then return i " + elseIfChain.toString() + "end"; //$NON-NLS-1$ //$NON-NLS-2$
		module = parse(source);
		assertFalse(elseIfCount + " `elseifIf in a row are not handled.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test local.
	 */
	public void testLocal() {

		String source = "local var"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Local declaration is not recognized.", module.isEmpty()); //$NON-NLS-1$ 

		source = "local var = 1"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Inititialisation of local declaration not handled.", module.isEmpty()); //$NON-NLS-1$ 
	}

	/**
	 * Test local recursion.
	 */
	public void testLocalRecursion() {

		String source = "local function f(var) return f(var+1) end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Local recursion declaration is not handled.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test repeat.
	 */
	public void testRepeat() {

		String source = "repeat break until( true )"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Repeat statement is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test return.
	 */
	public void testReturn() {

		String source = "function unicity() return 1 end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Single return is not recognized.", module.isEmpty()); //$NON-NLS-1$

		source = "function foo() return 1, 2 end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Multiple return is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test set.
	 */
	public void testSet() {

		// Single assignment
		String source = "set = 'up'"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Assignment is not recognized.", module.isEmpty()); //$NON-NLS-1$

		// Multiple assignment
		source = "set, stand = 'up', 'up'"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("Assignment is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test while.
	 */
	public void testWhile() {

		String source = "while( true ) do break end"; //$NON-NLS-1$
		module = parse(source);
		assertFalse("While statement is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

}
