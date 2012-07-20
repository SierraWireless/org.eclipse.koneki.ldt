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
import org.eclipse.koneki.ldt.ui.internal.editor.text.rules.LuaDocSingleCommentSeriesRule;
import org.junit.Test;

/**
 * Tests for {@link LuaCodeScanner.LuaNumberRule}.
 */
public class LuaDocSingleCommentSeriesRuleTest extends AbstractRuleTestCase {

	private static final IToken HYPHENS_LUA_DOC = new Token("lua_doc"); //$NON-NLS-1$

	@Test
	public void testDocumentation() {
		assertTokenFound("---doc", 0, 6, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("---doc\n", 0, 7, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("--- doc", 0, 7, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("----doc ", 0, 8, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("---doc\n--doc2", 0, 13, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("---doc\n--doc2\n--doc3", 0, 20, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("---\n--doc", 0, 9, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("----\n--doc", 0, 10, HYPHENS_LUA_DOC); //$NON-NLS-1$
	}

	@Test
	public void testInCode() {
		assertTokenFound("--comment\n---doc", 10, 6, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("---doc\n--comment", 0, 16, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("---doc\nlocal var", 0, 7, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("local var---doc", 9, 6, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("local var\n---doc\n", 10, 7, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("---doc\n--doc2\nlocal var", 0, 14, HYPHENS_LUA_DOC); //$NON-NLS-1$
		assertTokenFound("--comment\n---doc\nlocal var", 10, 7, HYPHENS_LUA_DOC); //$NON-NLS-1$
	}

	@Test
	public void testNoDocumentation() {
		assertTokenNotFound("--doc", HYPHENS_LUA_DOC); //$NON-NLS-1$
	}

	@Override
	protected IRule createRule() {
		return new LuaDocSingleCommentSeriesRule(HYPHENS_LUA_DOC); // define scanner instead
	}

}
