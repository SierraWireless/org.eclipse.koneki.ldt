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

import junit.framework.TestCase;

public class LuaPartitionScannerTestSuite extends AbstractScannerTestSuite {

	public LuaPartitionScannerTestSuite(boolean ignoreFailure) {
		super("LuaPartitionScanner", "tests/codepartionner", "txt", ignoreFailure); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}

	@Override
	protected TestCase createTestCase(String testName, File inputFile, File referenceFile) {
		return new LuaPartitionScannerTestCase(testName, inputFile, referenceFile);
	}

}
