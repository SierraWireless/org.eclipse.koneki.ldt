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
package org.eclipse.koneki.ldt.debug.ui.internal;

import org.eclipse.osgi.util.NLS;

//CHECKSTYLE NLS: OFF
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.debug.ui.internal.messages"; //$NON-NLS-1$
	public static String LuaDebugModelPresentation_ccode;
	public static String LuaDebugModelPresentation_pause_coroutine;
	public static String LuaDebugModelPresentation_running_coroutine;
	public static String LuaDebugModelPresentation_tail_return;
	public static String LuaDebugModelPresentation_unknown;
	public static String LuaDebugModelPresentation_upvalues;

	public static String LuaAttachMainTab_documentation_intro;
	public static String LuaAttachMainTab_connection_properties_group;
	public static String LuaAttachMainTab_documentation_link;
	public static String LuaAttachMainTab_idekey_label;
	public static String LuaAttachMainTab_localresolution_radiobutton;
	public static String LuaAttachMainTab_localresolution_textinfo;
	public static String LuaAttachMainTab_moduleresolution_radiobutton;
	public static String LuaAttachMainTab_moduleresolution_textinfo;
	public static String LuaAttachMainTab_path_label;
	public static String LuaAttachMainTab_replacepathresolution_radiobutton;
	public static String LuaAttachMainTab_replacepathresolution_textinfo;
	public static String LuaAttachMainTab_sourcemapping_group;
	public static String LuaAttachMainTab_timeout_label;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
// CHECKSTYLE NLS: ON
