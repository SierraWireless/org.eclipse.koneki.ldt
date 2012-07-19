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
package org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local;

import org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab;
import org.eclipse.koneki.ldt.core.LuaNature;

public class LuaMainLaunchConfigurationTab extends MainLaunchConfigurationTab {

	public LuaMainLaunchConfigurationTab(String mode) {
		super(mode);
	}

	@Override
	public String getNatureID() {
		return LuaNature.ID;
	}
}
