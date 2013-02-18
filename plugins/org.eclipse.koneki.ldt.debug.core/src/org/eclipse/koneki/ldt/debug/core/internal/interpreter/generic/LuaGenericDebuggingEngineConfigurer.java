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
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.debug.DbgpConnectionConfig;
import org.eclipse.koneki.ldt.debug.core.internal.Activator;
import org.eclipse.koneki.ldt.debug.core.internal.LuaDebugConstants;

public class LuaGenericDebuggingEngineConfigurer extends LuaGenericInterpreterConfigurer {

	private InterpreterConfig initialConfig;

	@Override
	public InterpreterConfig alterConfig(final ILaunch launch, final InterpreterConfig config, final IInterpreterInstall interpreterinstall)
			throws CoreException {
		// In debug engine the config must not be alter, surely because it is used as key to retrieve the DBGPConnectionConfig.
		initialConfig = config;
		InterpreterConfig interpreterConfig = (InterpreterConfig) config.clone();
		return super.alterConfig(launch, interpreterConfig, interpreterinstall);
	}

	protected List<IPath> getLuaPath(ILaunch launch, InterpreterConfig config) throws CoreException {
		List<IPath> luaPath = super.getLuaPath(launch, config);

		// add debugger path to lua path
		try {
			URL debuggerEntry = Activator.getDefault().getBundle().getEntry(LuaDebugConstants.DEBUGGER_PATH);
			File debuggerFolder = new File(FileLocator.toFileURL(debuggerEntry).getFile());
			luaPath.add(new Path(debuggerFolder.getPath()));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to extract debugger files.")); //$NON-NLS-1$
		}

		return luaPath;
	}

	@Override
	protected List<String> addCommands(final ILaunch launch, final InterpreterConfig config) throws CoreException {
		// Add debugger command to existing ones
		final List<String> parentList = super.addCommands(launch, config);
		parentList.add(createRunDebuggerCommand(launch, config));
		return parentList;
	}

	@Override
	protected Map<String, String> addEnvironmentVariables(final ILaunch launch, final InterpreterConfig config) throws CoreException {

		// Get the dbgp connection corresponding to this config.
		final Map<String, String> envVars = super.addEnvironmentVariables(launch, config);
		final DbgpConnectionConfig dbgpConnectionConfig = DbgpConnectionConfig.load(initialConfig);

		// HOST
		final String host = "127.0.0.1"; //$NON-NLS-1$
		envVars.put(LuaDebugConstants.ENV_VAR_KEY_DBGP_IDE_HOST, host);

		// PORT
		final int port = dbgpConnectionConfig.getPort();
		envVars.put(LuaDebugConstants.ENV_VAR_KEY_DBGP_IDE_PORT, Integer.toString(port));

		// SESSION ID
		final String sessionId = dbgpConnectionConfig.getSessionId();
		envVars.put(LuaDebugConstants.ENV_VAR_KEY_DBGP_IDE_KEY, sessionId);

		// TRANSPORT LAYER
		final String transportLayer = getTransportLayer();
		if (transportLayer != null && config.getEnvVar(LuaDebugConstants.ENV_VAR_KEY_DBGP_TRANSPORT) == null)
			envVars.put(LuaDebugConstants.ENV_VAR_KEY_DBGP_TRANSPORT, transportLayer);

		// PLATFORM
		final String os = Platform.getOS();
		if (os.equals(Platform.OS_WIN32))
			envVars.put(LuaDebugConstants.ENV_VAR_KEY_DBGP_PLATFORM, "win");//$NON-NLS-1$
		else
			envVars.put(LuaDebugConstants.ENV_VAR_KEY_DBGP_PLATFORM, "unix");//$NON-NLS-1$

		// WORKING DIRECTORY
		final IPath workingDirectory = config.getWorkingDirectoryPath();
		if (!workingDirectory.isEmpty())
			envVars.put(LuaDebugConstants.ENV_VAR_KEY_DBGP_WORKINGDIR, workingDirectory.toPortableString());

		// Indicate client it's being debugged
		envVars.put(LuaDebugConstants.ENV_VAR_DEBUGGING, "true"); //$NON-NLS-1$

		return envVars;
	}

	protected String createRunDebuggerCommand(final ILaunch launch, final InterpreterConfig config) {
		// Create command
		return "require ('debugger')();"; //$NON-NLS-1$
	}

	protected String getTransportLayer() {
		return null;
	}
}
