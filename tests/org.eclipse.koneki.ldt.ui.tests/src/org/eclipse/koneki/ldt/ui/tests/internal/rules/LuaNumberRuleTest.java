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

package org.eclipse.koneki.ldt.ui.tests.internal.rules;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaCodeScanner.LuaNumberRule;
import org.junit.Test;

public class LuaNumberRuleTest extends AbstractRuleTestCase {

	private static final Token NUMBER_TOKEN = new Token("number"); //$NON-NLS-1$

	@Test
	public void testIntegers() {
		assertTokenFound("0", 0, 1, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("10", 0, 2, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("-10", 1, 2, NUMBER_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testDecimals() {
		assertTokenFound("0.", 0, 2, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound(".1", 0, 2, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("0.0", 0, 3, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("local x = 3.4", 10, 3, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("local x=3.4", 8, 3, NUMBER_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testHexadecimals() {
		assertTokenFound("0x1", 0, 3, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("0xf", 0, 3, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("0x12", 0, 4, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("0xAA", 0, 4, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("0x", NUMBER_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testNumbersInExpressions() {
		assertTokenFound("local x = (10/3.4)", 11, 2, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("local x = (10/3.4)", 14, 3, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("local x = (10./3.4E10)", 11, 3, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("local x = (10./3.4E10)", 15, 6, NUMBER_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testExponential() {
		assertTokenFound("1e1", 0, 3, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("1E10", 0, 4, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("0.1e10", 0, 6, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("0.1E10", 0, 6, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound(".1E10", 0, 5, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound(".1e10", 0, 5, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("1.E10", 0, 5, NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenFound("1.e10", 0, 5, NUMBER_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testNonNumbers() {

		assertTokenNotFound("abc", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("e", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("E", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("ex", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound(".", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("..", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound(".x", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("abc.x", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("a.b", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("a.E", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("a.ex", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("a.x", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("os.exit()", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("table:send()", NUMBER_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("io.flush()", NUMBER_TOKEN); //$NON-NLS-1$
	}

	@Override
	protected IRule createRule() {
		return new LuaNumberRule(NUMBER_TOKEN);
	}
}
