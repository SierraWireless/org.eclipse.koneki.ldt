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
package org.eclipse.koneki.ldt.remote.debug.ui.internal.launch.tab;

import org.eclipse.osgi.util.NLS;

//CHECKSTYLE NLS: OFF
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.remote.debug.ui.internal.launch.tab.messages"; //$NON-NLS-1$

	public static String LuaRemoteLaunchConfigurationMainTab_debuggroup_title;

	public static String LuaRemoteLaunchConfigurationMainTab_scriptgroup_title;

	public static String LuaRemoteMainTab_projectgroup_browseprojectbutton;
	public static String LuaRemoteMainTab_projectgroup_title;
	public static String LuaRemoteMainTab_tabname;
	public static String LuaRemoteMainTab_targetgroup_hostlabel;
	public static String LuaRemoteMainTab_targetgroup_title;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
// CHECKSTYLE NLS: ON
