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
package org.eclipse.koneki.ldt.ui.internal.preferences;

import java.util.ArrayList;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class LuaSmartTypingConfigurationBlock extends AbstractConfigurationBlock {

	/**
	 * @param store
	 */
	public LuaSmartTypingConfigurationBlock(final OverlayPreferenceStore store) {
		super(store);
		store.addKeys(createOverlayStoreKeys());
	}

	/**
	 * @see org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(final Composite parent) {
		final Composite control = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		control.setLayout(layout);

		final Composite composite = createSubsection(control, null, Messages.LuaSmartConfigurationBlockTitle);
		addAutoclosingSection(composite);
		return control;
	}

	private void addAutoclosingSection(final Composite composite) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		addCheckBox(composite, Messages.LuaSmartConfigurationBlockStrings, PreferenceConstants.EDITOR_CLOSE_STRINGS, 0);
		addCheckBox(composite, Messages.LuaSmartConfigurationBlockBrackets, PreferenceConstants.EDITOR_CLOSE_BRACKETS, 0);
		addCheckBox(composite, Messages.LuaSmartConfigurationBlockBraces, PreferenceConstants.EDITOR_CLOSE_BRACES, 0);
	}

	private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {

		ArrayList<OverlayPreferenceStore.OverlayKey> overlayKeys = new ArrayList<OverlayPreferenceStore.OverlayKey>();

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_STRINGS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_BRACKETS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_BRACES));
		final OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return keys;

	}
}
