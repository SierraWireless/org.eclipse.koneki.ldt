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
package org.eclipse.koneki.ldt.ui.internal.editor.formatter;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.formatter.AbstractScriptFormatterFactory;
import org.eclipse.dltk.ui.formatter.IFormatterModifyDialog;
import org.eclipse.dltk.ui.formatter.IFormatterModifyDialogOwner;
import org.eclipse.dltk.ui.formatter.IScriptFormatter;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.preferences.LuaFormatterModifyDialog;

public class LuaFormatterFactory extends AbstractScriptFormatterFactory {
	private static final String SAMPLE_FILE_PATH = "/sample/formatter.lua"; //$NON-NLS-1$
	private final String[] preferenceKeys = new String[] { LuaFormatterPreferenceConstants.FORMATTER_INDENTATION_SIZE,
			LuaFormatterPreferenceConstants.FORMATTER_TAB_CHAR, LuaFormatterPreferenceConstants.FORMATTER_TAB_SIZE,
			LuaFormatterPreferenceConstants.FORMATTER_INDENT_TABLE_VALUES };

	public LuaFormatterFactory() {
	}

	@Override
	public PreferenceKey[] getPreferenceKeys() {
		final PreferenceKey[] preferences = new PreferenceKey[preferenceKeys.length];
		for (int p = 0; p < preferences.length; p++) {
			preferences[p] = new PreferenceKey(Activator.PLUGIN_ID, preferenceKeys[p]);
		}
		return preferences;
	}

	@Override
	protected PreferenceKey getProfilesKey() {
		return new PreferenceKey(Activator.PLUGIN_ID, LuaFormatterPreferenceConstants.FORMATTER_PROFILES);
	}

	@Override
	public PreferenceKey getActiveProfileKey() {
		return new PreferenceKey(Activator.PLUGIN_ID, LuaFormatterPreferenceConstants.FORMATTER_ACTIVE_PROFILE);
	}

	@Override
	public IScriptFormatter createFormatter(String lineDelimiter, Map<String, String> preferences) {
		return new LuaFormatter(lineDelimiter, preferences);
	}

	@Override
	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner) {
		return new LuaFormatterModifyDialog(dialogOwner, this);
	}

	public URL getPreviewContent() {
		return getPreviewSample();
	}

	public static URL getPreviewSample() {
		final URL sample = Platform.getBundle(Activator.PLUGIN_ID).getEntry(SAMPLE_FILE_PATH);
		try {
			return FileLocator.toFileURL(sample);
		} catch (IOException e) {
			Activator.logError(Messages.LuaFormatterFactoryPreviewNotFound, e);
			return sample;
		}
	}
}
