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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;
import org.eclipse.koneki.ldt.core.LuaConstants;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.debug.core.LuaDebugConstants;

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

}
