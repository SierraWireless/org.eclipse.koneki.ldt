/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
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
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.core.LuaConstants;

/**
 * Defines rules to follow in order to highlight source code in editor
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class LuaPartitionScanner extends RuleBasedPartitionScanner {

	public class MultiLineStringWithEqualsRule implements IPredicateRule {
		private IToken fToken;

		public MultiLineStringWithEqualsRule(IToken token) {
			this.fToken = token;
		}

		@Override
		public IToken getSuccessToken() {
			return fToken;
		}

		@Override
		public IToken evaluate(ICharacterScanner scanner, boolean resume) {
			int c = scanner.read();
			int equalsNumber = 0;
			if (c == '[') {
				// begin parsing what looks like a multiline string
				c = scanner.read();
				while (c == '=' && c != EOF) {
					equalsNumber++;
					c = scanner.read();
				}

				// at this point, the current character should be '[' otherwise it means we are not
				// detecting a multiline string opening after all
				if (c != '[') {
					scanner.unread();
					return Token.UNDEFINED;
				}

				// now read characters until ']' is detected...
				c = scanner.read();
				while (c != ']' && c != EOF) {
					c = scanner.read();
				}

				// now, look for the second ']', which may be located after "equalsNumber" '=' signs.
				// we should retry as many times as we don't encounter the right pattern, or stop if we reach the EOF
				while (c != EOF) {
					boolean missed = false;
					if (c == ']') {
						c = scanner.read();
						for (int i = 0; i < equalsNumber; i++) {
							if (c != '=') {
								missed = true;
								break;
							}
							c = scanner.read();
						}
						// if we exited the loop because there were not enough '=', we need
						// to start looking for the first ']' again
						if (missed)
							continue;
						// now should be the second ']'
						if (c == ']')
							return fToken;
						// else restart looking for the first ']'
						c = scanner.read();
					} else {
						c = scanner.read();
					}
				}
			}

			scanner.unread();
			return Token.UNDEFINED;
		}

		@Override
		public IToken evaluate(ICharacterScanner scanner) {
			return evaluate(scanner, false);
		}

	}

	public LuaPartitionScanner() {
		super();
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

		/*
		 * Deal with single and double quote multi lines strings
		 */
		IToken string = new Token(ILuaPartitions.LUA_STRING);
		IToken singleQuoteString = new Token(ILuaPartitions.LUA_SINGLE_QUOTE_STRING);
		IToken multilineString = new Token(ILuaPartitions.LUA_MULTI_LINE_STRING);
		rules.add(new MultiLineStringWithEqualsRule(multilineString));
		rules.add(new MultiLineRule("\'", "\'", singleQuoteString, '\\', false)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("\"", "\"", string, '\\', false)); //$NON-NLS-1$ //$NON-NLS-2$
		//	rules.add(new MultiLineRule("[[", "]]", multilineString)); //$NON-NLS-1$ //$NON-NLS-2$

		/*
		 * Deal with comments
		 */

		// Multi-line
		IToken multiLineComment = new Token(ILuaPartitions.LUA_MULTI_LINE_COMMENT);
		rules.add(new MultiLineRule("--[[", "]]", multiLineComment));//$NON-NLS-1$ //$NON-NLS-2$

		// Single line
		IToken comment = new Token(ILuaPartitions.LUA_COMMENT);
		rules.add(new EndOfLineRule(LuaConstants.COMMENT_STRING, comment));

		// Apply rules
		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}
