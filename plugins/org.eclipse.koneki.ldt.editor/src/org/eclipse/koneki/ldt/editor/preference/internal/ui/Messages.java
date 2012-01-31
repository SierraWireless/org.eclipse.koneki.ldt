/*******************************************************************************
 * Copyright (c) 2011, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.editor.preference.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.editor.preference.internal.ui.messages"; //$NON-NLS-1$
	// CHECKSTYLE NLS: OFF
	public static String LuaSmartConfigurationBlockBraces;
	public static String LuaSmartConfigurationBlockBrackets;
	public static String LuaSmartConfigurationBlockStrings;
	public static String LuaSmartConfigurationBlockTitle;
	public static String LuaSmartTypingPreferencePageDescription;
	public static String LuaSmartTypingPreferencePageDescriptionLabel;
	public static String LuaEditorPreferencePageDescription;
	public static String LuaEditorColoringPreferencePage_multiLineComment;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
// CHECKSTYLE NLS: ON
