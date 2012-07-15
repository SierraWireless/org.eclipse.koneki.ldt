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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.dltk.ui.text.ScriptCommentScanner;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.TodoTaskPreferencesOnPreferenceStore;
import org.eclipse.dltk.ui.text.rules.CombinedWordRule.WordMatcher;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.internal.editor.LuaDocumentorTags;
import org.eclipse.swt.SWT;

public class LuaDocumentorScanner extends ScriptCommentScanner {

	public LuaDocumentorScanner(ScriptSourceViewerConfiguration configuration) {
		super(configuration, ILuaColorConstants.LUA_DOC, ILuaColorConstants.COMMENT_TASK_TAGS, new TodoTaskPreferencesOnPreferenceStore(
				configuration.getPreferenceStore()));
	}

	@Override
	protected String[] getTokenProperties() {
		return new String[] { ILuaColorConstants.LUA_DOC, ILuaColorConstants.COMMENT_TASK_TAGS, ILuaColorConstants.LUA_DOC_TAGS };
	}

	@Override
	protected List<WordMatcher> createMatchers() {
		final List<WordMatcher> matchers = super.createMatchers();
		matchers.add(createLuaDocumentorKeywordMatcher());
		return matchers;
	}

	@Override
	protected List<IRule> createRules() {
		final List<IRule> rules = new ArrayList<IRule>();
		Token luaDoc = getToken(ILuaColorConstants.LUA_DOC);
		TextAttribute textAttribute;

		// Rules below are used to perform basic MarkDown pattern detection (bold, italic, ...)
		Token monospace = new Token(luaDoc.getData());
		textAttribute = (TextAttribute) monospace.getData();
		monospace.setData(new TextAttribute(textAttribute.getForeground(), textAttribute.getBackground(), textAttribute.getStyle()
				| TextAttribute.UNDERLINE));
		rules.add(new PatternRule("`", "`", monospace, (char) 0, false)); //$NON-NLS-1$//$NON-NLS-2$

		Token bold = new Token(luaDoc.getData());
		textAttribute = (TextAttribute) bold.getData();
		bold.setData(new TextAttribute(textAttribute.getForeground(), textAttribute.getBackground(), textAttribute.getStyle() | SWT.BOLD));
		rules.add(new PatternRule("__", "__", bold, (char) 0, false)); //$NON-NLS-1$//$NON-NLS-2$
		rules.add(new PatternRule("**", "**", bold, (char) 0, false)); //$NON-NLS-1$//$NON-NLS-2$

		Token italic = new Token(luaDoc.getData());
		textAttribute = (TextAttribute) italic.getData();
		italic.setData(new TextAttribute(textAttribute.getForeground(), textAttribute.getBackground(), textAttribute.getStyle() | SWT.ITALIC));
		rules.add(new PatternRule("_", "_", italic, (char) 0, false)); //$NON-NLS-1$//$NON-NLS-2$
		rules.add(new PatternRule("*", "*", italic, (char) 0, false)); //$NON-NLS-1$//$NON-NLS-2$

		rules.addAll(super.createRules());

		return rules;
	}

	private WordMatcher createLuaDocumentorKeywordMatcher() {
		final WordMatcher matcher = new WordMatcher();
		final Set<String> tags = new HashSet<String>();
		Collections.addAll(tags, LuaDocumentorTags.getTags());

		for (String tag : tags) {
			matcher.addWord(tag, getToken(ILuaColorConstants.LUA_DOC_TAGS));
		}
		return matcher;
	}

	/**
	 * @see org.eclipse.dltk.ui.text.ScriptCommentScanner#skipCommentChars()
	 */
	@Override
	protected int skipCommentChars() {
		int count = 0;
		int c = read();

		// ignore whitespaces
		IWhitespaceDetector whitespaceDetector = new IWhitespaceDetector() {
			@Override
			public boolean isWhitespace(char c) {
				return c == ' ' || c == '\t';
			}
		};
		while (whitespaceDetector.isWhitespace((char) c)) {
			count++;
			c = read();
		}

		// if we have a '-', it means we are at the beginning of a single line comment,
		// so we should skip exactly two dashes
		if (c == '-') {
			c = read();
			if (c == '-') {
				return 2 + count;
			}
			unread();
		}

		unread();
		return count;
	}

	@Override
	protected IWordDetector createIdentifierDetector() {
		return new IWordDetector() {
			public boolean isWordStart(char c) {
				return c == '@' || Character.isJavaIdentifierStart(c);
			}

			public boolean isWordPart(char c) {
				return c == '.' || c == '-' || Character.isJavaIdentifierPart(c);
			}
		};
	}

	@Override
	public IToken nextToken() {
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;
		if (fRules != null) {
			for (int i = 0; i < fRules.length; i++) {
				IToken token = (fRules[i].evaluate(this));
				if (!token.isUndefined())
					return token;
			}
		}
		if (read() == EOF)
			return Token.EOF;
		return fDefaultReturnToken;
	}

}
