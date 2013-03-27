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

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.junit.Before;
import org.junit.Test;

/**
 * Runs lua test function on a lua file and its reference one.
 * 
 * The idea is to call a lua function with the paths of tested lua file and reference one.
 */
public class LuaTestCase extends TestCase {

	private final String moduleName;
	private final List<String> luaPath;
	private final String referenceFileAbsolutePath;
	private final String sourceFileAbsolutePath;

	/** Actual test is performed by this object */
	private LuaTestModuleRunner luaRunner;

	public LuaTestCase(final String testName, final String testModuleName, final IPath inputFilePath, final IPath referenceFilePath,
			final List<String> directoryListForLuaPath) {
		moduleName = testModuleName;
		sourceFileAbsolutePath = inputFilePath.toOSString();
		referenceFileAbsolutePath = referenceFilePath.toOSString();
		luaPath = directoryListForLuaPath;

		setName(testName);
	}

	@Before
	public void setUp() {

		// Check if input file exist
		if (!new File(sourceFileAbsolutePath).exists()) {
			final String message = MessageFormat.format("{0} input does not exist.", sourceFileAbsolutePath); //$NON-NLS-1$
			throw new RuntimeException(message);
		}
		// Check if reference file exist
		if (!new File(referenceFileAbsolutePath).exists()) {
			final String message = MessageFormat.format("{0} reference does not exist.", referenceFileAbsolutePath); //$NON-NLS-1$
			throw new RuntimeException(message);
		}

		luaRunner = createLuaRunner(moduleName, sourceFileAbsolutePath, referenceFileAbsolutePath, luaPath, filesToCompile());
	}

	protected LuaTestModuleRunner createLuaRunner(String module, String absoluteSourcePath, String asbsoluteReferencePath, List<String> luapath,
			List<String> files) {
		return new LuaTestModuleRunner(module, absoluteSourcePath, asbsoluteReferencePath, luapath, files);
	}

	@Test
	public void test() {
		// Run test on lua side
		luaRunner.run();
	}

	@Override
	public void runTest() {
		test();
	}

	protected List<String> filesToCompile() {
		return Collections.<String> emptyList();
	}
}
