/*******************************************************************************
 * Copyright (c) 2012 Marc-Andre Laperle and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marc-Andre Laperle - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.editor;

import org.eclipse.osgi.util.NLS;

//CHECKSTYLE NLS: OFF
public final class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.editor.Messages";//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	public static String MemberFilterActionGroup_hide_local_functions_label;
	public static String MemberFilterActionGroup_hide_local_functions_tooltip;
	public static String MemberFilterActionGroup_hide_local_functions_description;

	public static String MemberFilterActionGroup_hide_local_functions_error;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
// CHECKSTYLE NLS: ON
