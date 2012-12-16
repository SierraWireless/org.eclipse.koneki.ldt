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
package org.eclipse.koneki.ldt.remote.debug.core.internal.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.remote.core.internal.RSEUtil;
import org.eclipse.koneki.ldt.remote.core.internal.lua.LuaRSEUtil;
import org.eclipse.koneki.ldt.remote.core.internal.lua.LuaSubSystem;
import org.eclipse.koneki.ldt.remote.debug.core.internal.LuaRemoteDebugConstant;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.ISystemProfile;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystem;

/**
 * Utility class to handle ILaunchConfiguration for LuaEmbeddedLaunchConfiguration
 */
public final class LuaRemoteLaunchConfigurationUtil {

	private LuaRemoteLaunchConfigurationUtil() {
	}

	/**
	 * set the HOST_ID attributes in the given launch configuration
	 * 
	 * @param conf
	 *            to edit
	 * @param host
	 *            to save in the HOST_ID attribute
	 */
	public static final void setConnectionId(ILaunchConfigurationWorkingCopy conf, IHost host) {
		List<String> hostData = new ArrayList<String>();
		hostData.add(host.getSystemProfileName());
		hostData.add(host.getName());
		conf.setAttribute(LuaRemoteDebugConstant.HOST_ID, hostData);
	}

	/**
	 * get the host identify by the given ILaunchConfiguration.
	 * 
	 * @return the host defined for this ILaunchConfiguration or null if no host is found
	 */
	public static final IHost getHost(ILaunchConfiguration conf) {
		try {
			List<?> hostData = conf.getAttribute(LuaRemoteDebugConstant.HOST_ID, Collections.EMPTY_LIST);

			// list must contained 2 elements (profileName and host name)
			if (hostData.size() != 2)
				return null;

			// get profile name
			Object oProfileName = hostData.get(0);
			if (!(oProfileName instanceof String))
				return null;
			String profileName = (String) oProfileName;

			// get host name
			Object oHost = hostData.get(1);
			if (!(oHost instanceof String))
				return null;
			String hostID = (String) oHost;

			// get profile
			ISystemProfile systemProfile = RSECorePlugin.getTheSystemRegistry().getSystemProfile(profileName);
			if (systemProfile == null)
				return null;

			// get host
			IHost host = RSECorePlugin.getTheSystemRegistry().getHost(systemProfile, hostID);
			return host;
		} catch (CoreException e) {
			return null;
		}
	}

	/**
	 * internal method to validate the value of the current tab
	 * 
	 * @param luaInspectorPort
	 * 
	 * @return true if value is valid
	 */
	public static String validateRemoteLaunchConfiguration(String projectName, String scriptName, IHost host) {
		// project validation
		// -------------------------------
		// projectName validation
		if (projectName == null || projectName.isEmpty()) {
			return Messages.LuaRemoteLaunchConfigurationUtil_error_no_project;
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project == null || !project.exists()) {
			return NLS.bind(Messages.LuaRemoteLaunchConfigurationUtil_error_unexisted_project, projectName);
		}
		if (!project.isOpen()) {
			return NLS.bind(Messages.LuaRemoteLaunchConfigurationUtil_error_closed_project, projectName);
		}
		try {
			if (!project.hasNature(LuaNature.ID)) {
				return NLS.bind(Messages.LuaRemoteLaunchConfigurationUtil_error_not_lua_project, projectName);
			}
		} catch (CoreException e) {
			// must not append (at this line project is open and exist)
			return "Unexpected problem :" + e.getMessage(); //$NON-NLS-1$
		}

		// scriptName validation
		if (scriptName == null || scriptName.isEmpty()) {
			return Messages.LuaRemoteLaunchConfigurationUtil_error_no_script_selected;
		}
		IResource script = project.findMember(scriptName);
		if (script == null || !script.exists()) {
			return NLS.bind(Messages.LuaRemoteLaunchConfigurationUtil_error_script_desnt_exist, scriptName);
		}
		if (script.getType() != IResource.FILE || !script.getFileExtension().equals("lua")) { //$NON-NLS-1$
			return NLS.bind(Messages.LuaRemoteLaunchConfigurationUtil_error_script_not_lua_file, scriptName);
		}

		// target validation
		// --------------------------------
		if (host == null) {
			return Messages.LuaRemoteLaunchConfigurationUtil_error_no_host_selected;
		}

		String hostName = host.getName();
		// check the target has the lua support
		LuaSubSystem luaSubSystem = LuaRSEUtil.getLuaSubSystem(host);
		if (luaSubSystem == null) {
			return NLS.bind(Messages.LuaRemoteLaunchConfigurationUtil_error_no_lua_service, hostName);
		}

		// check luaSubSystem configuration
		String luaCommand = luaSubSystem.getLuaCommand();
		if (luaCommand == null || luaCommand.isEmpty()) {
			return NLS.bind(Messages.LuaRemoteLaunchConfigurationUtil_error_no_luacommand, hostName);
		}
		String outputDirectory = luaSubSystem.getOutputDirectory();
		if (outputDirectory == null || outputDirectory.isEmpty()) {
			return NLS.bind(Messages.LuaRemoteLaunchConfigurationUtil_error_no_outputdir, hostName);
		}

		// check the target has the lua support
		IRemoteFileSubSystem remoteFileSubsystem = RSEUtil.getRemoteFileSubsystem(host);
		if (remoteFileSubsystem == null) {
			return NLS.bind(Messages.LuaRemoteLaunchConfigurationUtil_error_no_remote_file_service, hostName);
		}
		return null;
	}

	public static String getRemoteApplicationPath(ILaunchConfiguration configuration) {

		final IHost host = getHost(configuration);
		if (host == null)
			return ""; //$NON-NLS-1$

		final IRemoteFileSubSystem remoteFileSubSystem = RSEUtil.getRemoteFileSubsystem(host);
		final LuaSubSystem luaSubSystem = LuaRSEUtil.getLuaSubSystem(host);
		if (luaSubSystem == null || remoteFileSubSystem == null)
			return ""; //$NON-NLS-1$
		return luaSubSystem.getOutputDirectory() + remoteFileSubSystem.getSeparator() + configuration.getName();
	}
}
