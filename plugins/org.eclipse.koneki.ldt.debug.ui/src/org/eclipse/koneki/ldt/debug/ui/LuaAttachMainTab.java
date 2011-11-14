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
package org.eclipse.koneki.ldt.debug.ui;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.messages.DLTKLaunchConfigurationsMessages;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.koneki.ldt.core.LuaLanguageToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class LuaAttachMainTab extends ScriptLaunchConfigurationTab {

	private static String DEFAULT_IDEKEY = "luaidekey"; //$NON-NLS-1$

	protected Text ideKey;
	protected Text timeoutText;
	protected Text remoteWorkingDir;

	public LuaAttachMainTab(String mode) {
		super(mode);
	}

	/*
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return DLTKLaunchConfigurationsMessages.remoteTab_title;
	}

	/*
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage() {
		return DebugUITools.getImage(IDebugUIConstants.IMG_LCL_DISCONNECT);
	}

	/*
	 * @see org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab #doInitializeForm(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	protected void doInitializeForm(ILaunchConfiguration config) {
		ideKey.setText(LaunchConfigurationUtils.getString(config, ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_SESSION_ID, getDefaultIDEKey()));

		timeoutText.setText(Integer.toString(LaunchConfigurationUtils.getConnectionTimeout(config, getDefaultRemoteTimeout()) / 1000));

		remoteWorkingDir.setText(LaunchConfigurationUtils.getString(config, ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_REMOTE_WORKING_DIR,
				getDefaultRemoteWorkingDir()));
	}

	/**
	 * Override this method to configure other default ide key.
	 */
	protected String getDefaultIDEKey() {
		return DEFAULT_IDEKEY;
	}

	/**
	 * Override this method to configure other default remote working dir.
	 */
	protected String getDefaultRemoteWorkingDir() {
		return "";//$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab
	 * #doPerformApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_SESSION_ID, ideKey.getText().trim());
		int timeout;
		try {
			timeout = Integer.parseInt(timeoutText.getText().trim());
		} catch (NumberFormatException e) {
			timeout = getDefaultRemoteTimeout() / 1000;
		}
		config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_WAITING_TIMEOUT, timeout * 1000);

		config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_REMOTE_WORKING_DIR, remoteWorkingDir.getText().trim());
	}

	private int getDefaultRemoteTimeout() {
		return DLTKDebugPlugin.getConnectionTimeout() * 3;
	}

	protected boolean validate() {
		return super.validate() && validateIdeKey() && validateRemoteWorkingDir();
	}

	protected boolean validateIdeKey() {
		String key = ideKey.getText().trim();
		if (key.length() == 0) {
			setErrorMessage(DLTKLaunchConfigurationsMessages.remoteError_ideKeyEmpty);
			return false;
		}

		return true;
	}

	protected boolean validateRemoteWorkingDir() {
		return true;
	}

	/*
	 * @see org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab #doCreateControl(org.eclipse.swt.widgets.Composite)
	 */
	protected void doCreateControl(Composite composite) {
		Group group = SWTFactory.createGroup(composite, DLTKLaunchConfigurationsMessages.remoteTab_connectionProperties, 2, 1,
				GridData.FILL_HORIZONTAL);

		SWTFactory.createLabel(group, DLTKLaunchConfigurationsMessages.remoteTab_connectionIdeKey, 1);
		ideKey = SWTFactory.createText(group, SWT.BORDER, 1, EMPTY_STRING);
		ideKey.addModifyListener(getWidgetListener());

		SWTFactory.createLabel(group, DLTKLaunchConfigurationsMessages.remoteTab_timeout, 1);
		timeoutText = SWTFactory.createText(group, SWT.BORDER, 1, EMPTY_STRING);
		timeoutText.addModifyListener(getWidgetListener());

		SWTFactory.createHorizontalSpacer(composite, 1);

		group = SWTFactory.createGroup(composite, DLTKLaunchConfigurationsMessages.remoteTab_remoteSourceMapping, 1, 1, GridData.FILL_HORIZONTAL);

		SWTFactory.createLabel(group, DLTKLaunchConfigurationsMessages.remoteTab_remoteWorkingDir, 1);

		remoteWorkingDir = SWTFactory.createText(group, SWT.BORDER, 1, EMPTY_STRING);
		remoteWorkingDir.addModifyListener(getWidgetListener());
	}

	@Override
	protected String getNatureID() {
		return LuaLanguageToolkit.getDefault().getNatureId();
	}
}
