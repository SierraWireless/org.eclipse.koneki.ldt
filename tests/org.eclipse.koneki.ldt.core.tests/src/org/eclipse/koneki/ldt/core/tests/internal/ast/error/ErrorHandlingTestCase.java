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

import junit.framework.TestCase;

import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemCollector;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParser;
import org.junit.Before;
import org.junit.Test;

public class ErrorHandlingTestCase extends TestCase {

	private LuaSourceParser parser;
	private ProblemCollector reporter;

	@Before
	public void setUp() {
		parser = new LuaSourceParser();
		reporter = new ProblemCollector();
	}

	@Test
	public void testBindingOnFaultyLocal() {
		final String code = "\n\nlocal "; //$NON-NLS-1$
		parser.parse(new ModuleSource(code), reporter);
		if (reporter.isEmpty()) {
			fail(MessageFormat.format("No error found for code:\n{0}", code)); //$NON-NLS-1$
		} else {
			final IProblem iProblem = reporter.getProblems().get(0);
			final int currentProblemLine = iProblem.getSourceLineNumber();
			final int problemLine = 3;
			if (problemLine != currentProblemLine) {
				fail(MessageFormat.format("Error displayed on line {0} instead of line {1}.", currentProblemLine, problemLine)); //$NON-NLS-1$
			}
		}
	}

	@Test
	public void testIncompleteStatement() {
		final String code = "if"; //$NON-NLS-1$
		parser.parse(new ModuleSource(code), reporter);
		if (!reporter.isEmpty()) {
			fail(MessageFormat.format("No error found for incomplete statement:\n{0}", code)); //$NON-NLS-1$
		}
	}

	@Test
	public void testErrorHandling() {
		final String code = "local ="; //$NON-NLS-1$
		parser.parse(new ModuleSource(code), reporter);
		if (!reporter.isEmpty()) {
			fail(MessageFormat.format("No error found for incomplete statement:\n{0}", code)); //$NON-NLS-1$
		}
	}
}
