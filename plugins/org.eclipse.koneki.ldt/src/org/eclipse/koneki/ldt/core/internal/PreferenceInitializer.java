/*******************************************************************************
 * Copyright (c) 2013 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.core.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public static final String EE_DEFAULT_ID = "EE__default_id"; //$NON-NLS-1$

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(DefaultScope.INSTANCE, LuaLanguageToolkit.getDefault()
				.getPreferenceQualifier());

		preferenceStore.setDefault(EE_DEFAULT_ID, "lua-5.1"); //$NON-NLS-1$
	}
}
