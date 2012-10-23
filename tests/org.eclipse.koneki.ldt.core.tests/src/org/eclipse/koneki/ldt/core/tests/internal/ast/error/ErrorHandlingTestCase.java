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
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	private IProblem parseAndCheckErrors(final String code) {

		// Parse
		final LuaSourceParser parser = new LuaSourceParser();
		final ProblemCollector reporter = new ProblemCollector();
		parser.parse(new ModuleSource(code), reporter);

		// Check if there is a problem
		Assert.assertFalse(MessageFormat.format("No error found for:\n{0}", code), reporter.isEmpty()); //$NON-NLS-1$

		// Check if the problem seems valid
		return reporter.getErrors().get(0);
	}

	private IProblem parseAndCheckErrors(final String code, final int line) {

		// Check line
		final IProblem problem = parseAndCheckErrors(code);
		Assert.assertEquals(MessageFormat.format("Reported error line is invalid.\nGiven code is:\n{0}", code), line, problem.getSourceLineNumber()); //$NON-NLS-1$
		return problem;
	}

	private IProblem parseAndCheckErrors(final String code, final int line, final int offset) {

		// Check offset
		final IProblem problem = parseAndCheckErrors(code, line);
		Assert.assertEquals(MessageFormat.format("Wrong start offset. Given code was:\n{0}", code), offset, problem.getSourceStart()); //$NON-NLS-1$
		return problem;
	}

	private void parseAndCheckErrors(final String code, final int line, final int startOffset, final int endOffset) {

		// Parse and check line
		final IProblem problem = parseAndCheckErrors(code, line);

		// Check start
		final int problemSourceStart = problem.getSourceStart();
		final String startMessage = "Start error offset too small :{0} <= expected, got <{1}>.\nGiven code is:\n{2}"; //$NON-NLS-1$
		Assert.assertTrue(MessageFormat.format(startMessage, startOffset, problemSourceStart, code), startOffset <= problemSourceStart);

		// Check end
		final int problemSourceEnd = problem.getSourceEnd();
		final String endMessage = "End error offset is to big: >={0} expected, got <{1}>.\nGiven code is:\n{2}"; //$NON-NLS-1$
		Assert.assertTrue(MessageFormat.format(endMessage, endOffset, problemSourceEnd, code), endOffset >= problemSourceEnd);
	}

	@Test
	public void testImbricatedBlocks() {
		final StringBuilder wrongBlock = new StringBuilder();
		wrongBlock.append("do"); //$NON-NLS-1$
		wrongBlock.append(NEW_LINE);
		wrongBlock.append("do"); //$NON-NLS-1$
		wrongBlock.append(NEW_LINE);
		wrongBlock.append("error"); //$NON-NLS-1$
		wrongBlock.append(NEW_LINE);
		wrongBlock.append("end"); //$NON-NLS-1$
		wrongBlock.append(NEW_LINE);
		wrongBlock.append("end"); //$NON-NLS-1$
		parseAndCheckErrors(wrongBlock.toString(), 3, 6);
	}

	@Test
	public void testIncompleteDo() {
		parseAndCheckErrors("do"); //$NON-NLS-1$ 
	}

	@Test
	public void testIncompleteElse() {
		parseAndCheckErrors("else", 1, 0); //$NON-NLS-1$ 
		parseAndCheckErrors("else end"); //$NON-NLS-1$ 
		parseAndCheckErrors("else x=nil end"); //$NON-NLS-1$ 
		parseAndCheckErrors("if else"); //$NON-NLS-1$ 
		parseAndCheckErrors("if true else"); //$NON-NLS-1$ 
		parseAndCheckErrors("if true then else"); //$NON-NLS-1$ 
		parseAndCheckErrors("if then else"); //$NON-NLS-1$ 
		parseAndCheckErrors("if then else end"); //$NON-NLS-1$ 
	}

	@Test
	public void testIncompleteEnd() {
		parseAndCheckErrors("end", 1, 0); //$NON-NLS-1$ 
	}

	@Test
	public void testIncompleteFor() {
		parseAndCheckErrors("for", 1, 0); //$NON-NLS-1$ 
	}

	@Test
	public void testIncompleteFunction() {
		parseAndCheckErrors("function"); //$NON-NLS-1$ 
	}

	@Test
	public void testIncompleteIf() {
		parseAndCheckErrors("if"); //$NON-NLS-1$ 
		parseAndCheckErrors("if end"); //$NON-NLS-1$ 
		parseAndCheckErrors("if then end"); //$NON-NLS-1$ 
		parseAndCheckErrors("if true end"); //$NON-NLS-1$ 
	}

	@Test
	public void testIncompleteIfElseIf() {
		parseAndCheckErrors("elseif"); //$NON-NLS-1$ 
		parseAndCheckErrors("elseif end"); //$NON-NLS-1$ 
		parseAndCheckErrors("elseif true end"); //$NON-NLS-1$ 
		parseAndCheckErrors("elseif true then end"); //$NON-NLS-1$

		parseAndCheckErrors("if elseif"); //$NON-NLS-1$ 
		parseAndCheckErrors("if elseif end"); //$NON-NLS-1$ 
		parseAndCheckErrors("if then elseif end"); //$NON-NLS-1$ 
		parseAndCheckErrors("if then elseif then end"); //$NON-NLS-1$ 
		parseAndCheckErrors("if true then elseif then end"); //$NON-NLS-1$ 
	}

	@Test
	public void testIncompleteLocal() {
		parseAndCheckErrors("local", 1, 0); //$NON-NLS-1$ 
	}

	@Test
	public void testIncompleteRepeat() {
		parseAndCheckErrors("repeat"); //$NON-NLS-1$ 
	}

	@Test
	public void testIncompleteThen() {
		parseAndCheckErrors("then", 1, 0); //$NON-NLS-1$ 
	}

	@Test
	public void testSyntaxErrorAfterBlankLines() {
		final StringBuilder sb = new StringBuilder();
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("if"); //$NON-NLS-1$
		parseAndCheckErrors(sb.toString(), 3, 4);
	}

	@Test
	public void testSyntaxErrorAtStart() {
		final StringBuilder sb = new StringBuilder();
		sb.append("x"); //$NON-NLS-1$
		sb.append(NEW_LINE);
		sb.append("return nil"); //$NON-NLS-1$
		parseAndCheckErrors(sb.toString(), 1, 0);
	}

	@Test
	public void testSyntaxErrorSurroundedByValidCode() {
		final StringBuilder sb = new StringBuilder();
		sb.append("n=function(x)"); //$NON-NLS-1$
		sb.append(NEW_LINE);
		sb.append("x"); //$NON-NLS-1$
		sb.append(NEW_LINE);
		sb.append("end"); //$NON-NLS-1$
		parseAndCheckErrors(sb.toString(), 1, 2, sb.length());
	}

	@Test
	public void testWrongExpression() {
		final StringBuilder wrongFunction = new StringBuilder();
		wrongFunction.append("n = function()");//$NON-NLS-1$
		wrongFunction.append(NEW_LINE);
		wrongFunction.append("return x x");//$NON-NLS-1$
		wrongFunction.append(NEW_LINE);
		wrongFunction.append("end"); //$NON-NLS-1$
		parseAndCheckErrors(wrongFunction.toString(), 1, 4);
	}

	@Test
	public void testWrongStatements() {
		parseAndCheckErrors("function n(x x)end", 1, 10); //$NON-NLS-1$
		parseAndCheckErrors("local ="); //$NON-NLS-1$
		parseAndCheckErrors("for _,_ ine x do end"); //$NON-NLS-1$
	}
}
