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

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

/**
 * A scanner to detect the following pattern:
 * 
 * <pre>
 * ---
 * -- @module foo.
 * -- Blah blah.
 * </pre>
 */
public class LuaDocSingleCommentSeriesRule implements IPredicateRule {
	private IToken fDocToken;
	private EndOfLineRule threeDashesRule;
	private EndOfLineRule twoDashesRule;

	public LuaDocSingleCommentSeriesRule(IToken docToken) {
		this.fDocToken = docToken;

		threeDashesRule = new EndOfLineRule("---", fDocToken); //$NON-NLS-1$
		twoDashesRule = new EndOfLineRule("--", fDocToken); //$NON-NLS-1$
	}

	@Override
	public IToken getSuccessToken() {
		return fDocToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		WhitespaceRule whiteSpaceRule = new WhitespaceRule(new IWhitespaceDetector() {
			@Override
			public boolean isWhitespace(char c) {
				return c == ' ' || c == '\t';
			}
		});

		IToken t = threeDashesRule.evaluate(scanner);
		if (t == fDocToken) {
			while (t == fDocToken) {
				// ignore possible trailing whitespaces
				whiteSpaceRule.evaluate(scanner);
				t = twoDashesRule.evaluate(scanner);
			}
			return fDocToken;
		}

		return Token.UNDEFINED;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

}