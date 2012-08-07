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
package org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local.tab;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.debug.ui.launcher.WorkingDirectoryBlock;
import org.eclipse.koneki.ldt.core.internal.Activator;

public class LuaArgumentsTabWorkingDirectoryBlock extends WorkingDirectoryBlock {

	private static final String LAUNCH_CONFIGURATION_WORKING_DIRECTORY = "${workspace_loc}"; //$NON-NLS-1$

	@Override
	protected void setDefaultWorkingDir() {

		// Default parent working directory should be project root
		final ILaunchConfiguration config = getCurrentLaunchConfiguration();
		if (config != null) {
			try {
				final IScriptProject project = getProject(config);
				if (project != null) {

					// Now that we have current project we can compose default parent directory path
					final IEnvironment environment = EnvironmentManager.getEnvironment(project);
					final String separator;
					if (environment != null) {
						separator = Character.toString(environment.getSeparatorChar());
					} else {
						separator = System.getProperty("file.separator"); //$NON-NLS-1$
					}
					final String projectName = project.getProject().getName();
					final String path = MessageFormat.format("{0}{1}{2}", LAUNCH_CONFIGURATION_WORKING_DIRECTORY, separator, projectName); //$NON-NLS-1$
					setDefaultWorkingDirectoryText(path);
					return;
				}
			} catch (final CoreException ce) {
				Activator.logWarning("Unable to retrive current project name from environment to compute parent working directory.", ce); //$NON-NLS-1$
			}
		}

		// When project name is not available, we will use the workspace
		setDefaultWorkingDirectoryText(LAUNCH_CONFIGURATION_WORKING_DIRECTORY);
	}

}
