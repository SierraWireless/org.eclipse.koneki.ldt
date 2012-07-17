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

import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.text.folding.SourceCodeFoldingPreferenceBlock;
import org.eclipse.jface.preference.PreferencePage;

/**
 * Javascript source code folding preferences.
 */
public class LuaCodeFoldingPreferenceBlock extends SourceCodeFoldingPreferenceBlock {

	public LuaCodeFoldingPreferenceBlock(OverlayPreferenceStore store, PreferencePage page) {
		super(store, page);
	}

	protected String getInitiallyFoldMethodsText() {
		return Messages.LuaFoldingPreferencePage_initiallyFoldLevelOneBlocks;
	}

	protected boolean supportsClassFolding() {
		return false;
	}
}
