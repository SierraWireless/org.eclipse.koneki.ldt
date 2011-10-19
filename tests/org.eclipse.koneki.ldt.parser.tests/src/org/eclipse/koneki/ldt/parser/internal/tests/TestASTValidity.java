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

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.koneki.ldt.parser.Activator;
import org.eclipse.koneki.ldt.parser.LuaSourceParser;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.DummyReporter;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.SpyVisitor;

/**
 * Put AST reliability to the test. Checks if function declarations in AST match
 * declarations on code.
 * 
 * @author kkinfoo
 * 
 */
public class TestASTValidity extends TestCase {

	private SpyVisitor visitor;
	private LuaSourceParser parser;
	private ModuleDeclaration ast;
	private String error = null;

	// Initialize packages names
	private static String _AST;
	private static String _DECLARATION;
	private static String _EXPRESSION;
	private static String _STATEMENT;
	static {
		// Retrieve package name
		_AST = Activator.class.getName();
		_AST = _AST.substring(0, _AST.lastIndexOf('.')) + ".ast.";

		// Compose sub packages names
		_DECLARATION = _AST + "declarations";
		_EXPRESSION = _AST + "expressions";
		_STATEMENT = _AST + "statements";
	};

	/** Gather errors that occurs during parsing. */
	private String getError() {
		return error == null ? new String() : error;
	}

	public void setUp() {
		visitor = new SpyVisitor();
		parser = new LuaSourceParser();
	}

	/** Checks if visitor is fairly cleared. */
	public void testClear() {

		// Parse a local variable declaration
		String code = "local var";
		String typeName = _STATEMENT + ".Local";
		assertTrue(code + "\n" + getError(), traverse(code));
		boolean assertion = visitor.hasVisitedType(typeName);
		assertTrue("Unable to locate: " + typeName, assertion);

		// Verify that previous parsing has been deleted
		visitor.clear();
		assertion = visitor.hasVisitedType(typeName);
		assertFalse("Able to locate: " + typeName, assertion);
	}

	public void testFor() {

		// Regular numeric for
		// `Fornum{ `Id "i", `Number 1, `Number 10, { } }
		String code = "for i=1,10 do end";
		assertTrue(code + "\n" + getError(), traverse(code));

		// Id: 1
		String typeName = _EXPRESSION + ".Identifier";
		int count = visitor.typeCount(typeName);
		assertEquals("Wrong count of " + typeName, 1, count);

		// Number: 2
		typeName = _EXPRESSION + ".Number";
		count = visitor.typeCount(typeName);
		assertEquals("Wrong count of " + typeName, 2, count);

		// Numeric For: 1
		typeName = _STATEMENT + ".ForNumeric";
		count = visitor.typeCount(typeName);
		assertEquals("Wrong count of " + typeName, 1, count);

		// Same with step indication
		code = "for i=1,10,2 do end";
		assertTrue(code + "\n" + getError(), traverse(code));

		// Id: 1
		typeName = _EXPRESSION + ".Identifier";
		count = visitor.typeCount(typeName);
		assertEquals("Wrong count of " + typeName, 1, count);

		// Number: 3
		typeName = _EXPRESSION + ".Number";
		count = visitor.typeCount(typeName);
		assertEquals("Wrong count of " + typeName, 3, count);

		// Numeric For: 1
		typeName = _STATEMENT + ".ForNumeric";
		count = visitor.typeCount(typeName);
		assertEquals("Wrong count of " + typeName, 1, count);

	}

	/**
	 * Indicates if a function declaration is considered only once in AST, as a
	 * function and as a function declaration.
	 */
	public void testFunction() {

		// Check function declaration
		String code = "m = function ()end";

		// Function declaration: 1
		String typeName = _DECLARATION + ".FunctionDeclaration";
		assertTrue(code + "\n" + getError(), traverse(code));
		assertTrue("Unable to find required type.", visitor
				.hasVisitedType(typeName));
		assertEquals("Wrong declaration count.", 1, visitor.typeCount(typeName));
	}

	/**
	 * Check in pair function declaration {@code table = 'method', function
	 * ()end} .
	 */
	public void testFunctionInIndex() {

		// Check function declaration
		String code = "table['method']=function()end";
		boolean traverseStatus = traverse(code);
		assertTrue(code + "\n" + getError(), traverseStatus);

		// The AST for the code "table['method'] = function ()end" is:
		// `Set{ { `Index{ `Id "table", `String "method" } },
		// { `Function{ { }, { } } } }

		/*
		 * Count check
		 */
		// Set: 1
		String typeName = _STATEMENT + ".Set";
		int typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong assignement count.", 1, typeCount);

		// Index: 1
		typeName = _EXPRESSION + ".Index";
		typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong table count.", 1, typeCount);

		// Id: 1
		typeName = _EXPRESSION + ".Identifier";
		typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong identifier count.", 1, typeCount);

		// Function declaration: 1
		typeName = _DECLARATION + ".FunctionDeclaration";
		typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong declaration function count.", 1, typeCount);
		assertTrue("Unable to find required type.", visitor
				.hasVisitedType(typeName));
	}

