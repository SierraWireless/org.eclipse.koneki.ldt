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
package org.eclipse.koneki.ldt.debug.core.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;

public class LuaDebugPreferenceInitializer extends AbstractPreferenceInitializer {

	public LuaDebugPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences store = new DefaultScope().getNode(Activator.PLUGIN_ID);

		store.putBoolean(DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE, false);
		store.putBoolean(DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING, true);
		store.putBoolean(DLTKDebugPreferenceConstants.PREF_DBGP_SHOW_SCOPE_GLOBAL, true);
		store.putBoolean(DLTKDebugPreferenceConstants.PREF_DBGP_SHOW_SCOPE_CLASS, true);
		store.putBoolean(DLTKDebugPreferenceConstants.PREF_DBGP_SHOW_SCOPE_LOCAL, true);

	}
}
