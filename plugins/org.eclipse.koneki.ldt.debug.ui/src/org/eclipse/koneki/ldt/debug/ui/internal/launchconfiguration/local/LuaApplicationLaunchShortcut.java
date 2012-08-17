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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.launching.process.ScriptRuntimeProcessFactory;
import org.eclipse.koneki.ldt.core.LuaConstants;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.debug.core.LuaDebugConstants;
import org.eclipse.koneki.ldt.debug.ui.internal.Activator;

public class LuaApplicationLaunchShortcut extends AbstractScriptLaunchShortcut {

	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(LuaDebugConstants.LOCAL_LAUNCH_CONFIGURATION_ID);
	}

	@Override
	protected String getNatureId() {
		return LuaNature.ID;
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
