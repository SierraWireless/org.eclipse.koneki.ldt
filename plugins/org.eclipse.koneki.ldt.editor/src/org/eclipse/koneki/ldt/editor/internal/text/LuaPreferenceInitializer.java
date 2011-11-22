/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/

package org.eclipse.koneki.ldt.editor.internal.text;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;

public class LuaPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		DLTKUIPlugin.getDefault().getPreferenceStore().setDefault(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE, true);

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);

		// Initialize DLTK default values
		PreferenceConstants.initializeDefaultValues(store);

		// Initialize Lua constants
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_SINGLE_LINE_COMMENT, new RGB(63, 127, 95));
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_MULTI_LINE_COMMENT, new RGB(63, 95, 191));
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_KEYWORD, new RGB(127, 0, 85));
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_STRING, new RGB(42, 0, 255));
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_NUMBER, new RGB(185, 20, 20));

		store.setDefault(ILuaColorConstants.LUA_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(ILuaColorConstants.LUA_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);

		store.setDefault(ILuaColorConstants.LUA_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(ILuaColorConstants.LUA_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);

		store.setDefault(ILuaColorConstants.LUA_KEYWORD + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(ILuaColorConstants.LUA_KEYWORD + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);

		store.setDefault(PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_DOCS_FOLDING_ENABLED, true);

	}

}
