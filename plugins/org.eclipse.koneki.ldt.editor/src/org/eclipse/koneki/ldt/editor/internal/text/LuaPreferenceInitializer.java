/*******************************************************************************
 * Copyright (c) 2009, 2012 Sierra Wireless and others.
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
import org.eclipse.dltk.compiler.task.TaskTagUtils;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlightingUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;

public class LuaPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		DLTKUIPlugin.getDefault().getPreferenceStore().setDefault(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE, true);

		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);

		// Initialize DLTK default values
		PreferenceConstants.initializeDefaultValues(store);

		// Initialize Lua code color and style constants
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_SINGLE_LINE_COMMENT, new RGB(63, 127, 95));
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_MULTI_LINE_COMMENT, new RGB(63, 95, 191));
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_KEYWORD, new RGB(127, 0, 85));
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_STRING, new RGB(42, 0, 255));
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_NUMBER, new RGB(185, 20, 20));
		PreferenceConverter.setDefault(store, ILuaColorConstants.COMMENT_TASK_TAGS, new RGB(127, 159, 191));

		store.setDefault(ILuaColorConstants.LUA_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(ILuaColorConstants.LUA_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);

		store.setDefault(ILuaColorConstants.LUA_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(ILuaColorConstants.LUA_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);

		store.setDefault(ILuaColorConstants.LUA_KEYWORD + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(ILuaColorConstants.LUA_KEYWORD + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		store.setDefault(ILuaColorConstants.COMMENT_TASK_TAGS + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);

		// Enable code folding
		store.setDefault(PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_DOCS_FOLDING_ENABLED, true);

		// Enable auto close
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_BRACES, true);
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_BRACKETS, true);
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_STRINGS, true);

		// Content-assist related preferences
		store.setDefault(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS, ".:"); //$NON-NLS-1$

		TaskTagUtils.initializeDefaultValues(org.eclipse.koneki.ldt.Activator.getDefault().getPluginPreferences());

		// Semantic highlighting preferences initialization
		SemanticHighlightingUtils.initializeDefaultValues(store, Activator.getDefault().getTextTools().getSemanticHighlightings());

		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_LOCAL_VARIABLE, new RGB(103, 103, 103));
		PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_GLOBAL_VARIABLE, new RGB(0, 0, 0));
		store.setDefault(ILuaColorConstants.LUA_GLOBAL_VARIABLE + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
	}

}
