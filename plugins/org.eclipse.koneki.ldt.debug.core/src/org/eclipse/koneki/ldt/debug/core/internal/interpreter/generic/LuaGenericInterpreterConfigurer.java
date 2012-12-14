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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.koneki.ldt.core.IProjectSourceRootFolderVisitor;
import org.eclipse.koneki.ldt.core.LuaUtils;
import org.eclipse.koneki.ldt.core.LuaUtils.ProjectFragmentFilter;
import org.eclipse.koneki.ldt.debug.core.internal.LuaDebugConstants;

public class LuaGenericInterpreterConfigurer {

	private static final String LUA_INIT_PATTERN = LuaDebugConstants.WILDCARD_PATTERN + File.separator + LuaDebugConstants.LUA_INIT_PATTERN;
	private static final String LUAC_INIT_PATTERN = LuaDebugConstants.WILDCARD_PATTERN + File.separator + LuaDebugConstants.LUAC_INIT_PATTERN;

	public InterpreterConfig alterConfig(final ILaunch launch, final InterpreterConfig config, final IInterpreterInstall interpreterinstall)
			throws CoreException {

		// TODO HACK ENV_VAR : make environment variable defined at interpreter level less priority
		// ****************************************************************************
		// get launch conf env var
		@SuppressWarnings("unchecked")
		final Map<String, String> configEnvs = launch.getLaunchConfiguration().getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES,
				(Map<?, ?>) null);
		EnvironmentVariable[] interEnvs = interpreterinstall.getEnvironmentVariables();
		// add var defined at interpreter level only if it was not defined at launch conf level
		if (interEnvs != null) {
			for (EnvironmentVariable envVar : interEnvs) {
				if (configEnvs == null || !configEnvs.containsKey(envVar.getName()))
					config.addEnvVar(envVar.getName(), envVar.getValue());
			}
		}
		// END HACK
		// ****************************************************************************

		// Append project path to $LUA_PATH
		final String envLuaPath = config.getEnvVar(LuaDebugConstants.LUA_PATH);
		final String interpreterPath = createLuaPath(launch, config);
		if (envLuaPath != null) {
			config.addEnvVar(LuaDebugConstants.LUA_PATH, interpreterPath + envLuaPath);
		} else {
			config.addEnvVar(LuaDebugConstants.LUA_PATH, interpreterPath);

		}

		// Create commands to execute
		final List<String> commandList = new ArrayList<String>();
		addCommands(commandList, launch, config);

		// Flatten commands
		if (!commandList.isEmpty()) {
			final StringBuilder commands = new StringBuilder();
			for (final String cmd : commandList) {
				commands.append(cmd);
			}

			// Add commands to execute as interpreter argument
			config.addInterpreterArg("-e"); //$NON-NLS-1$
			config.addInterpreterArg(commands.toString());
		}
		return config;
	}

	protected void addCommands(final List<String> commandList, final ILaunch launch, final InterpreterConfig config) throws CoreException {
	}

	protected String createLuaPath(final ILaunch launch, final InterpreterConfig config) throws CoreException {
		// Get lua path
		List<IPath> luaPath = getLuaPath(launch, config);

		// Create : set path command
		StringBuilder command = new StringBuilder();
		for (final IPath iPath : luaPath) {
			command.append(iPath.toOSString());
			command.append(File.separatorChar);
			command.append(LuaDebugConstants.LUA_PATTERN);
			command.append(iPath.toOSString());
			command.append(File.separatorChar);
			command.append(LUA_INIT_PATTERN);
			command.append(iPath.toOSString());
			command.append(File.separatorChar);
			command.append(LuaDebugConstants.LUAC_PATTERN);
			command.append(iPath.toOSString());
			command.append(File.separatorChar);
			command.append(LUAC_INIT_PATTERN);
		}
		return command.toString();
	}

	protected List<IPath> getLuaPath(final ILaunch launch, final InterpreterConfig config) throws CoreException {
		// get Script Project
		String projectName = launch.getLaunchConfiguration().getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		// collect all root sourceFolder which are not in archive or execution environment
		final List<IPath> luaPath = new ArrayList<IPath>();
		LuaUtils.visitRootSourceFolder(DLTKCore.create(project),
				EnumSet.complementOf(EnumSet.of(ProjectFragmentFilter.ARCHIVE, ProjectFragmentFilter.EXECUTION_ENVIRONMENT)),
				new IProjectSourceRootFolderVisitor() {

					@Override
					public void processSourceRootFolder(IPath absolutePath, IProgressMonitor monitor) throws CoreException {
						luaPath.add(absolutePath);
					}
				}, new NullProgressMonitor());

		return luaPath;
	}
}
