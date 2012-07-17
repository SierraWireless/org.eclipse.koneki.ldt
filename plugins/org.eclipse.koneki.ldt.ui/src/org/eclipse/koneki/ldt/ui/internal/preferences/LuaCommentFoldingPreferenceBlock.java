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
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.dltk.ui.text.folding.DocumentationFoldingPreferenceBlock;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Group;

public class LuaCommentFoldingPreferenceBlock extends DocumentationFoldingPreferenceBlock {

	public LuaCommentFoldingPreferenceBlock(OverlayPreferenceStore store, PreferencePage page) {
		super(store, page);
	}

	protected String getInitiallyFoldDocsText() {
		return Messages.LuaFoldingPreferencePage_initiallyFoldDoc;
	}

	protected boolean supportsDocFolding() {
		return true;
	}

	/**
	 * Overriding this method to not have the "Initially fold headers" check box.
	 * 
	 * @see org.eclipse.dltk.ui.text.folding.DocumentationFoldingPreferenceBlock#addInitiallyFoldOptions(org.eclipse.swt.widgets.Group)
	 */
	protected void addInitiallyFoldOptions(Group group) {
		createCheckBox(group, PreferencesMessages.FoldingConfigurationBlock_initiallyFoldComments, PreferenceConstants.EDITOR_FOLDING_INIT_COMMENTS);

		if (supportsDocFolding()) {
			createCheckBox(group, getInitiallyFoldDocsText(), PreferenceConstants.EDITOR_FOLDING_INIT_DOCS);
		}
	}

}
