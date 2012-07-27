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

import org.eclipse.core.runtime.CoreException;
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
		LuaGenericDebuggingEngineConfigurer luaGenericDebuggingEngineConfigurer = new LuaGenericDebuggingEngineConfigurer();
		InterpreterConfig alteredConfig = luaGenericDebuggingEngineConfigurer.alterConfig(launch, config);
		return alteredConfig;
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
		return new LuaDebugTarget(getDebugModelId(), dbgpService, getSessionId(launch.getLaunchConfiguration()), launch, null);
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
