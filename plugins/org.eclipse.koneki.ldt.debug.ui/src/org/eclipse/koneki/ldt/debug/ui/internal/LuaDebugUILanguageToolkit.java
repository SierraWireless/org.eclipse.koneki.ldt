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
package org.eclipse.koneki.ldt.debug.ui.internal;

import org.eclipse.dltk.debug.ui.AbstractDebugUILanguageToolkit;
import org.eclipse.dltk.debug.ui.IDLTKDebugUILanguageToolkit;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.koneki.ldt.core.internal.LuaLanguageToolkit;

public class LuaDebugUILanguageToolkit extends AbstractDebugUILanguageToolkit implements IDLTKDebugUILanguageToolkit {

	@Override
	public String getDebugModelId() {
		return LuaLanguageToolkit.getDefault().getNatureId() + ".debugModel"; //$NON-NLS-1$
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

}
