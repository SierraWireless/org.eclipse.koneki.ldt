package org.eclipse.koneki.ldt.remote.debug.ui.internal.launch.tab;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class LuaRemoteLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	public LuaRemoteLaunchConfigurationTabGroup() {
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[3];
		tabs[0] = new LuaRemoteLaunchConfigurationMainTab(mode);
		tabs[1] = new LuaRemoteEnvironmentTab();
		tabs[2] = new CommonTab();
		setTabs(tabs);
	}

}
