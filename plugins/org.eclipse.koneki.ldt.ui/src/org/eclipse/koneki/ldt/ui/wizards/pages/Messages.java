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
package org.eclipse.koneki.ldt.ui.wizards.pages;

import org.eclipse.osgi.util.NLS;

//CHECKSTYLE NLS: OFF
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.ui.wizards.pages.messages"; //$NON-NLS-1$
	public static String LuaExecutionEnvironmentGroupManageExecutionEnvironment;
	public static String LuaExecutionEnvironmentGroupNoEEForProjectCreation;
	public static String LuaExecutionEnvironmentGroupSelectEE;
	public static String LuaExecutionEnvironmentGroupTitle;
	public static String LuaProjecSettingsPageLabel;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
// CHECKSTYLE NLS: ON
