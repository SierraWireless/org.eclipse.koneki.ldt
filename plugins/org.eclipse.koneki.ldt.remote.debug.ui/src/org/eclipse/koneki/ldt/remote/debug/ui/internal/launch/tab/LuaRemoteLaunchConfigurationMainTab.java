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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.koneki.ldt.remote.debug.core.internal.LuaRemoteDebugConstant;
import org.eclipse.koneki.ldt.remote.debug.core.internal.launch.LuaRemoteLaunchConfigurationUtil;
import org.eclipse.koneki.ldt.remote.debug.ui.internal.Activator;
import org.eclipse.koneki.ldt.remote.debug.ui.internal.ImageConstants;
import org.eclipse.koneki.ldt.ui.LuaDialogUtil;
import org.eclipse.koneki.ldt.ui.SWTUtil;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.ui.widgets.SystemHostCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LuaRemoteLaunchConfigurationMainTab extends AbstractLaunchConfigurationTab {

	/**
	 * Commons listener for Textfield (used to update dialog buttons)
	 */
	private final class TextModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}
	}

	// private Text luacpathText;
	// private Text luapathText;
	private SystemHostCombo hostCombo;
	private TextModifyListener textModifyListener = new TextModifyListener();
	private Text projectNameText;
	private Button projectSelectionButton;

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayoutFactory.swtDefaults().applyTo(comp);
		comp.setFont(parent.getFont());

		// TODO not sure this is the good way to wait for init everywhere in the code
		try {
			RSECorePlugin.waitForInitCompletion();
			// CHECKSTYLE:OFF
		} catch (InterruptedException e) {
			// nothing to do ..
			// CHECKSTYLE:ON
		}

		createProjectConfigComponent(comp);
		createTargetConfigComponent(comp);
	}

	/**
	 * create the composite used to configure the target information
	 * 
	 * @param comp
	 */
	private void createProjectConfigComponent(Composite comp) {
		// create group
		Group group = new Group(comp, SWT.None);
		group.setText(Messages.LuaRemoteMainTab_projectgroup_title);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);

		// create project choice :
		projectNameText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(projectNameText);
		projectSelectionButton = new Button(group, SWT.PUSH);
		projectSelectionButton.setText(Messages.LuaRemoteMainTab_projectgroup_browseprojectbutton);
		final int browseButtonHorizontalHint = SWTUtil.getButtonWidthHint(projectSelectionButton);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).hint(browseButtonHorizontalHint, SWT.DEFAULT).applyTo(projectSelectionButton);
	}

	/**
	 * create the composite used to configure the target information
	 */
	private void createTargetConfigComponent(Composite comp) {
		// create group
		Group group = new Group(comp, SWT.None);
		group.setText(Messages.LuaRemoteMainTab_targetgroup_title);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);

		// create target combo
		Label hostLabel = new Label(group, SWT.None);
		hostLabel.setText(Messages.LuaRemoteMainTab_targetgroup_hostlabel);
		IHost selectfirstHost = null;
		hostCombo = new SystemHostCombo(group, SWT.None, selectfirstHost, true, "files", false); //$NON-NLS-1$
		final int newSystemHint = SWTUtil.getButtonWidthHint(hostCombo.getNewButton());
		hostCombo.setButtonWidthHint(newSystemHint);

		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(hostCombo);

		// create interpreter command :
		// Label luacommandLabel = new Label(group, SWT.None);
		// luacommandLabel.setText(Messages.LuaEmbeddedMainTab_targetgroup_luacommandlabel);
		// luaCommandPathText = new Text(group, SWT.SINGLE | SWT.BORDER);
		// GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(luaCommandPathText);

		// // create luapath
		// Label luapathLabel = new Label(group, SWT.None);
		// luapathLabel.setText(Messages.LuaEmbeddedMainTab_targetgroup_luapathlabel);
		// luapathText = new Text(group, SWT.SINGLE | SWT.BORDER);
		// GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(luapathText);
		//
		// // create luacpath
		// Label luacpathLabel = new Label(group, SWT.None);
		// luacpathLabel.setText(Messages.LuaEmbeddedMainTab_targetgroup_luacpathlabel);
		// luacpathText = new Text(group, SWT.SINGLE | SWT.BORDER);
		// GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(luacpathText);

		// create remote path
		// Label ramoteApplicationPathLabel = new Label(group, SWT.None);
		// ramoteApplicationPathLabel.setText(Messages.LuaEmbeddedMainTab_targetgroup_remoteapppathlabel);
		// remoteApplicationPathText = new Text(group, SWT.SINGLE | SWT.BORDER);
		// GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(remoteApplicationPathText);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO : get project of selected resource
		configuration.setAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$

		// get first available target
		// IHost[] hosts = RSECorePlugin.getTheSystemRegistry().getHosts();
		// if (hosts.length > 0) {
		// LuaRemoteLaunchConfigurationUtil.setConnectionId(configuration, hosts[0]);
		// }

		// TODO integrate UI, see org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab
		configuration.setAttribute(ScriptLaunchConfigurationConstants.ENABLE_DBGP_LOGGING, false);
	}

	/**
	 * add listeners to be aware of tab modification (to update dialog buttons)
	 */
	private void addListeners() {
		if (!projectNameText.isListening(SWT.Modify))
			projectNameText.addModifyListener(textModifyListener);
		if (!projectSelectionButton.isListening(SWT.Selection))
			projectSelectionButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					selectLuaEmbeddedProject();
				}
			});
		// if (!luapathText.isListening(SWT.Modify))
		// luapathText.addModifyListener(textModifyListener);
		// if (!luacpathText.isListening(SWT.Modify))
		// luacpathText.addModifyListener(textModifyListener);
		if (!hostCombo.isListening(SWT.Selection))
			hostCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateLaunchConfigurationDialog();
				}
			});
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String projectName = configuration.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
			if (!projectName.equals(projectNameText.getText()))
				projectNameText.setText(projectName);
			IHost host = LuaRemoteLaunchConfigurationUtil.getHost(configuration);
			if (hostCombo.getHost() != host)
				hostCombo.select(host);
			addListeners();
		} catch (CoreException e) {
			Activator.logError("Launch Configuration main tab for lua embedded failed at initialization", e); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// Save attributes
		configuration.setAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectNameText.getText());
		IHost host = hostCombo.getHost();
		if (host != null)
			LuaRemoteLaunchConfigurationUtil.setConnectionId(configuration, hostCombo.getHost());
		else
			configuration.removeAttribute(LuaRemoteDebugConstant.HOST_ID);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return Messages.LuaRemoteMainTab_tabname;
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
	 */
	@Override
	public Image getImage() {
		return Activator.getDefault().getImageRegistry().get(ImageConstants.LUA_REMOTE_APP_LAUNCH_MAIN_TAB_ICON);
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public boolean isValid(ILaunchConfiguration configuration) {
		// try {
		//			String projectName = configuration.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");//$NON-NLS-1$
		// IHost host = LuaRemoteLaunchConfigurationUtil.getHost(configuration);
		// return innerIsValuesValid(projectName, host, luacommandpath, remoteapplicationpath);
		// return false;
		// } catch (CoreException e) {
		//			Activator.logError("Launch Configuration main tab for lua embedded failed at validation", e); //$NON-NLS-1$
		// return false;
		// }
		return true;
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#canSave()
	 */
	@Override
	public boolean canSave() {
		String projectName = projectNameText.getText();
		IHost host = hostCombo.getHost();
		return innerIsValuesValid(projectName, host);
	}

	/**
	 * internal method to validate the value of the current tab
	 * 
	 * @param luaInspectorPort
	 * 
	 * @return true if value is valid
	 */
	private boolean innerIsValuesValid(String projectName, IHost host) {
		String error = LuaRemoteLaunchConfigurationUtil.validateRemoteLaunchConfiguration(projectName, host);
		setErrorMessage(error);
		return error == null;
	}

	/**
	 * Open project selection dialog
	 */
	private void selectLuaEmbeddedProject() {
		String currentProjectName = projectNameText.getText();
		IProject selectedProject = LuaDialogUtil.openSelectLuaProjectDialog(getShell(), projectNameText.getText());
		if (selectedProject != null && !selectedProject.getName().equals(currentProjectName))
			projectNameText.setText(selectedProject.getName());
	}

}
