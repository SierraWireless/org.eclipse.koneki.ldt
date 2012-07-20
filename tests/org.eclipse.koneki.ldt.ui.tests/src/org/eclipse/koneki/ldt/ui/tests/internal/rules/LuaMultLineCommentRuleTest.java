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
import org.eclipse.koneki.ldt.ui.internal.editor.text.rules.LuaMultLineCommentRule;
import org.junit.Test;

/**
 * Tests for {@link LuaCodeScanner.LuaNumberRule}.
 */
public class LuaMultLineCommentRuleTest extends AbstractRuleTestCase {

	private static final IToken MULTI_LINE_COMMENT_TOKEN = new Token("multi_line_comment"); //$NON-NLS-1$

	@Test
	public void testMutiLineComment() {
		assertTokenFound("--[[comment]]", 0, 13, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-comment]]", 0, 14, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[\ncomment\n]]", 0, 15, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound(" --[[comment]]", 1, 13, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[=[comment]=]", 0, 15, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[===[comment]===]", 0, 19, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[comment\n--comment2\n]]", 0, 25, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[comment]]--[[comment2]]", 0, 13, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[comment]]--[[comment2]]", 13, 14, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[comment--[[comment2]]", 0, 25, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testEndLessComment() {
		assertTokenFound("--[[comment", 0, 11, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[comment]comment2", 0, 20, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[\ncomment\ncomment2", 0, 21, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[comment]=]comment2", 0, 22, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[=[comment]]comment2", 0, 22, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[=[comment]==]comment2", 0, 24, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testInCodeComment() {
		assertTokenFound("print('code')--[[comment]]", 13, 13, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("print('code')\n--[[comment]]", 14, 13, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[comment]]\nprint('code')", 0, 13, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("print('code')\n--[[comment]]\nprint('code')", 14, 13, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenFound("print('code')\n--[[\ncomment\n]]\nprint('code')", 14, 15, MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testNoMutiLineComment() {
		assertTokenNotFound("-[[comment]]", MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("[[comment]]", MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("--[comment]]", MULTI_LINE_COMMENT_TOKEN); //$NON-NLS-1$
	}

	@Override
	protected IRule createRule() {
		return new LuaMultLineCommentRule(MULTI_LINE_COMMENT_TOKEN);
	}

}
