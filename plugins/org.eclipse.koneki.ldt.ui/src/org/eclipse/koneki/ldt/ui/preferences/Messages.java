/*******************************************************************************
 * Copyright (c) 2012	 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.preferences;

import org.eclipse.osgi.util.NLS;

// CHECKSTYLE NLS: OFF
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.ui.preferences.messages"; //$NON-NLS-1$
	public static String GlobalLuaPreferencePage_description;
	public static String LuaExecutionEnvironmentPreferencePage_addbutton;
	public static String LuaExecutionEnvironmentPreferencePage_removeButton;
	public static String LuaExecutionEnvironmentPreferencePageInstallationAborted;
	public static String LuaExecutionEnvironmentPreferencePageInvalidFile;
	public static String LuaExecutionEnvironmentPreferencePageIOProblemTitle;
	public static String LuaExecutionEnvironmentPreferencePageNoCurrentSelection;
	public static String LuaExecutionEnvironmentPreferencePageProblemWithFile;
	public static String LuaExecutionEnvironmentPreferencePageRemoveDialogTitle;
	public static String LuaExecutionEnvironmentPreferencePageTitle;
	public static String LuaExecutionEnvironmentPreferencePageUnableToDelete;
	public static String LuaExecutionEnvironmentPreferencePageUnableToDeleteEE;
	public static String LuaExecutionEnvironmentPreferencePageUnableToInstallTitle;
	public static String LuaTodoTaskPreferencePage_description;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
// CHECKSTYLE NLS: ON
