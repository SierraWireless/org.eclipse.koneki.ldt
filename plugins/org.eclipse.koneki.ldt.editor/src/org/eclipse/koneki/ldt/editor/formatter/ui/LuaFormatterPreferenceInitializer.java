package org.eclipse.koneki.ldt.editor.formatter.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.koneki.ldt.editor.formatter.LuaFormatterPreferenceConstants;

public class LuaFormatterPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(LuaFormatterPreferenceConstants.FORMATTER_ID, Util.EMPTY_STRING);
		store.setDefault(LuaFormatterPreferenceConstants.FORMATTER_INDENTATION_SIZE, 4);
		store.setDefault(LuaFormatterPreferenceConstants.FORMATTER_TAB_SIZE, 4);
		store.setDefault(LuaFormatterPreferenceConstants.FORMATTER_TAB_CHAR, "tab"); //$NON-NLS-1$
	}

}
