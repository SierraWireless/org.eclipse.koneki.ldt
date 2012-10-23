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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.koneki.ldt.lua.tests.internal.Activator;
import org.eclipse.koneki.ldt.metalua.AbstractMetaLuaModule;
import org.junit.Assert;

import com.naef.jnlua.LuaState;

/**
 * Run lua test function on two files, a source and a reference one.
 */
public class LuaTestModuleRunner extends AbstractMetaLuaModule {

	private static final String LUA_TEST_FUNCTION = "test"; //$NON-NLS-1$

	private final List<String> path;
	private final String sourceFilePath;
	private final String referenceFilePath;
	private final List<String> filesToCompile;
	private final String testModule;

	public LuaTestModuleRunner(final String testModuleName, final String sourcePath, final String refPath, final List<String> localPath,
			final List<String> filesPathToCompile) {
		testModule = testModuleName;
		filesToCompile = filesPathToCompile;
		sourceFilePath = sourcePath;
		referenceFilePath = refPath;
		path = new ArrayList<String>();
		path.addAll(localPath);
	}

	@Override
	protected List<String> getMetaLuaFileToCompile() {
		return filesToCompile;
	}

	@Override
	protected List<String> getLuaSourcePaths() {
		return path;
	}

	@Override
	protected List<String> getMetaLuaSourcePaths() {
		return getLuaSourcePaths();
	}

	@Override
	protected String getPluginID() {
		return Activator.PLUGIN_ID;
	}

	@Override
	protected String getModuleName() {
		return testModule;
	}

	/**
	 * Call test function named {@value #LUA_TEST_FUNCTION}.
	 * 
	 * @throws CoreException
	 *             with message error when failure occurs.
	 */
	public void run() {

		// Load Lua instance with right module
		final LuaState luaState = loadLuaModule();

		try {
			// Run lua test function
			luaState.getGlobal(getModuleName());
			luaState.getField(-1, LUA_TEST_FUNCTION);
			luaState.pushString(sourceFilePath);
			luaState.pushString(referenceFilePath);
			luaState.call(2, 2);

			// Get error message if there is any
			if ((luaState.isBoolean(-2) && !luaState.toBoolean(-2)) || luaState.isNil(-2)) {

				// Notify error when needed
				final String errorMessage = luaState.toString(-1);
				if (errorMessage != null) {
					Assert.fail(errorMessage);
				} else {
					Assert.fail("No message"); //$NON-NLS-1$
				}
			}
		} finally {
			// Close lua instance
			luaState.close();
		}
	}

}
