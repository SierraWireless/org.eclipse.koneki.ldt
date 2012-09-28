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

	String HOST_ID = Activator.PLUGIN_ID + ".debug.hostid"; //$NON-NLS-1$
	String PROJECT_NAME = ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME;
	String ENV_VAR_KEY_DBGP_IDE_KEY = null;
	String ENV_VAR_KEY_DBGP_IDE_PORT = null;
}
