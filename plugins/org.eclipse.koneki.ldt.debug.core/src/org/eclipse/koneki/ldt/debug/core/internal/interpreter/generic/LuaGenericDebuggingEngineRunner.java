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
package org.eclipse.koneki.ldt.debug.core.internal.interpreter.generic;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.debug.core.IDbgpService;
import org.eclipse.dltk.debug.core.ScriptDebugManager;
import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.launching.DebuggingEngineRunner;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.debug.core.internal.Activator;
import org.eclipse.koneki.ldt.debug.core.internal.LuaDebugTarget;

public class LuaGenericDebuggingEngineRunner extends DebuggingEngineRunner {

	public LuaGenericDebuggingEngineRunner(IInterpreterInstall install) {
		super(install);
	}

	@Override
	protected InterpreterConfig addEngineConfig(InterpreterConfig config, PreferencesLookupDelegate delegate, ILaunch launch) throws CoreException {
		InterpreterConfig interpreterConfig = (InterpreterConfig) config.clone();

		try {
			// add debugger to lua path
			URL debuggerEntry = Activator.getDefault().getBundle().getEntry("script"); //$NON-NLS-1$
			File debuggerFolder = new File(FileLocator.toFileURL(debuggerEntry).getFile());

			// add args to call debugger
			interpreterConfig.addInterpreterArg("-e");
			interpreterConfig.addInterpreterArg("\"package.path = package.path..[[;" + debuggerFolder.getPath() + File.separatorChar
					+ "?.lua]]; require ('debugger')();\"");

		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to extract debugger files.")); //$NON-NLS-1$
		}

		return interpreterConfig;
	}

	@Override
	protected String getDebuggingEngineId() {
		return ScriptDebugManager.getInstance().getDebugModelByNature(LuaNature.ID);
	}

	@Override
	protected String getDebugPreferenceQualifier() {
		return Activator.PLUGIN_ID;
	}

	protected IScriptDebugTarget createDebugTarget(ILaunch launch, IDbgpService dbgpService) throws CoreException {
		// TODO use generated Session Id
		// return new LuaDebugTarget(getDebugModelId(), dbgpService, getSessionId(launch.getLaunchConfiguration()), launch, null);
		return new LuaDebugTarget(getDebugModelId(), dbgpService, "luaidekey", launch, null); //$NON-NLS-1$
	}

	@Override
	protected String getDebuggingEnginePreferenceQualifier() {
		// TODO verify it's not useful
		// return Activator.PLUGIN_ID + ".engine";
		return null;
	}

	@Override
	protected String getLogFileNamePreferenceKey() {
		// TODO verify it's not useful
		// return Activator.PLUGIN_ID + ".logfilenamekey";
		return null;
	}
}
