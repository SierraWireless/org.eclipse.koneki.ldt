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

import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

public interface LuaRemoteDebugConstant {

	String REMOTE_LAUNCH_CONFIGURATION_ID = "org.eclipse.koneki.ldt.remote.debug.core.luaremotedebug"; //$NON-NLS-1$

	String PROJECT_NAME = ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME;
	String SCRIPT_NAME = ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME;
	String HOST_ID = Activator.PLUGIN_ID + ".debug.hostid"; //$NON-NLS-1$

	String BREAK_ON_FIRST_LINE = ScriptLaunchConfigurationConstants.ENABLE_BREAK_ON_FIRST_LINE;
	String DBGP_LOGGING = ScriptLaunchConfigurationConstants.ENABLE_DBGP_LOGGING;

	String NATURE = ScriptLaunchConfigurationConstants.ATTR_SCRIPT_NATURE;
}
