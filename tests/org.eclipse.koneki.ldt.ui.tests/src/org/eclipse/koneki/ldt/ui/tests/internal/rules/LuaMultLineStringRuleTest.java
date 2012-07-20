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
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaCodeScanner;
import org.eclipse.koneki.ldt.ui.internal.editor.text.rules.LuaMultLineStringRule;
import org.junit.Test;

/**
 * Tests for {@link LuaCodeScanner.LuaNumberRule}.
 */
public class LuaMultLineStringRuleTest extends AbstractRuleTestCase {

	private static final IToken MULTI_LINE_STRING_TOKEN = new Token("multi_line"); //$NON-NLS-1$

	@Test
	public void testMutiLineString() {
		assertTokenFound("[[string]]", 0, 10, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[string]]", 2, 10, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-string]]", 2, 11, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[[\nstring\n]]", 0, 12, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound(" [[string]]", 1, 10, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[=[string]=]", 0, 12, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[===[string]===]", 0, 16, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[[string\nstring2\n]]", 0, 19, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[[string]][[string2]]", 0, 10, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[[string]][[string2]]", 10, 11, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[[string[[string2]]", 0, 19, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testEndLessString() {
		assertTokenFound("[[string", 0, 8, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[[string]string2", 0, 16, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[[\nstring\nstring2", 0, 17, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[[string]=]string2", 0, 18, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[=[string]]string2", 0, 18, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[=[string]==]string2", 0, 20, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testStringInCode() {
		assertTokenFound("print([[string]])", 6, 10, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("print([[\nstring\n]])", 6, 12, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("[[\nstring\n]]\nlocal var", 0, 12, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
		assertTokenFound("local var = [[\nstring\n]]\nprint(var)", 12, 12, MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testNoMutiLineString() {
		assertTokenNotFound("[string]]", MULTI_LINE_STRING_TOKEN); //$NON-NLS-1$
	}

	@Override
	protected IRule createRule() {
		return new LuaMultLineStringRule(MULTI_LINE_STRING_TOKEN);
	}

}
