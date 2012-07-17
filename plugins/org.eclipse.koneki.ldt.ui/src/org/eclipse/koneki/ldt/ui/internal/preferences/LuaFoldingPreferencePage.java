/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/

package org.eclipse.koneki.ldt.ui.internal.preferences;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.text.folding.DefaultFoldingPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.text.folding.IFoldingPreferenceBlock;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.koneki.ldt.ui.internal.Activator;

/**
 * Lua editor folding options.
 */
public final class LuaFoldingPreferencePage extends AbstractConfigurationBlockPreferencePage {
	protected void setPreferenceStore() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new DefaultFoldingPreferenceConfigurationBlock(overlayPreferenceStore, this) {
			protected IFoldingPreferenceBlock createDocumentationBlock(OverlayPreferenceStore store, PreferencePage page) {
				return new LuaCommentFoldingPreferenceBlock(store, page);
			}

			protected IFoldingPreferenceBlock createSourceCodeBlock(OverlayPreferenceStore store, PreferencePage page) {
				return new LuaCodeFoldingPreferenceBlock(store, page);
			}
		};
	}
}
