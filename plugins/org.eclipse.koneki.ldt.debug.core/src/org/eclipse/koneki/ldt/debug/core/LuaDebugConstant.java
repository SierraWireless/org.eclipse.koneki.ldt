/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.core;

import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

/**
 * Constant for attribute of Lua Embedded launch configuration
 */
public interface LuaDebugConstant {

	/**
	 * the project name
	 */
	String PROJECT_NAME = ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME;

	/**
	 * the ID of the chosen host
	 */
	String HOST_ID = Activator.PLUGIN_ID + ".debug.hostid"; //$NON-NLS-1$

	/**
	 * the lua command path
	 */
	String LUA_COMMAND_PATH = Activator.PLUGIN_ID + ".debug.luacommandpath"; //$NON-NLS-1$
	String DEFAULT_LUA_COMMAND_PATH = "/usr/ReadyAgent/bin/lua"; //$NON-NLS-1$
	/**
	 * the lua path (used for module resolution)
	 */
	String LUA_PATH = Activator.PLUGIN_ID + ".debug.luapath"; //$NON-NLS-1$
	String DEFAULT_LUA_PATH = "/usr/ReadyAgent/lib/?.lua;/usr/ReadyAgent/lua/?.lua;./?.lua"; //$NON-NLS-1$
	/**
	 * the lua cpath (used for module resolution)
	 */
	String LUA_CPATH = Activator.PLUGIN_ID + ".debug.luacpath"; //$NON-NLS-1$
	String DEFAULT_LUA_CPATH = "/usr/ReadyAgent/lib/?.so;/usr/ReadyAgent/lua/?.so"; //$NON-NLS-1$

	/**
	 * the path where the application will be paste.
	 */
	String REMOTE_APPLICATION_PATH = Activator.PLUGIN_ID + ".debug.remoteapplicationpath"; //$NON-NLS-1$
	String DEFAULT_REMOTE_APPLICATION_PATH = "/tmp"; //$NON-NLS-1$

	/**
	 * the Lua Inspector port
	 */
	String LUA_INSPECTOR_PORT = Activator.PLUGIN_ID + ".debug.inspectorPort"; //$NON-NLS-1$
	String LUA_INSPECTOR_DEFAULT_PORT = "12333"; //$NON-NLS-1$

	/**
	 * The identifier of Lua application launch configuration type
	 */
	String LUA_LAUNCH_CONFIG_TYPE = Activator.PLUGIN_ID + ".remoteluaapplication"; //$NON-NLS-1$

	/**
	 * Default Env vars
	 */
	String ENV_VAR_KEY_LD_LIBRARY = "LD_LIBRARY_PATH"; //$NON-NLS-1$
	String ENV_VAR_DEFAULT_VAL_LD_LIBRARY = "$LD_LIBRARY_PATH:/usr/ReadyAgent/lib"; //$NON-NLS-1$
	String ENV_VAR_KAY_LUA_PATH = "LUA_PATH"; //$NON-NLS-1$
	String ENV_VAR_KEY_LUA_CPATH = "LUA_CPATH"; //$NON-NLS-1$
	String ENV_VAR_KEY_LUA_INSPECTOR = "LUA_INSPECTOR_PORT"; //$NON-NLS-1$
	String ENV_VAR_KEY_DBGP_IDE_KEY = "DBGP_IDEKEY";//$NON-NLS-1$
	String ENV_VAR_KEY_DBGP_IDE_HOST = "DBGP_IDEHOST";//$NON-NLS-1$
	String ENV_VAR_KEY_DEFAULT_VAL_DBGP_IDE_HOST = "192.168.14.100";//$NON-NLS-1$
	String ENV_VAR_KEY_DBGP_IDE_PORT = "DBGP_IDEPORT";//$NON-NLS-1$

	/**
	 * Type name constants
	 */
	String TYPE_TABLE = "table"; //$NON-NLS-1$
	String TYPE_SEQUENCE = "sequence"; //$NON-NLS-1$
	String TYPE_MULTIVAL = "multival"; //$NON-NLS-1$
	String TYPE_LUAFUNC = "function (Lua)"; //$NON-NLS-1$
	String TYPE_SPECIAL = "special"; //$NON-NLS-1$

}
