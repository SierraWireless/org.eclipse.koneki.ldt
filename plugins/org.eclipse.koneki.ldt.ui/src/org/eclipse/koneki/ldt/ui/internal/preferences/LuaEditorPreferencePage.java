/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.preferences;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.ui.preferences.EditorConfigurationBlock;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.koneki.ldt.ui.internal.Activator;

public class LuaEditorPreferencePage extends AbstractConfigurationBlockPreferencePage {

	@Override
	protected void setDescription() {
		setDescription(Messages.LuaEditorPreferencePageDescription);
	}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		// Use of the DLTK Editor preference page, where the 0 means we don't want any of optional parameters.
		return new EditorConfigurationBlock(this, overlayPreferenceStore, 0) {

			// Filter editor color parameters
			protected EditorColorItem[] createColorListModel() {
				return new EditorColorItem[] {
						new EditorColorItem(PreferencesMessages.EditorPreferencePage_matchingBracketsHighlightColor,
								PreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR),
						new EditorColorItem(PreferencesMessages.EditorPreferencePage_backgroundForMethodParameters,
								PreferenceConstants.CODEASSIST_PARAMETERS_BACKGROUND),
						new EditorColorItem(PreferencesMessages.EditorPreferencePage_foregroundForMethodParameters,
								PreferenceConstants.CODEASSIST_PARAMETERS_FOREGROUND), };
			}
		};
	}
}
