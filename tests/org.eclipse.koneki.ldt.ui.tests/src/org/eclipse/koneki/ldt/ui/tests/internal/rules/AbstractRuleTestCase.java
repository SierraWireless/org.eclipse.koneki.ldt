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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.tests.internal.ScannerResult;

/**
 * An abstract class to perform JUnit tests on rules
 */
public abstract class AbstractRuleTestCase extends TestCase {

	protected abstract IRule createRule();

	protected ITokenScanner createScanner(final IDocument doc) {
		final IRule numberRule = createRule();
		final RuleBasedScanner scanner = new RuleBasedScanner();
		scanner.setRules(new IRule[] { numberRule });
		scanner.setRange(doc, 0, doc.getLength());
		return scanner;
	}

	public void assertTokenFound(final String inputString, final int expectedOffset, final int expectedLength, final IToken expectedToken) {
		final IDocument doc = new Document(inputString);
		ITokenScanner scanner = createScanner(doc);

		boolean tokenFound = false;
		List<ScannerResult> tokenList = new ArrayList<ScannerResult>();
		for (IToken token = scanner.nextToken(); token != Token.EOF; token = scanner.nextToken()) {
			if (token == expectedToken && scanner.getTokenOffset() == expectedOffset && scanner.getTokenLength() == expectedLength) {
				tokenFound = true;
			}
			tokenList.add(new ScannerResult(token, scanner.getTokenOffset(), scanner.getTokenLength()));
		}
		if (!tokenFound) {
			StringBuffer messageBuilder = new StringBuffer();
			messageBuilder.append(MessageFormat.format("For input:\"{0}\" expected token is [-Token={1} -Offset={2} -Lenght={3}] ", //$NON-NLS-1$
					inputString.replace("\n", "\\n"), expectedToken.getData(), expectedOffset, expectedLength)); //$NON-NLS-1$//$NON-NLS-2$
			if (tokenList.isEmpty()) {
				messageBuilder.append("but no token was found"); //$NON-NLS-1$
			} else {
				messageBuilder.append("\nbut token(s) found were:\n"); //$NON-NLS-1$
				appendTokenList(tokenList, messageBuilder, inputString);
			}
			fail(messageBuilder.toString());
		}
	}

	public void assertTokenNotFound(final String inputString, final IToken tokenNotExpected) {
		final IDocument doc = new Document(inputString);
		ITokenScanner scanner = createScanner(doc);

		boolean tokenFound = false;
		List<ScannerResult> tokenList = new ArrayList<ScannerResult>();
		for (IToken token = scanner.nextToken(); token != Token.EOF; token = scanner.nextToken()) {
			if (token == tokenNotExpected) {
				tokenFound = true;
			}

			tokenList.add(new ScannerResult(token, scanner.getTokenOffset(), scanner.getTokenLength()));
		}

		if (tokenFound) {
			StringBuffer messageBuilder = new StringBuffer();
			messageBuilder.append(MessageFormat.format("For input:\"{0}\" the token not expected is [-Token={1}] ", inputString.replace("\n", "\\n"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					tokenNotExpected.getData()));
			if (!tokenList.isEmpty()) {
				messageBuilder.append("but following token(s) was found:\n"); //$NON-NLS-1$
				appendTokenList(tokenList, messageBuilder, inputString);
			}
			fail(messageBuilder.toString());
		}
	}

	protected void appendTokenList(final List<ScannerResult> tokenList, final StringBuffer messageBuilder, final String inputString) {
		for (ScannerResult result : tokenList) {
			StringBuilder inputWithStar = new StringBuilder(inputString);
			inputWithStar.insert(result.getOffset(), '*');
			inputWithStar.insert(result.getOffset() + result.getLenght() + 1, '*');
			messageBuilder.append(MessageFormat.format(
					"'{'-Token={0} -Offset={1} -Lenght={2} \"{3}\"'}'\n", result.getToken().getData(), result.getOffset(), //$NON-NLS-1$
					result.getLenght(), inputWithStar.toString()));
		}
	}

}
