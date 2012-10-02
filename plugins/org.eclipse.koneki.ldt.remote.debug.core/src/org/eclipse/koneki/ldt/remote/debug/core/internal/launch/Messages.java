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
package org.eclipse.koneki.ldt.remote.debug.core.internal.launch;

import org.eclipse.osgi.util.NLS;

// CHECKSTYLE NLS: OFF
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.remote.debug.core.internal.launch.messages"; //$NON-NLS-1$
	public static String LuaRemoteLaunchConfigurationDelegate_error_connectionfailed;
	public static String LuaRemoteLaunchConfigurationDelegate_error_filealreadyexist;
	public static String LuaRemoteLaunchConfigurationDelegate_error_noluasystem;
	public static String LuaRemoteLaunchConfigurationDelegate_error_noremotefilesystem;
	public static String LuaRemoteLaunchConfigurationDelegate_error_unable_to_define_ideip;
	public static String LuaRemoteLaunchConfigurationDelegate_error_unabletoaccestoremoteapplicationdir;
	public static String LuaRemoteLaunchConfigurationDelegate_error_unabletouploaddebuggerfiles;
	public static String LuaRemoteLaunchConfigurationUtil_error_closed_project;
	public static String LuaRemoteLaunchConfigurationUtil_error_no_host_selected;
	public static String LuaRemoteLaunchConfigurationUtil_error_no_lua_service;
	public static String LuaRemoteLaunchConfigurationUtil_error_no_luacommand;
	public static String LuaRemoteLaunchConfigurationUtil_error_no_outputdir;
	public static String LuaRemoteLaunchConfigurationUtil_error_no_project;
	public static String LuaRemoteLaunchConfigurationUtil_error_no_remote_file_service;
	public static String LuaRemoteLaunchConfigurationUtil_error_no_script_selected;
	public static String LuaRemoteLaunchConfigurationUtil_error_not_lua_project;
	public static String LuaRemoteLaunchConfigurationUtil_error_script_desnt_exist;
	public static String LuaRemoteLaunchConfigurationUtil_error_script_not_lua_file;
	public static String LuaRemoteLaunchConfigurationUtil_error_unexisted_project;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
// CHECKSTYLE NLS: ON
