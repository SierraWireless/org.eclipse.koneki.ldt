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
 * $Id: TestLuaBinaryOperations.java 2161 2009-07-23 10:07:30Z kkinfoo $
 */
package org.eclipse.koneki.ldt.core.tests.internal.ast;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParserFactory;
import org.eclipse.koneki.ldt.core.tests.internal.ast.utils.DummyReporter;

/**
 * The Class TestLuaBinaryOperations. Aims to test implementation of Lua binary operations in the parser.
 */
public class TestLuaBinaryOperations extends TestCase {

	/** The file name. */
	private static final String FILENAME = "none"; //$NON-NLS-1$

	/** The reporter. */
	private IProblemReporter reporter;

	private ModuleDeclaration parse(final String source) {
		ModuleSource module = new ModuleSource(FILENAME, source);
		return (ModuleDeclaration) new LuaSourceParserFactory().createSourceParser().parse(module, this.reporter);
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
	 * Test addition.
	 */
	public void testAddition() {
		ModuleDeclaration module = parse("add = 1 + 2"); //$NON-NLS-1$
		assertFalse("Addition is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test and.
	 */
	public void testAnd() {
		ModuleDeclaration module = parse("_and = true and false"); //$NON-NLS-1$
		assertFalse("Logical and is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test concatenation.
	 */
	public void testConcatenation() {
		ModuleDeclaration module = parse("concat = 'string' .. [[another]]"); //$NON-NLS-1$
		assertFalse("Concatenation is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test division.
	 */
	public void testDivision() {
		ModuleDeclaration module = parse("div = 1 / 2"); //$NON-NLS-1$
		assertFalse("Division is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test equality.
	 */
	public void testEquality() {
		ModuleDeclaration module = parse("eq = 1 == 2"); //$NON-NLS-1$
		assertFalse("Equality is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test modulo.
	 */
	public void testModulo() {
		ModuleDeclaration module = parse("mod = 1 % 2"); //$NON-NLS-1$
		assertFalse("Modulo is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test multiplication.
	 */
	public void testMultiplication() {
		ModuleDeclaration module = parse("mul = 1 * 2"); //$NON-NLS-1$
		assertFalse("Multiplication is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test lighter.
	 */
	public void testLighter() {
		ModuleDeclaration module = parse("lt = 1 < 2"); //$NON-NLS-1$
		assertFalse("Lighter than is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test lighter or equal.
	 */
	public void testLighterOrEqual() {
		ModuleDeclaration module = parse("le = 1 <= 2"); //$NON-NLS-1$
		assertFalse("Lighter than or equal is not supported.", module.isEmpty()); //$NON-NLS-1$;
	}

	/**
	 * Test or.
	 */
	public void testOr() {
		ModuleDeclaration module = parse("_or = true or false"); //$NON-NLS-1$
		assertFalse("Logical or is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test power.
	 */
	public void testPower() {
		ModuleDeclaration module = parse("pow = 1 + 2"); //$NON-NLS-1$
		assertFalse("Power raise is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test subtraction.
	 */
	public void testSubtraction() {
		ModuleDeclaration module = parse("sub = 1 - 2"); //$NON-NLS-1$
		assertFalse("Subtraction is not supported.", module.isEmpty()); //$NON-NLS-1$
	}

}
