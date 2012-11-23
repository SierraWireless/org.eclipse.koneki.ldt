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
package org.eclipse.koneki.ldt.remote.debug.ui.internal.launch;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local.LuaApplicationLaunchShortcut;
import org.eclipse.koneki.ldt.remote.core.internal.RSEUtil;
import org.eclipse.koneki.ldt.remote.core.internal.lua.LuaSubSystem;
import org.eclipse.koneki.ldt.remote.debug.core.internal.LuaRemoteDebugConstant;
import org.eclipse.koneki.ldt.remote.debug.core.internal.launch.LuaRemoteLaunchConfigurationUtil;
import org.eclipse.koneki.ldt.remote.debug.ui.internal.Activator;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.internal.ui.view.SystemViewLabelAndContentProvider;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystem;
import org.eclipse.rse.ui.actions.SystemNewConnectionAction;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

@SuppressWarnings("restriction")
public class LuaRemoteLaunchShortcut extends LuaApplicationLaunchShortcut {

	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(LuaRemoteDebugConstant.REMOTE_LAUNCH_CONFIGURATION_ID);
	}

	/**
	 * Copy of the super method with a custom config name generation
	 */
	@Override
	public ILaunchConfiguration createLaunchConfiguration(IFile script) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;

		try {
			ILaunchConfigurationType configType = getConfigurationType();

			// wait for RSE
			RSEUtil.waitForRSEInitialization();

			// find a host for the launch conf
			IHost host = null;
			List<IHost> hosts = findHosts();
			if (hosts.isEmpty()) {
				// ask user about creating a target
				if (MessageDialog.openQuestion(getShell(), Messages.LuaRemoteLaunchShortcut_notargetdialog_title,
						Messages.LuaRemoteLaunchShortcut_notargetdialog_message)) {
					SystemNewConnectionAction newConnectionAction = new SystemNewConnectionAction(getShell(), false, null);
					newConnectionAction.run();
					host = (IHost) newConnectionAction.getValue();
				}

				// open new lua target wizard
			} else if (hosts.size() == 1) {
				host = hosts.get(0);
			} else {
				// select dialog
				ElementListSelectionDialog selectHostDialog = new ElementListSelectionDialog(getShell(), new SystemViewLabelAndContentProvider());
				selectHostDialog.setElements(hosts.toArray());
				selectHostDialog.setTitle(Messages.LuaRemoteLaunchShortcut_selectHostDialog_title);
				selectHostDialog.setMessage(Messages.LuaRemoteLaunchShortcut_selectHost_message);
				selectHostDialog.open();
				host = (IHost) selectHostDialog.getFirstResult();
			}

			if (host == null) {
				return null;
			}

			// custom launch conf name
			String fileNameWithoutExtension = script.getLocation().removeFileExtension().lastSegment();
			String configNamePrefix = MessageFormat.format("{0}_{1}_{2}", script.getProject().getName(), fileNameWithoutExtension, host); //$NON-NLS-1$

			wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(configNamePrefix));
			wc.setAttribute(LuaRemoteDebugConstant.NATURE, getNatureId());
			wc.setAttribute(LuaRemoteDebugConstant.PROJECT_NAME, script.getProject().getName());
			wc.setAttribute(LuaRemoteDebugConstant.SCRIPT_NAME, script.getProjectRelativePath().toPortableString());
			LuaRemoteLaunchConfigurationUtil.setConnectionId(wc, host);

			wc.setMappedResources(new IResource[] { script });
			config = wc.doSave();
		} catch (CoreException e) {
			Activator.logError("Unable to create a launch configuration from a LaunchShortcut", e); //$NON-NLS-1$
		}
		return config;
	}

	private List<IHost> findHosts() {
		IHost[] hosts = RSECorePlugin.getTheSystemRegistry().getHosts();
		ArrayList<IHost> newHostList = new ArrayList<IHost>();

		for (IHost host : hosts) {
			boolean isFileSubSystem = false;
			boolean isLuaSubSystem = false;

			for (ISubSystem subsystem : host.getSubSystems()) {
				if (subsystem instanceof IRemoteFileSubSystem) {
					isFileSubSystem = true;
				}
				if (subsystem instanceof LuaSubSystem) {
					isLuaSubSystem = true;
				}
			}
			if (isFileSubSystem && isLuaSubSystem) {
				newHostList.add(host);
			}
		}
		return newHostList;
	}
}
