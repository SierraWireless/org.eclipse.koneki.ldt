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
package org.eclipse.koneki.ldt.remote.debug.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.ISystemProfile;

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
	public static String validateLuaEmbeddedConfiguration(String projectName, IHost host) {
		// projectName validation
		if (projectName == null || projectName.isEmpty()) {
			return "A project must be selected.";
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project == null || !project.exists()) {
			return "This project doesn't exist.";
		}
		if (!project.isOpen()) {
			return "This project is not open.";
		}
		try {
			if (!project.hasNature(LuaNature.ID)) {
				return "This project is not a lua project.";
			}
		} catch (CoreException e) {
			// must not append (at this line project is open and exist)
			return null;
		}

		// target validation
		if (host == null) {
			return "An host must be selected.";
		}
		return null;
	}

}
