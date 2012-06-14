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

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LuaSmartTypingPreferencePage extends AbstractConfigurationBlockPreferencePage {

	/**
	 * @see org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage#createConfigurationBlock(org.eclipse.dltk.ui.preferences.OverlayPreferenceStore)
	 */
	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(final OverlayPreferenceStore overlayPreferenceStore) {
		return new LuaSmartTypingConfigurationBlock(overlayPreferenceStore);
	}

	/**
	 * @see org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage#setPreferenceStore()
	 */
	protected void setPreferenceStore() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}/*
	 * @see org.eclipse.ui.internal.editors.text.AbstractConfigurationBlockPreferencePage#setDescription()
	 */

	protected void setDescription() {
		setDescription(Messages.LuaSmartTypingPreferencePageDescription);
	}

	protected Label createDescriptionLabel(final Composite parent) {
		final Label label = new Label(parent, SWT.HORIZONTAL);
		label.setText(Messages.LuaSmartTypingPreferencePageDescriptionLabel);
		return label;
	}
}
