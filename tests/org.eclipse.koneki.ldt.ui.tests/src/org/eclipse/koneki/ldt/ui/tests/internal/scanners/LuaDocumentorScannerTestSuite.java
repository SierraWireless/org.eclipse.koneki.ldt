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
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class LuaDocumentorScannerTestSuite extends AbstractScannerTestSuite {

	public LuaDocumentorScannerTestSuite(boolean ignoreFailure) {
		super("LuaDocumentorScanner", "tests/luadocumentor", "txt", ignoreFailure); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}

	@Override
	protected TestCase createTestCase(String testName, File inputFile, File referenceFile) {
		return new LuaDocumentorScannerTestCase(testName, inputFile, referenceFile);
	}

	@Override
	protected List<String> createTestBlacklist() {
		List<String> blacklist = new ArrayList<String>();
		blacklist.add("longluadoc.lua"); //$NON-NLS-1$
		blacklist.add("module.lua"); //$NON-NLS-1$
		blacklist.add("firstline.lua"); //$NON-NLS-1$
		return blacklist;
	}

}
