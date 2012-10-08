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
package org.eclipse.koneki.ldt.remote.debug.ui.internal.launch;

import org.eclipse.osgi.util.NLS;

/**
 * TODO Comment this class
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.remote.debug.ui.internal.launch.messages"; //$NON-NLS-1$
	public static String LuaRemoteLaunchShortcut_notargetdialog_message;
	public static String LuaRemoteLaunchShortcut_notargetdialog_title;
	public static String LuaRemoteLaunchShortcut_selectHost_message;
	public static String LuaRemoteLaunchShortcut_selectHostDialog_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
