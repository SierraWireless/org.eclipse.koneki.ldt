/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 ******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.preferences;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.preferences.UserLibraryPreferencePage;
import org.eclipse.koneki.ldt.core.internal.LuaLanguageToolkit;

public class LuaUserLibraryPreferencePage extends UserLibraryPreferencePage {

	/**
	 * @see org.eclipse.dltk.ui.preferences.UserLibraryPreferencePage#getLanguageToolkit()
	 */
	@Override
	protected IDLTKLanguageToolkit getLanguageToolkit() {
		return LuaLanguageToolkit.getDefault();
	}

}