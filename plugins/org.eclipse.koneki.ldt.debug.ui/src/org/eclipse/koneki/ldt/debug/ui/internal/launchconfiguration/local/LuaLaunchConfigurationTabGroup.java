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

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local.tab.LuaArgumentsTab;
import org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local.tab.LuaEnvironmentTab;

public class LuaLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		final LuaMainLaunchConfigurationTab main = new LuaMainLaunchConfigurationTab(mode);
		final LuaArgumentsTab arguments = new LuaArgumentsTab();
		final LuaEnvironmentTab env = new LuaEnvironmentTab();
		final CommonTab common = new CommonTab();
		setTabs(new ILaunchConfigurationTab[] { main, arguments, env, common });
	}
}
