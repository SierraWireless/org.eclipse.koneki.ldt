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
import org.eclipse.koneki.ldt.ui.internal.editor.text.rules.LuaDocMultLineCommentRule;
import org.junit.Test;

/**
 * Tests for {@link LuaCodeScanner.LuaNumberRule}.
 */
public class LuaDocMultLineCommentTest extends AbstractRuleTestCase {

	private static final IToken MULTI_LINE_DOC_TOKEN = new Token("multi_line_doc"); //$NON-NLS-1$

	@Test
	public void testMutiLineDoc() {
		assertTokenFound("--[[-doc]]", 0, 10, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-\ndoc\n]]", 0, 12, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-doc1--doc2]]", 0, 17, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound(" --[[-doc]]", 1, 10, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[=[-doc]=]", 0, 12, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[===[-doc]===]", 0, 16, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-doc]]--[[-doc2]]", 0, 10, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-doc]]--[[-doc2]]", 10, 11, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-doc--[[-doc2]]", 0, 19, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testEndLessDoc() {
		assertTokenFound("--[[-doc", 0, 8, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-doc]doc2", 0, 13, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-\ndoc\ndoc2", 0, 14, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-doc]=]doc2", 0, 15, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[=[-doc]]doc2", 0, 15, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[=[-doc]==]doc2", 0, 17, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testInCodeDoc() {
		assertTokenFound("print('code')--[[-doc]]", 13, 10, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("print('code')\n--[[-doc]]", 14, 10, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("--[[-doc]]\nprint('code')", 0, 10, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("print('code')\n--[[-doc]]\nprint('code')", 14, 10, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenFound("print('code')\n--[[-\ndoc\n]]\nprint('code')", 14, 12, MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
	}

	@Test
	public void testNoMultiLineDoc() {
		assertTokenNotFound("-[[-doc]]", MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("[[-doc]]", MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("--[[\n-doc\n]]", MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("--[[doc]]", MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
		assertTokenNotFound("--[-doc]]", MULTI_LINE_DOC_TOKEN); //$NON-NLS-1$
	}

	@Override
	protected IRule createRule() {
		return new LuaDocMultLineCommentRule(MULTI_LINE_DOC_TOKEN);
	}

}
