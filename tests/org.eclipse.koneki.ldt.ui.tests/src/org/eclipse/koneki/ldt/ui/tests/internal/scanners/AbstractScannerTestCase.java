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
package org.eclipse.koneki.ldt.ui.tests.internal.scanners;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.tests.internal.ScannerResult;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractScannerTestCase extends TestCase {

	private final File referenceFile;
	private final File inputFile;
	private String inputString;
	private String referenceString;

	public AbstractScannerTestCase(String testName, File inputFilePath, File referenceFilePath) {
		inputFile = inputFilePath;
		referenceFile = referenceFilePath;

		setName(testName);
	}

	@Before
	public void setUp() {

		// Check if input file exist
		if (!inputFile.exists()) {
			final String message = MessageFormat.format("{0} input does not exist.", inputFile); //$NON-NLS-1$
			throw new RuntimeException(message);
		}
		// Check if reference file exist
		if (!referenceFile.exists()) {
			final String message = MessageFormat.format("{0} reference does not exist.", referenceFile); //$NON-NLS-1$
			throw new RuntimeException(message);
		}

		inputString = loadInputString(inputFile);

		referenceString = loadReferenceString(referenceFile);

		try {
			referenceString = FileUtils.readFileToString(referenceFile);
		} catch (IOException e) {
			final String message = MessageFormat.format("Unable to read reference file: {0}", referenceFile); //$NON-NLS-1$
			throw new RuntimeException(message, e);
		}

	}

	protected String loadInputString(File inputFile2) {
		return loadString(inputFile2);
	}

	protected String loadReferenceString(File referenceFile2) {
		return loadString(referenceFile2);
	}

	private String loadString(File file) {
		String fileInString;
		try {
			fileInString = FileUtils.readFileToString(file);
		} catch (IOException e) {
			final String message = MessageFormat.format("Unable to read source file: {0}", inputFile.getAbsolutePath()); //$NON-NLS-1$
			throw new RuntimeException(message, e);
		}
		return fileInString;
	}

	@Test
	public void test() {
		Document doc = new Document(getInputString());
		ITokenScanner partionner = createScanner();
		partionner.setRange(doc, 0, doc.getLength());
		List<ScannerResult> tokenList = new ArrayList<ScannerResult>();

		for (IToken token = partionner.nextToken(); token != Token.EOF; token = partionner.nextToken()) {
			tokenList.add(new ScannerResult(token, partionner.getTokenOffset(), partionner.getTokenLength()));
		}

		assertEquals("File partionning differ from the reference:", getReferenceString(), toString(tokenList)); //$NON-NLS-1$
	}

	protected abstract ITokenScanner createScanner();

	protected String toString(List<ScannerResult> tokenList) {
		StringBuilder stringBuilder = new StringBuilder();

		for (ScannerResult result : tokenList) {
			if (!isIgnoredToken(result.getToken())) {
				stringBuilder.append(result.toString());
			}
		}
		return stringBuilder.toString();
	}

	protected boolean isIgnoredToken(IToken token) {
		return token.getData() == null;
	}

	@Override
	public void runTest() {
		test();
	}

	public String getInputString() {
		return inputString;
	}

	public String getReferenceString() {
		return referenceString;
	}

}
