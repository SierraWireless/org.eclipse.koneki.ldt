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
package org.eclipse.koneki.ldt.remote.debug.ui.internal.launch.tab;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.launchConfigurations.ScriptArgumentsTab;
import org.eclipse.dltk.debug.ui.messages.ScriptLaunchMessages;
import org.eclipse.dltk.internal.debug.ui.launcher.WorkingDirectoryBlock;
import org.eclipse.koneki.ldt.remote.debug.core.internal.launch.LuaRemoteLaunchConfigurationUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class LuaRemoteArgumentsTab extends ScriptArgumentsTab implements ILaunchConfigurationTab {
	@Override
	protected WorkingDirectoryBlock createWorkingDirBlock() {
		return new WorkingDirectoryBlock() {

			@Override
			protected void setDefaultWorkingDir() {
				ILaunchConfiguration configuration = getCurrentLaunchConfiguration();
				setDefaultWorkingDirectoryText(LuaRemoteLaunchConfigurationUtil.getRemoteApplicationPath(configuration));
			}

			@Override
			protected Button createPushButton(Composite parent, String label, Image image) {
				Button button = super.createPushButton(parent, label, image);
				if (label.equals(ScriptLaunchMessages.WorkingDirectoryBlock_0) || label.equals(ScriptLaunchMessages.WorkingDirectoryBlock_1)
						|| label.equals(ScriptLaunchMessages.WorkingDirectoryBlock_17)) {
					button.setVisible(false);
				}
				return button;
			}

			@Override
			public boolean isValid(ILaunchConfiguration config) {
				return true;
			}
		};
	}
}
