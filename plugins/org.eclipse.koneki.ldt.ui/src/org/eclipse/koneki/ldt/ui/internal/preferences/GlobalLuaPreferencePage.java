/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 ******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.preferences;

import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlock;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class GlobalLuaPreferencePage extends AbstractConfigurationBlockPreferencePage {

	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new AbstractConfigurationBlock(overlayPreferenceStore, this) {
			public Control createControl(Composite parent) {
				Composite composite = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_HORIZONTAL);
				return composite;
			}
		};
	}

	protected String getHelpId() {
		return null;
	}

	protected void setDescription() {
		setDescription(Messages.GlobalLuaPreferencePage_description);
	}

	protected void setPreferenceStore() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
}