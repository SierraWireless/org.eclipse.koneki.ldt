/*******************************************************************************
 * Copyright (c) 2009, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/

package org.eclipse.koneki.ldt.ui.internal.editor.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.core.LuaConstants;
import org.eclipse.koneki.ldt.ui.internal.editor.text.rules.LuaDocMultLineCommentRule;
import org.eclipse.koneki.ldt.ui.internal.editor.text.rules.LuaDocSingleCommentSeriesRule;
import org.eclipse.koneki.ldt.ui.internal.editor.text.rules.LuaMultLineCommentRule;
import org.eclipse.koneki.ldt.ui.internal.editor.text.rules.LuaMultLineStringRule;

/**
 * Defines rules to follow in order to highlight source code in editor
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class LuaPartitionScanner extends RuleBasedPartitionScanner {

	public LuaPartitionScanner() {
		super();
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

		/*
		 * Deal with documentation
		 */
		// Multi-line documentation
		IToken docMultiLine = new Token(ILuaPartitions.LUA_DOC_MULTI);
		rules.add(new LuaDocMultLineCommentRule(docMultiLine));

		// Documentation starting with "---"
		IToken doc = new Token(ILuaPartitions.LUA_DOC);
		rules.add(new LuaDocSingleCommentSeriesRule(doc));

		/*
		 * Deal with comments
		 */
		// Multi-line documentation
		IToken multilineComment = new Token(ILuaPartitions.LUA_MULTI_LINE_COMMENT);
		rules.add(new LuaMultLineCommentRule(multilineComment));

		// Single line
		IToken singleLineComment = new Token(ILuaPartitions.LUA_COMMENT);
		rules.add(new EndOfLineRule(LuaConstants.COMMENT_STRING, singleLineComment));

		/*
		 * Deal with single and double quote multi lines strings
		 */
		IToken multilineString = new Token(ILuaPartitions.LUA_MULTI_LINE_STRING);
		rules.add(new LuaMultLineStringRule(multilineString));

		IToken singleQuoteString = new Token(ILuaPartitions.LUA_SINGLE_QUOTE_STRING);
		rules.add(new SingleLineRule("\'", "\'", singleQuoteString, '\\', false)); //$NON-NLS-1$ //$NON-NLS-2$

		IToken string = new Token(ILuaPartitions.LUA_STRING);
		rules.add(new SingleLineRule("\"", "\"", string, '\\', false)); //$NON-NLS-1$ //$NON-NLS-2$

		// Apply rules
		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}
