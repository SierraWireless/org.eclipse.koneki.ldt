/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.core.tests.internal.ast.error;

import java.text.MessageFormat;

import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemCollector;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParser;
import org.junit.Assert;
import org.junit.Test;

public class ErrorHandlingTestCase {
	private static final String NEW_LINE = System.getProperty("line.separator"); //$NON-NLS-1$

	private void parseAndCheckErrors(final String code) {
		parseAndCheckErrors(code, 1, code.length());
	}

	private void parseAndCheckErrors(final String code, final int line, final int offset) {
		parseAndCheckErrors(code, line, offset, -1);
	}

	private void parseAndCheckErrors(final String code, final int line, final int startOffset, final int endOffset) {

		// Parse
		final LuaSourceParser parser = new LuaSourceParser();
		final ProblemCollector reporter = new ProblemCollector();
		parser.parse(new ModuleSource(code), reporter);

		// Check if there is a problem
		if (reporter.isEmpty())
			Assert.fail(MessageFormat.format("No error found for:\n{0}", code)); //$NON-NLS-1$

		// Check if the problem seems valid
		final IProblem problem = reporter.getErrors().get(0);

		// Check line
		final int problemLineNumber = problem.getSourceLineNumber();
		if (problemLineNumber != line)
			Assert.fail(MessageFormat.format(
					"Reported error line is invalid: <{0}> expected, got <{1}>.\nGiven code is:\n{2}", line, problemLineNumber, code)); //$NON-NLS-1$

		// Check start
		final int problemSourceStart = problem.getSourceStart();
		if (startOffset != problemSourceStart)
			Assert.fail(MessageFormat.format("Reported error start offset is invalid: <{0}> expected, got <{1}>.\nGiven code is:\n{2}",//$NON-NLS-1$	
					startOffset, problemSourceStart, code));

		// Check end
		final int problemSourceEnd = problem.getSourceEnd();
		if (endOffset != problemSourceEnd)
			Assert.fail(MessageFormat.format(
					"Reported error end offset is invalid: <{0}> expected, got <{1}>.\nGiven code is:\n{2}", endOffset, problemSourceEnd, code)); //$NON-NLS-1$		
	}

	@Test
	public void testIncompleteStatement() {
		final String[] statements = { "do", //$NON-NLS-1$ 
				"else",//$NON-NLS-1$ 
				"end", //$NON-NLS-1$ 
				"for", //$NON-NLS-1$ 
				"function", //$NON-NLS-1$ 
				"if", //$NON-NLS-1$ 
				"local", //$NON-NLS-1$ 
				"repeat", //$NON-NLS-1$ 
				"then", //$NON-NLS-1$ 
				"while" //$NON-NLS-1$ 
		};
		for (final String code : statements)
			parseAndCheckErrors(code);
	}

	@Test
	public void testSyntaxErrorAfterBlankLines() {
		final StringBuilder sb = new StringBuilder();
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("local "); //$NON-NLS-1$
		parseAndCheckErrors(sb.toString(), 3, sb.length());
	}

	@Test
	public void testSyntaxErrorAtStart() {
		final StringBuilder sb = new StringBuilder();
		sb.append("x"); //$NON-NLS-1$
		sb.append(NEW_LINE);
		sb.append("return nil"); //$NON-NLS-1$
		parseAndCheckErrors(sb.toString(), 3, 13, sb.length());
	}

	@Test
	public void testSyntaxErrorSurroundedByValidCode() {
		final StringBuilder sb = new StringBuilder();
		sb.append("function n(x)"); //$NON-NLS-1$
		sb.append(NEW_LINE);
		sb.append("x"); //$NON-NLS-1$
		sb.append(NEW_LINE);
		sb.append("end"); //$NON-NLS-1$
		parseAndCheckErrors(sb.toString(), 3, 13, sb.length());
	}

	@Test
	public void testWrongExperssion() {
		final StringBuilder wrongFunction = new StringBuilder();
		wrongFunction.append("function n()");//$NON-NLS-1$
		wrongFunction.append(NEW_LINE);
		wrongFunction.append("return x x");//$NON-NLS-1$
		wrongFunction.append(NEW_LINE);
		wrongFunction.append("end"); //$NON-NLS-1$
		parseAndCheckErrors(wrongFunction.toString(), 2, 33, wrongFunction.length());
	}

	@Test
	public void testWrongStatements() {

		final String wrongFunction = "function n(x x)end"; //$NON-NLS-1$
		parseAndCheckErrors(wrongFunction, 1, 13, wrongFunction.length());

		parseAndCheckErrors("local ="); //$NON-NLS-1$

		final String wrongForLoop = "for _,_ ine x do end"; //$NON-NLS-1$
		parseAndCheckErrors(wrongForLoop, 1, 11, wrongForLoop.length());
	}
}