	/**
	 * Check in pair function declaration {@code table = 'method', function
	 * ()end} .
	 */
	public void testFunctionInPair() {

		// Check function declaration
		String code = "t ={method=function()end}";
		boolean traverseStatus = traverse(code);
		assertTrue(code + "\n" + getError(), traverseStatus);

		// The AST for the code "t ={method=function()end}" is:
		// `Set{ { `Id "t" },
		// { `Table{ `Pair{ `String "method",
		// `Function{ { }, { } } } } } }

		/*
		 * Count check
		 */
		// Set: 1
		String typeName = _STATEMENT + ".Set";
		int typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong assignement count.", 1, typeCount);

		// Index: 1
		typeName = _EXPRESSION + ".Pair";
		typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong table count.", 1, typeCount);

		// Id: 1
		typeName = _EXPRESSION + ".Identifier";
		typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong identifier count.", 1, typeCount);

		// Function declaration: 1
		typeName = _DECLARATION + ".FunctionDeclaration";
		typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong declaration function count.", 1, typeCount);
		assertTrue("Unable to find required type.", visitor
				.hasVisitedType(typeName));
	}

	/**
	 * Check composed function declarations {@code first=function()
	 * second=function() end return second end}
	 */
	public void testImbricatedFuntionDeclarations() {
		String code = "first=function() local second=function() end end";
		// Function declarations: 2
		String typeName = _DECLARATION + ".FunctionDeclaration";
		assertTrue(code + "\n" + getError(), traverse(code));
		assertTrue("Unable to find required type.", visitor
				.hasVisitedType(typeName));
		assertEquals("Wrong declaration count.", 2, visitor.typeCount(typeName));
	}

	public void testLocalFunction() {
		String code = "local f = function() end";
		boolean traverseStatus = traverse(code);
		assertTrue(code + "\n" + getError(), traverseStatus);

		// Declaration : 1
		String typeName = _DECLARATION + ".FunctionDeclaration";
		int typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong declaration function count.", 1, typeCount);
		assertTrue(code + "\n" + getError(), traverse(code));
		assertTrue("Unable to find required type.", visitor
				.hasVisitedType(typeName));
		assertEquals("Wrong declaration count.", 1, visitor.typeCount(typeName));
		// // Function: 2
		// typeName = _EXPRESSION + ".Function";
		// typeCount = visitor.typeCount(typeName);
		// assertEquals("Wrong function count.", 1, typeCount);
	}

	public void testLocalRecursionFunction() {
		String code = "local function f() end";
		boolean traverseStatus = traverse(code);
		assertTrue(code + "\n" + getError(), traverseStatus);

		// Declaration : 1
		String typeName = _DECLARATION + ".FunctionDeclaration";
		int typeCount = visitor.typeCount(typeName);
		assertEquals("Wrong declaration function count.", 1, typeCount);
		assertTrue(code + "\n" + getError(), traverse(code));
		assertTrue("Unable to find required type.", visitor
				.hasVisitedType(typeName));
		assertEquals("Wrong declaration count.", 1, visitor.typeCount(typeName));
		// Function: 2
		// typeName = _EXPRESSION + ".Function";
		// typeCount = visitor.typeCount(typeName);
		// assertEquals("Wrong function count.", 1, typeCount);
	}

	/**
	 * Targets to verify if there is only one Set node in AST while parsing a
	 * single assignment
	 */
	public void testSet() {
		// Here is the Metalua AST for the following code:
		// `Set{ { `Id "m" }, { `Number 1 } }
		String code = "m = 1";
		boolean traverseStatus = traverse(code);
		assertTrue(code + "\n" + getError(), traverseStatus);

		// Try to find type and type count for every node type
		String[] typeNames = { _EXPRESSION + ".Number",
				_EXPRESSION + ".Identifier", _STATEMENT + ".Set" };
		int[] expectedCount = { 1, 1, 1 };
		assertTrue(typeNames.length == expectedCount.length);
		for (int k = 0; k < typeNames.length; k++) {
			int currentTypeCount = visitor.typeCount(typeNames[k]);
			boolean encountredType = visitor.hasVisitedType(typeNames[k]);
			assertTrue("Unable to find " + typeNames[k], encountredType);
			assertEquals("Wrong count for " + typeNames[k], expectedCount[k],
					currentTypeCount);
		}
	}

	/** Check if several function declaration is handled. */
	public void testSeveralFunction() {
		String code = "m = function () function l() end end";
		String typeName = _DECLARATION + ".FunctionDeclaration";
		assertTrue(code + "\n" + getError(), traverse(code));
		assertTrue("Unable to find required type.", visitor
				.hasVisitedType(typeName));
		assertEquals("Wrong declaration count.", 2, visitor.typeCount(typeName));
	}

	/**
	 * Traverse ASTs nodes
	 * 
	 * @param String
	 *            code The AST is created from this Lua code
	 * @return true on
	 */
	private boolean traverse(String code) {

		// Try to run visitor in AST
		try {
			assertNotNull("Valid string is required.", code);
			char[] fileName = (getClass() + ".java").toCharArray();
			ast = parser.parse(fileName, code.toCharArray(),
					new DummyReporter());

			// Forget previous parsing
			visitor.clear();
			ast.traverse(visitor);
		} catch (Exception e) {
			// If parsing fails bear the reason in mind
			error = e.getMessage();
			return false;
		}
		return true;
	}
}
