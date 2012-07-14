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

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaCodeScanner;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaCodeScanner.LuaNumberRule;

/**
 * Tests for {@link LuaCodeScanner.LuaNumberRule}.
 */
public class LuaNumberRuleTest extends TestCase {

	private static final IToken NUMBER_TOKEN = new Token("number"); //$NON-NLS-1$

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNumbers() {
		assertTrue(numberDetected("0")); //$NON-NLS-1$
		assertTrue(numberDetected("0.")); //$NON-NLS-1$

		assertTrue(numberDetected("-1")); //$NON-NLS-1$
		assertTrue(numberDetected("-1.2")); //$NON-NLS-1$
		assertTrue(numberDetected("-.3")); //$NON-NLS-1$

		assertTrue(numberDetected("1E10")); //$NON-NLS-1$
		assertTrue(numberDetected("-1E10")); //$NON-NLS-1$
		assertTrue(numberDetected("0.1e10")); //$NON-NLS-1$
		assertTrue(numberDetected("0.1E10")); //$NON-NLS-1$
		assertTrue(numberDetected(".1")); //$NON-NLS-1$
		assertTrue(numberDetected(".1E10")); //$NON-NLS-1$
		assertTrue(numberDetected(".1e10")); //$NON-NLS-1$

		assertTrue(numberDetected("0x")); //$NON-NLS-1$
		assertTrue(numberDetected("0x12")); //$NON-NLS-1$
	}

	public void testNonNumbers() {
		assertFalse(numberDetected("abc")); //$NON-NLS-1$

		assertFalse(numberDetected("e")); //$NON-NLS-1$
		assertFalse(numberDetected("E")); //$NON-NLS-1$
		assertFalse(numberDetected("e10")); //$NON-NLS-1$
		assertFalse(numberDetected("E10")); //$NON-NLS-1$
		assertFalse(numberDetected("ex")); //$NON-NLS-1$

		//		assertFalse(numberDetected(".")); //$NON-NLS-1$
		//		assertFalse(numberDetected("..")); //$NON-NLS-1$
		//		assertFalse(numberDetected(".x")); //$NON-NLS-1$

		//		assertFalse(numberDetected("0.x")); //$NON-NLS-1$
		//		assertFalse(numberDetected("0.ex")); //$NON-NLS-1$

		assertFalse(numberDetected("a.b")); //$NON-NLS-1$
		assertFalse(numberDetected("a.E")); //$NON-NLS-1$
		assertFalse(numberDetected("a.ex")); //$NON-NLS-1$
		assertFalse(numberDetected("a.x")); //$NON-NLS-1$
	}

	private boolean numberDetected(String n) {
		IDocument doc = new Document(n);
		LuaCodeScanner.LuaNumberRule numberRule = new LuaNumberRule(NUMBER_TOKEN);
		RuleBasedScanner scanner = new RuleBasedScanner();
		scanner.setRules(new IRule[] { numberRule });
		scanner.setRange(doc, 0, doc.getLength());
		return scanner.nextToken() == NUMBER_TOKEN;
	}

}
