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
package org.eclipse.koneki.ldt.ui.internal.editor.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaPartitionScanner;

public class LuaMultLineStringRule implements IPredicateRule {
	// CHECKSTYLE:OFF
	protected int readCount;
	protected IToken returnToken;
	// CHECKSTYLE:ON

	private IToken fDefaultToken;

	public LuaMultLineStringRule(IToken stringToken) {
		this.fDefaultToken = stringToken;
	}

	@Override
	public IToken getSuccessToken() {
		return fDefaultToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		// initialize value
		readCount = 0;
		returnToken = fDefaultToken;

		// evaluate rule
		IToken result = doEvaluate(scanner);

		// rewind scanner if no token detected
		if (result.isUndefined()) {
			for (; readCount > 0; readCount--) {
				scanner.unread();
			}
		}
		return result;
	}

	protected IToken doEvaluate(ICharacterScanner scanner) {
		int equalsNumber = 0;
		int c = scanner.read();
		readCount++;

		if (c == '[') {
			// begin parsing what looks like a multiline string/comment
			c = scanner.read();
			readCount++;
			while (c == '=') {
				equalsNumber++;
				c = scanner.read();
				readCount++;
			}

			// at this point, the current character should be '[' otherwise it means we are not
			// detecting a multiline string/comment opening after all
			if (c != '[') {
				return Token.UNDEFINED;
			}

			// now read characters until ']' is detected...
			IToken content = doEvaluateContent(scanner);
			if (content.isUndefined())
				return content;

			// now, look for the second ']', which may be located after "equalsNumber" '=' signs.
			// we should retry as many times as we don't encounter the right pattern, or stop if we
			// reach the EOF
			c = scanner.read();
			while (c != LuaPartitionScanner.EOF) {
				boolean missed = false;
				if (c == ']') {
					c = scanner.read();
					readCount++;
					for (int i = 0; i < equalsNumber; i++) {
						if (c != '=') {
							missed = true;
							break;
						}
						c = scanner.read();
						readCount++;
					}
					// if we exited the loop because there were not enough '=', we need
					// to start looking for the first ']' again
					if (missed)
						continue;
					// now should be the second ']'
					if (c == ']')
						return returnToken;
					// else restart looking for the first ']'
					c = scanner.read();
					readCount++;
				} else {
					c = scanner.read();
					readCount++;
				}
			}
			scanner.unread();
			return returnToken;
		}
		return Token.UNDEFINED;
	}

	protected IToken doEvaluateContent(ICharacterScanner scanner) {
		int c;
		do {
			c = scanner.read();
			readCount++;
		} while (c != ']' && c != LuaPartitionScanner.EOF);

		scanner.unread();
		readCount--;
		return returnToken;
	}
}