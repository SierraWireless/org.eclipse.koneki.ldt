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
package org.eclipse.koneki.ldt.lua.tests.internal.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;

public class ModelTestCase extends LuaTestCase {

	private static ArrayList<String> filesToCompileList = null;

	public ModelTestCase(final String testSuiteName, final String testFileName, final IPath sourceFilePath, final IPath referenceFilePath,
			final List<String> directoryListForLuaPath) {
		super(testSuiteName, testFileName, sourceFilePath, referenceFilePath, directoryListForLuaPath);
	}

	@Override
	protected synchronized List<String> filesToCompile() {
		if (filesToCompileList == null) {
			filesToCompileList = new ArrayList<String>(2);
			filesToCompileList.add("models/internalmodelbuilder.mlua");//$NON-NLS-1$
			filesToCompileList.add("models/apimodelbuilder.mlua"); //$NON-NLS-1$
		}
		return filesToCompileList;
	}
}
