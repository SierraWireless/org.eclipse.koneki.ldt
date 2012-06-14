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
 * $Id: TestExpressions.java 2161 2009-07-23 10:07:30Z kkinfoo $
 */
package org.eclipse.koneki.ldt.core.tests.internal.ast;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParser;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParserFactory;
import org.eclipse.koneki.ldt.core.tests.internal.ast.utils.DummyReporter;

/**
 * The Class TestExpressions, tests if {@linkplain LuaSourceParser} can handle every kind of {@linkplain Expression} that Lua offers.
 */
public class TestExpressions extends TestCase {

	/** The file name. */
	private static final String FILENAME = "none"; //$NON-NLS-1$

	/** The reporter. */
	private IProblemReporter reporter;

	/** The module. */
	private ModuleDeclaration module;

	private ModuleDeclaration parse(final String source) {
		ModuleSource input = new ModuleSource(FILENAME, source);
		return (ModuleDeclaration) new LuaSourceParserFactory().createSourceParser().parse(input, this.reporter);

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
	 * Test boolean false.
	 */
	public void testBooleanFalse() {
		module = parse("bool = false"); //$NON-NLS-1$
		assertFalse("False is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test boolean true.
	 */
	public void testBooleanTrue() {
		module = parse("bool = true");//$NON-NLS-1$
		assertFalse("True is not recognized.", module.isEmpty());//$NON-NLS-1$
	}

	/**
	 * Test call.
	 */
	public void testCall() {
		module = parse("method = function () end method()");//$NON-NLS-1$
		assertFalse("Call to function is not recognized.", module.isEmpty());//$NON-NLS-1$

		module = parse("withParam = function (foo, bar) end withParam(nil, nil)");//$NON-NLS-1$
		assertFalse("Call to function with parameters is not recognized.", module.isEmpty());//$NON-NLS-1$
	}

	/**
	 * Test dots.
	 */
	public void testDots() {
		module = parse("method = function (...) end method()");//$NON-NLS-1$
		assertFalse("Dots are not recognized.", module.isEmpty());//$NON-NLS-1$
	}

	/**
	 * Empty source code
	 */
	public void testEmptySource() {
		module = parse("");//$NON-NLS-1$
		assertFalse("Empy source not handled.", module.isEmpty());//$NON-NLS-1$
	}

	/**
	 * Test function.
	 */
	public void testFunction() {

		module = parse("method = function (var) return var +1 end"); //$NON-NLS-1$
		assertFalse("Function is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test index.
	 */
	public void testIndex() {

		module = parse("tab = {} tab[2]= 2"); //$NON-NLS-1$
		assertFalse("Numeric index is not handled.", module.isEmpty()); //$NON-NLS-1$

		module = parse("module = {} module.field= 2"); //$NON-NLS-1$
		assertFalse("Field-like index is not handled.", module.isEmpty()); //$NON-NLS-1$
	}

	public void testInvoke() {
		module = parse("module:method()"); //$NON-NLS-1$
		assertFalse("Simple invocation is not handled.", module.isEmpty()); //$NON-NLS-1$

		module = parse("module:table(arg)"); //$NON-NLS-1$
		assertFalse("Invocation with argument not handled.", module.isEmpty()); //$NON-NLS-1$

		module = parse("y=y(ii):w(ty).y"); //$NON-NLS-1$
		assertFalse("Imbricated invocation is not handled.", module.isEmpty()); //$NON-NLS-1$
	}

	public void testLength() {
		module = parse("var = #table"); //$NON-NLS-1$
		assertFalse("Length operator not handled.", module.isEmpty()); //$NON-NLS-1$

		module = parse("var = #{}"); //$NON-NLS-1$
		assertFalse("Length operator not handled.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test pair.
	 */
	public void testPair() {
		module = parse("dic = {[1] = 'one', two = 2}"); //$NON-NLS-1$
		assertFalse("Pair is not recognized.", module.isEmpty()); //$NON-NLS-1$
		module = parse("local subpath = 'path' local i = {[subpath] = value}"); //$NON-NLS-1$
		assertFalse("Pair with identifier key is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test nil.
	 */
	public void testNil() {
		module = parse("null = nil"); //$NON-NLS-1$
		assertFalse("Nil is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test number.
	 */
	public void testNumber() {
		module = parse("number = 6"); //$NON-NLS-1$
		assertFalse("Number is not recognized.", module.isEmpty()); //$NON-NLS-1$
		module = parse("local number = 6.0"); //$NON-NLS-1$
		assertFalse("Number is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test parenthesis.
	 */
	public void testParenthesis() {
		module = parse("paren = (1 + 2) * 5"); //$NON-NLS-1$
		assertFalse("Parenthesis is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test string.
	 */
	public void testString() {
		module = parse("string, another = 'string', [[anotherOne]]"); //$NON-NLS-1$
		assertFalse("String is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

	/**
	 * Test table.
	 */
	public void testTable() {
		module = parse("table = {1,'2'}"); //$NON-NLS-1$
		assertFalse("Table is not recognized.", module.isEmpty()); //$NON-NLS-1$
	}

}
