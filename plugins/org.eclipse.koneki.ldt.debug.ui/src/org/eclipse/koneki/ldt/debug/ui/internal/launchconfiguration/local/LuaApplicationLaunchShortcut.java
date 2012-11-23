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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchHistory;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ProjectFragment;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;
import org.eclipse.dltk.launching.LaunchingMessages;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.launching.process.ScriptRuntimeProcessFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.koneki.ldt.core.LuaConstants;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.core.LuaUtils;
import org.eclipse.koneki.ldt.debug.core.internal.LuaDebugConstants;
import org.eclipse.koneki.ldt.debug.ui.internal.Activator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

/**
 * Run As strategy:<br>
 * launch the latest launch config for the selection<br>
 * if not found : <br>
 * 1) A file is selected <br>
 * -> create a new one <br>
 * 2) A folder is selected <br>
 * -> the folder have only 1 file -> do 1)<br>
 * -> the folder contains several files -> prompt the one to be selected, then do 1)<br>
 * 3) A project is selected<br>
 * -> there is a main module -> do 1)<br>
 * -> there is no main module -> do 2)<br>
 */
@SuppressWarnings("restriction")
public class LuaApplicationLaunchShortcut extends AbstractScriptLaunchShortcut {

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

		// As the Lua Application Run As menu is enable only when one item is selected (see extension point)
		// the following code just take in account the first element
		Object selection = search[0];

		// configuration to launch :
		ILaunchConfiguration config = null;

		// find existing
		config = findExistingLaunchConfiguration(selection, mode);

		// not found create one
		if (config == null)
			config = createLaunchConfiguration(selection);

		// Launch
		if (config != null)
			DebugUITools.launch(config, mode);
	}

	private ILaunchConfiguration createLaunchConfiguration(Object selection) {

		// script selection
		if (selection instanceof IFile) {
			return createLaunchConfiguration((IFile) selection);
		} else if (selection instanceof ISourceModule) {
			try {
				IResource correspondingResource = ((ISourceModule) selection).getCorrespondingResource();
				if (correspondingResource instanceof IFile)
					return createLaunchConfiguration((IFile) correspondingResource);
			} catch (ModelException e) {
				Activator.logError(NLS.bind("Unable to get corresponding resource for module {0}", selection), e); //$NON-NLS-1$
				return null;
			}
		}
		// source folder selection
		else if (selection instanceof IFolder) {
			IModelElement sourcefolder = DLTKCore.create((IFolder) selection);
			if (sourcefolder instanceof IScriptFolder)
				return createLaunchConfiguration((IScriptFolder) sourcefolder);
		} else if (selection instanceof IScriptFolder) {
			return createLaunchConfiguration((IScriptFolder) selection);
		}
		// project selection
		else if (selection instanceof IProject) {
			IScriptProject project = DLTKCore.create((IProject) selection);
			if (project != null)
				return createLaunchConfiguration(project);
		} else if (selection instanceof ProjectFragment) {
			IScriptProject scriptProject = ((ProjectFragment) selection).getScriptProject();
			if (scriptProject != null)
				return createLaunchConfiguration(scriptProject);
		} else if (selection instanceof IScriptProject) {
			return createLaunchConfiguration((IScriptProject) selection);
		}
		return null;
	}

	public ILaunchConfiguration createLaunchConfiguration(IFile script) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {
			ILaunchConfigurationType configType = getConfigurationType();

			// custom launch conf name
			String fileNameWithoutExtension = script.getLocation().removeFileExtension().lastSegment();
			String configNamePrefix = MessageFormat.format("{0}_{1}", script.getProject().getName(), fileNameWithoutExtension); //$NON-NLS-1$

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

	public ILaunchConfiguration createLaunchConfiguration(IParent sourcecontainer) {
		// Search scripts contains in this container
		IResource[] scripts = null;
		try {
			scripts = findScripts(new Object[] { sourcecontainer }, PlatformUI.getWorkbench().getProgressService());
		} catch (InterruptedException e) {
			return null;
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), LaunchingMessages.ScriptLaunchShortcut_0, e.getMessage());
			return null;
		}

		IResource script;
		if (scripts.length == 0) {
			// no file in folder
			MessageDialog.openError(getShell(), LaunchingMessages.ScriptLaunchShortcut_1, getSelectionEmptyMessage());
		} else if (scripts.length == 1) {
			// create a new one for this script
			return createConfiguration(scripts[0]);
		} else if (scripts.length > 1) {
			// prompt user to choose a script
			script = chooseScript(scripts, getScriptSelectionTitle());
			if (script != null) {
				return createLaunchConfiguration(scripts[0]);
			}
		}
		return null;
	}

	public ILaunchConfiguration createLaunchConfiguration(IScriptProject project) {
		// search a main file
		String defaultModuleName = new Path(LuaConstants.DEFAULT_MAIN_FILE).removeFileExtension().toString();
		IModuleSource mainModule = LuaUtils.getModuleSource(defaultModuleName, project);
		if (mainModule != null) {
			try {
				return createConfiguration(mainModule.getModelElement().getCorrespondingResource());
			} catch (ModelException e) {
				Activator.logWarning("Unable to find ressource corresponding to main module", e); //$NON-NLS-1$
			}
		}

		// if not found do the same as sourcefolder
		return createLaunchConfiguration((IParent) project);
	}

	/**
	 * find the last launch configuration for this container
	 */
	private ILaunchConfiguration findExistingLaunchConfiguration(Object container, String launchMode) {
		try {
			// get all existing config
			List<ILaunchConfiguration> candidateConfigs = retreiveLaunchConfiguration(launchMode);

			// search the first which match for this container
			candidateConfigs = filterConfig(container, candidateConfigs);

			if (!candidateConfigs.isEmpty()) {
				return candidateConfigs.get(0);
			}
		} catch (CoreException e) {
			Activator.logWarning("Unable to retrieve existing launch configuration.", e); //$NON-NLS-1$
		} catch (InterruptedException e) {
			return null;
		}

		return null;
	}

	/**
	 * get all existing launch configuration for the current type and launch mode sorted by favorites then history.
	 */
	private List<ILaunchConfiguration> retreiveLaunchConfiguration(String launchMode) throws CoreException {
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

	/**
	 * search the config in candidateConfigs list which match for this selection. <br>
	 * 
	 * see {@link org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut.getScriptResources(Object[], IProgressMonitor)}
	 */
	private List<ILaunchConfiguration> filterConfig(Object selection, List<ILaunchConfiguration> candidateConfigs) throws InterruptedException,
			CoreException {
		IResource[] scripts = findScripts(new Object[] { selection }, PlatformUI.getWorkbench().getProgressService());
		return filterConfig(Arrays.asList(scripts), candidateConfigs);
	}

	/**
	 * search the config in candidateConfigs list which match for this list of scripts.
	 */
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

	@Override
	protected void launch(final IResource script, final String mode) {
		searchAndLaunch(new Object[] { script }, mode, null, null);
	}
}