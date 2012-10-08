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
package org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchHistory;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;
import org.eclipse.dltk.launching.LaunchingMessages;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.launching.process.ScriptRuntimeProcessFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.koneki.ldt.core.LuaConstants;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.debug.core.internal.LuaDebugConstants;
import org.eclipse.koneki.ldt.debug.ui.internal.Activator;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class LuaApplicationLaunchShortcut extends AbstractScriptLaunchShortcut {

	private String launchMode;

	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(LuaDebugConstants.LOCAL_LAUNCH_CONFIGURATION_ID);
	}

	@Override
	protected String getNatureId() {
		return LuaNature.ID;
	}

	@Override
	public void searchAndLaunch(Object[] search, String mode, String selectMessage, String emptyMessage) {
		this.launchMode = mode;

		// As the Lua Application Run As menu is enable only when one item is selected (see extension point)
		// the following code just take in account the first element
		Object selection = search[0];

		// 1 Launch last container config
		try {
			IContainer container = null;
			if (selection instanceof IContainer) {
				container = (IContainer) selection;
			} else if (selection instanceof IScriptProject || selection instanceof IScriptFolder || selection instanceof IProjectFragment) {
				container = (IContainer) ((IModelElement) selection).getCorrespondingResource();
			}

			if (container != null) {
				ILaunchConfiguration config = findLaunchConfiguration(container);
				if (config != null) {
					DebugUITools.launch(config, mode);
					return;
				}
			}
		} catch (ModelException e) {
			String message = MessageFormat.format("Unable to retreive Launch Configuration for the selection: {0}", selection); //$NON-NLS-1$
			Activator.logWarning(message, e);
			// CHECKSTYLE:OFF
		} catch (InterruptedException e) {
			// If the user cancel the search of last container launch configuration, there is nothing to do as we will let him choose the script is
			// want to launch
			// CHECKSTYLE:ON
		} catch (CoreException e) {
			String message = MessageFormat.format("Unable to retreive and launch Launch Configuration for the selection: {0}", selection); //$NON-NLS-1$
			Activator.logWarning(message, e);
		}

		// Search for script
		IResource[] scripts = null;
		try {
			scripts = findScripts(search, PlatformUI.getWorkbench().getProgressService());
		} catch (InterruptedException e) {
			return;
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), LaunchingMessages.ScriptLaunchShortcut_0, e.getMessage());
			return;
		}

		IResource script;
		if (scripts.length == 0) {
			MessageDialog.openError(getShell(), LaunchingMessages.ScriptLaunchShortcut_1, emptyMessage);
		} else if (scripts.length == 1) {
			// 2 launch most recent config for the file
			ILaunchConfiguration config = findLaunchConfiguration(scripts[0], getConfigurationType());
			if (config != null) {
				DebugUITools.launch(config, mode);
			}
		} else if (scripts.length > 1) {
			// 3 Try to get src/main.lua otherwise, prompt user to choose a script
			script = chooseScript(scripts, selectMessage);
			launch(script, mode);
		}
	}

	private ILaunchConfiguration findLaunchConfiguration(IContainer container) throws CoreException, InterruptedException {

		List<ILaunchConfiguration> candidateConfigs = retreiveLaunchConfiguration();
		candidateConfigs = filterConfig(container, candidateConfigs);

		if (!candidateConfigs.isEmpty()) {
			return candidateConfigs.get(0);
		}
		return null;
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut#findLaunchConfiguration(org.eclipse.core.resources.IResource,
	 *      org.eclipse.debug.core.ILaunchConfigurationType)
	 */
	@Override
	protected ILaunchConfiguration findLaunchConfiguration(IResource script, ILaunchConfigurationType configType) {
		try {
			List<ILaunchConfiguration> candidateConfigs = retreiveLaunchConfiguration();
			candidateConfigs = filterConfig(Arrays.asList(script), candidateConfigs);

			if (!candidateConfigs.isEmpty()) {
				return (ILaunchConfiguration) candidateConfigs.get(0);
			}
		} catch (CoreException e) {
			String message = MessageFormat.format("Unable to retreive Launch Configuration for the given script: {0}", script); //$NON-NLS-1$
			Activator.logWarning(message, e);
		}
		return createConfiguration(script);
	}

	private List<ILaunchConfiguration> retreiveLaunchConfiguration() throws CoreException {
		ILaunchGroup group = DebugUIPlugin.getDefault().getLaunchConfigurationManager().getLaunchGroup(getConfigurationType(), launchMode);
		LaunchHistory history = DebugUIPlugin.getDefault().getLaunchConfigurationManager().getLaunchHistory(group.getIdentifier());

		List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
		candidateConfigs.addAll(Arrays.asList(history.getFavorites()));
		candidateConfigs.addAll(Arrays.asList(history.getHistory()));

		// if no history for our resource, let filter existing configs
		if (candidateConfigs.isEmpty()) {
			candidateConfigs.addAll(Arrays.asList(DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(getConfigurationType())));
		}
		return candidateConfigs;
	}

	private List<ILaunchConfiguration> filterConfig(IContainer container, List<ILaunchConfiguration> candidateConfigs) throws InterruptedException,
			CoreException {
		IResource[] scripts = findScripts(new Object[] { container }, PlatformUI.getWorkbench().getProgressService());
		return filterConfig(Arrays.asList(scripts), candidateConfigs);
	}

	private List<ILaunchConfiguration> filterConfig(List<IResource> scripts, List<ILaunchConfiguration> configs) throws CoreException {
		List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
		for (ILaunchConfiguration config : configs) {
			for (IResource script : scripts) {
				if (config.getAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME, Util.EMPTY_STRING).equals(
						script.getProjectRelativePath().toString())
						&& config.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, Util.EMPTY_STRING).equals(
								script.getProject().getName()) && config.getType().equals(getConfigurationType())) {
					candidateConfigs.add(config);
				}
			}
		}
		return candidateConfigs;
	}

	/**
	 * By default, select the main.lua script, if it does't exist, ask the user.
	 * 
	 * @see org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut#chooseScript(org.eclipse.core.resources.IResource[],
	 *      java.lang.String)
	 */
	@Override
	protected IResource chooseScript(IResource[] scripts, String title) {
		IPath defaultPath = new Path(LuaConstants.SOURCE_FOLDER).append(LuaConstants.DEFAULT_MAIN_FILE);
		for (IResource script : scripts) {
			IPath scriptPath = script.getLocation();

			// test if the script path ends with the default path
			if (scriptPath.segmentCount() > defaultPath.segmentCount()) {

				// remove the beginning of the script to test the end
				int numberOfSegmentToTest = scriptPath.segmentCount() - defaultPath.segmentCount();
				scriptPath = scriptPath.removeFirstSegments(numberOfSegmentToTest);

				if (scriptPath.equals(defaultPath)) {
					return script;
				}
			}
		}
		return super.chooseScript(scripts, title);
	}

	/**
	 * Copy of the super method with a custom config name generation
	 */
	@Override
	protected ILaunchConfiguration createConfiguration(IResource script) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {
			ILaunchConfigurationType configType = getConfigurationType();

			// custom launch conf name
			String fileNameWithoutExtension = script.getLocation().removeFileExtension().lastSegment();
			String configNamePrefix = MessageFormat.format("{0}#{1}", script.getProject().getName(), fileNameWithoutExtension); //$NON-NLS-1$

			wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(configNamePrefix));
			wc.setAttribute(ScriptLaunchConfigurationConstants.ATTR_SCRIPT_NATURE, getNatureId());
			wc.setAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, script.getProject().getName());
			wc.setAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME, script.getProjectRelativePath().toPortableString());
			wc.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, ScriptRuntimeProcessFactory.PROCESS_FACTORY_ID);

			wc.setMappedResources(new IResource[] { script });
			config = wc.doSave();
		} catch (CoreException e) {
			Activator.logError("Unable to create a launch configuration from a LaunchShortcut", e); //$NON-NLS-1$
		}
		return config;
	}
}
