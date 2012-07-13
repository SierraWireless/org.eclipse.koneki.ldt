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
package org.eclipse.koneki.ldt.ui.internal.editor.text;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class MultiLineStringOrCommentRule implements IPredicateRule {
	private IToken fDefaultToken;
	private IToken fDocToken;

	public MultiLineStringOrCommentRule(IToken commentToken) {
		this.fDefaultToken = commentToken;
	}

	public MultiLineStringOrCommentRule(IToken commentToken, IToken docToken) {
		this.fDefaultToken = commentToken;
		this.fDocToken = docToken;
	}

	@Override
	public IToken getSuccessToken() {
		return fDefaultToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		int equalsNumber = 0;
		int c = scanner.read();
		int readCount = 1;
		IToken returnToken = fDefaultToken;

		if (fDocToken != null) {
			if (c == '-') {
				c = scanner.read();
				readCount++;
				if (c != '-') {
					scanner.unread();
					return Token.UNDEFINED;
				} else {
					c = scanner.read();
					readCount++;
				}
			} else {
				scanner.unread();
				return Token.UNDEFINED;
			}
		}
		if (c == '[') {
			// begin parsing what looks like a multiline string/comment
			c = scanner.read();
			readCount++;
			while (c == '=' && c != LuaPartitionScanner.EOF) {
				equalsNumber++;
				c = scanner.read();
				readCount++;
			}

			// at this point, the current character should be '[' otherwise it means we are not
			// detecting a multiline string/comment opening after all
			if (c != '[') {
				scanner.unread();
				readCount--;
				for (; readCount > 0; readCount--) {
					scanner.unread();
				}
				return Token.UNDEFINED;
			}

			// now read characters until ']' is detected...
			c = scanner.read();
			// if the first actual character inside the comment block is a "-", then
			// we have a LuaDoc block...
			if (fDocToken != null && c == '-') {
				returnToken = fDocToken;
			}
			readCount++;
			while (c != ']' && c != LuaPartitionScanner.EOF) {
				c = scanner.read();
				readCount++;
			}

			// now, look for the second ']', which may be located after "equalsNumber" '=' signs.
			// we should retry as many times as we don't encounter the right pattern, or stop if we
			// reach the EOF
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
		}

		for (; readCount > 0; readCount--) {
			scanner.unread();
		}
		return Token.UNDEFINED;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

}