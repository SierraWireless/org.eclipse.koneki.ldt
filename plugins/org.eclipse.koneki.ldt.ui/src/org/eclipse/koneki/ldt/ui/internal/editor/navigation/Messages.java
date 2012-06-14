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
package org.eclipse.koneki.ldt.ui.internal.editor.navigation;

import org.eclipse.osgi.util.NLS;

// CHECKSTYLE NLS: OFF
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.ui.internal.editor.navigation.messages"; //$NON-NLS-1$
	public static String LuaCompletionProvidersFlags;
	public static String MemberFilterActionGroup_hide_local_functions_label;
	public static String MemberFilterActionGroup_hide_local_functions_tooltip;
	public static String MemberFilterActionGroup_hide_local_functions_description;

	public static String MemberFilterActionGroup_hide_local_functions_error;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
// CHECKSTYLE NLS: ON
