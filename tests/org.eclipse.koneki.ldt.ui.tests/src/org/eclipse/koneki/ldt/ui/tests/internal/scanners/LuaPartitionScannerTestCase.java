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

import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaPartitionScanner;

public class LuaPartitionScannerTestCase extends AbstractScannerTestCase {

	public LuaPartitionScannerTestCase(String testName, File inputFile, File referenceFile) {
		super(testName, inputFile, referenceFile);
	}

	@Override
	protected ITokenScanner createScanner() {
		return new LuaPartitionScanner();
	}

}
