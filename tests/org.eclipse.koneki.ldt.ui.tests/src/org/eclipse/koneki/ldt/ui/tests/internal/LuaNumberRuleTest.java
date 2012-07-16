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

package org.eclipse.koneki.ldt.ui.tests.internal;

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaCodeScanner;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaCodeScanner.LuaNumberRule;
import org.junit.Test;

/**
 * Tests for {@link LuaCodeScanner.LuaNumberRule}.
 */
public class LuaNumberRuleTest extends TestCase {

	private static final IToken NUMBER_TOKEN = new Token("number"); //$NON-NLS-1$

	@Test
	public void testIntegers() {
		numberDetected("0", 0, 1); //$NON-NLS-1$
		numberDetected("10", 0, 2); //$NON-NLS-1$
		numberDetected("-10", 0, 3, false); //$NON-NLS-1$
	}

	@Test
	public void testDecimals() {
		numberDetected("0.", 0, 2); //$NON-NLS-1$
		numberDetected(".1", 0, 2); //$NON-NLS-1$
		numberDetected("0.0", 0, 3); //$NON-NLS-1$
		numberDetected("local x = 3.4", 10, 3); //$NON-NLS-1$
		numberDetected("local x=3.4", 8, 3); //$NON-NLS-1$
	}

	@Test
	public void testHexadecimals() {
		numberDetected("0x1", 0, 3); //$NON-NLS-1$
		numberDetected("0xf", 0, 3); //$NON-NLS-1$
		numberDetected("0x12", 0, 4); //$NON-NLS-1$
		numberDetected("0xAA", 0, 4); //$NON-NLS-1$
		numberDetected("0x", 0, 2, false); //$NON-NLS-1$
	}

	@Test
	public void testNumbersInExpressions() {
		numberDetected("local x = (10/3.4)", 11, 2); //$NON-NLS-1$
		numberDetected("local x = (10/3.4)", 14, 3); //$NON-NLS-1$
		numberDetected("local x = (10./3.4E10)", 11, 3); //$NON-NLS-1$
		numberDetected("local x = (10./3.4E10)", 15, 6); //$NON-NLS-1$
	}

	@Test
	public void testExponential() {
		numberDetected("1e1", 0, 3); //$NON-NLS-1$
		numberDetected("1E10", 0, 4); //$NON-NLS-1$
		numberDetected("0.1e10", 0, 6); //$NON-NLS-1$
		numberDetected("0.1E10", 0, 6); //$NON-NLS-1$
		numberDetected(".1E10", 0, 5); //$NON-NLS-1$
		numberDetected(".1e10", 0, 5); //$NON-NLS-1$
		numberDetected("1.E10", 0, 5); //$NON-NLS-1$
		numberDetected("1.e10", 0, 5); //$NON-NLS-1$
	}

	@Test
	public void testNonNumbers() {
		assertNoNumberFound("abc"); //$NON-NLS-1$
		assertNoNumberFound("e"); //$NON-NLS-1$
		assertNoNumberFound("E"); //$NON-NLS-1$
		assertNoNumberFound("ex"); //$NON-NLS-1$
		assertNoNumberFound("."); //$NON-NLS-1$
		assertNoNumberFound(".."); //$NON-NLS-1$
		assertNoNumberFound(".x"); //$NON-NLS-1$
		assertNoNumberFound("abc.x"); //$NON-NLS-1$
		assertNoNumberFound("a.b"); //$NON-NLS-1$
		assertNoNumberFound("a.E"); //$NON-NLS-1$
		assertNoNumberFound("a.ex"); //$NON-NLS-1$
		assertNoNumberFound("a.x"); //$NON-NLS-1$
		assertNoNumberFound("os.exit()"); //$NON-NLS-1$
		assertNoNumberFound("table:send()"); //$NON-NLS-1$
		assertNoNumberFound("io.flush()"); //$NON-NLS-1$
	}

	private void numberDetected(final String n, final int offset, final int length, final boolean failWhenNumberIsFound) {
		final IDocument doc = new Document(n);
		final LuaCodeScanner.LuaNumberRule numberRule = new LuaNumberRule(NUMBER_TOKEN);
		final RuleBasedScanner scanner = new RuleBasedScanner();
		scanner.setRules(new IRule[] { numberRule });
		scanner.setRange(doc, offset, doc.getLength() - offset);
		final IToken token = scanner.nextToken();
		final boolean numberFound = token == NUMBER_TOKEN && scanner.getTokenOffset() == offset && scanner.getTokenLength() == length;
		if ((!numberFound) && failWhenNumberIsFound) {
			final String negation = failWhenNumberIsFound ? "" : " not "; //$NON-NLS-1$ //$NON-NLS-2$
			fail(MessageFormat.format("In \"{0}\", \"{1}\" is {2} parsed as a number.", n, n.substring(offset, offset + length), negation)); //$NON-NLS-1$
		}
	}

	private void numberDetected(String n, int offset, int length) {
		numberDetected(n, offset, length, true);
	}

	private void assertNoNumberFound(final String n) {
		final IDocument doc = new Document(n);
		final LuaCodeScanner.LuaNumberRule numberRule = new LuaNumberRule(NUMBER_TOKEN);
		final RuleBasedScanner scanner = new RuleBasedScanner();
		scanner.setRules(new IRule[] { numberRule });
		scanner.setRange(doc, 0, doc.getLength());
		for (IToken t = scanner.nextToken(); t != Token.EOF; t = scanner.nextToken()) {
			if (t == NUMBER_TOKEN) {
				final StringBuffer sb = new StringBuffer(n);
				sb.insert(scanner.getTokenOffset(), "*"); //$NON-NLS-1$
				sb.insert(scanner.getTokenOffset() + scanner.getTokenLength() + 1, "*"); //$NON-NLS-1$
				fail("A number has been found at an unexpected location: \"" + sb.toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}
