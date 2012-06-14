/*******************************************************************************
 * Copyright (c) 2011-2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.editor.formatter.LuaFormatterPreferenceConstants;

public class LuaFormatterPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(LuaFormatterPreferenceConstants.FORMATTER_ID, Util.EMPTY_STRING);
		store.setDefault(LuaFormatterPreferenceConstants.FORMATTER_INDENTATION_SIZE, 4);
		store.setDefault(LuaFormatterPreferenceConstants.FORMATTER_TAB_SIZE, 4);
		store.setDefault(LuaFormatterPreferenceConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.TAB);
		store.setDefault(LuaFormatterPreferenceConstants.FORMATTER_INDENT_TABLE_VALUES, true);
	}

}
