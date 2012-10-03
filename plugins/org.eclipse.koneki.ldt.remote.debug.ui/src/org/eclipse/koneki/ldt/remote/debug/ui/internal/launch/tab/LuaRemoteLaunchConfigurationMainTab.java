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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.preferences.ScriptDebugPreferencesMessages;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.koneki.ldt.core.LuaConstants;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.remote.core.internal.RSEUtil;
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
import org.eclipse.swt.events.SelectionListener;
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

	/**
	 * Commons listener for UI (used to update dialog buttons)
	 */
	private final class SelectionChangeListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			updateLaunchConfigurationDialog();
		}
	}

	private SystemHostCombo hostCombo;
	private TextModifyListener textModifyListener = new TextModifyListener();
	private SelectionListener selectionChangeListener = new SelectionChangeListener();
	private Text projectNameText;
	private Button projectSelectionButton;
	private Text scriptNameText;
	private Button scriptSelectionButton;
	private Button breakOnFirstLineButton;
	private Button enableLoggingButton;

	private String mode;

	public LuaRemoteLaunchConfigurationMainTab(String mode) {
		this.mode = mode;
	}

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
		RSEUtil.waitForRSEInitialization();

		createProjectConfigComponent(comp);
		createScriptConfigComponent(comp);
		createTargetConfigComponent(comp);

		if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			createDebugConfigComponent(comp);
		}
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

	private void createScriptConfigComponent(Composite comp) {
		// create group
		Group group = new Group(comp, SWT.None);
		group.setText(Messages.LuaRemoteLaunchConfigurationMainTab_scriptgroup_title);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);

		// create script choice :
		scriptNameText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(scriptNameText);
		scriptSelectionButton = new Button(group, SWT.PUSH);
		scriptSelectionButton.setText(Messages.LuaRemoteMainTab_projectgroup_browseprojectbutton);
		final int browseButtonHorizontalHint = SWTUtil.getButtonWidthHint(projectSelectionButton);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).hint(browseButtonHorizontalHint, SWT.DEFAULT).applyTo(scriptSelectionButton);
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
	}

	/**
	 * @param comp
	 */
	private void createDebugConfigComponent(Composite comp) {
		// create group
		Group group = new Group(comp, SWT.None);
		group.setText(Messages.LuaRemoteLaunchConfigurationMainTab_debuggroup_title);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);

		breakOnFirstLineButton = createCheckButton(group, ScriptDebugPreferencesMessages.BreakOnFirstLineLabel);
		createVerticalSpacer(group, 1);

		enableLoggingButton = createCheckButton(group, ScriptDebugPreferencesMessages.EnableDbgpLoggingLabel);
		createVerticalSpacer(group, 1);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		IProject defaultProject = null;
		try {
			IProject[] avilableProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : avilableProjects) {
				if (project.hasNature(LuaNature.ID) && defaultProject == null) {
					defaultProject = project;
				}
			}
			// CHECKSTYLE:OFF
		} catch (CoreException e) {
			// nothing to do, continue trying to find a default project
		}
		// CHECKSTYLE:ON

		configuration.setAttribute(LuaRemoteDebugConstant.PROJECT_NAME, defaultProject == null ? "" : defaultProject.getName()); //$NON-NLS-1$

		String defaultScript = ""; //$NON-NLS-1$
		if (defaultProject != null) {
			IPath standardPath = new Path(LuaConstants.SOURCE_FOLDER).append(LuaConstants.DEFAULT_MAIN_FILE);
			IFile standardFile = defaultProject.getFile(standardPath);
			if (standardFile.exists()) {
				defaultScript = standardFile.getProjectRelativePath().toPortableString();
			}
		}
		configuration.setAttribute(LuaRemoteDebugConstant.SCRIPT_NAME, defaultScript);

		// get first available target
		IHost[] hosts = RSECorePlugin.getTheSystemRegistry().getHosts();
		if (hosts.length > 0) {
			LuaRemoteLaunchConfigurationUtil.setConnectionId(configuration, hosts[0]);
		}
		configuration.setAttribute(LuaRemoteDebugConstant.DBGP_LOGGING, false);
		configuration.setAttribute(LuaRemoteDebugConstant.BREAK_ON_FIRST_LINE, false);
	}

	/**
	 * add listeners to be aware of tab modification (to update dialog buttons)
	 */
	private void addListeners() {
		if (!projectNameText.isListening(SWT.Modify))
			projectNameText.addModifyListener(textModifyListener);
		if (!scriptNameText.isListening(SWT.Modify))
			scriptNameText.addModifyListener(textModifyListener);
		if (!projectSelectionButton.isListening(SWT.Selection))
			projectSelectionButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					selectLuaEmbeddedProject();
				}
			});
		if (!scriptSelectionButton.isListening(SWT.Selection))
			scriptSelectionButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					selectScript();
				}
			});
		if (!hostCombo.isListening(SWT.Selection))
			hostCombo.addSelectionListener(selectionChangeListener);
		if (breakOnFirstLineButton != null && !breakOnFirstLineButton.isListening(SWT.Selection))
			breakOnFirstLineButton.addSelectionListener(selectionChangeListener);
		if (enableLoggingButton != null && !enableLoggingButton.isListening(SWT.Selection))
			enableLoggingButton.addSelectionListener(selectionChangeListener);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String projectName = configuration.getAttribute(LuaRemoteDebugConstant.PROJECT_NAME, ""); //$NON-NLS-1$
			if (!projectName.equals(projectNameText.getText()))
				projectNameText.setText(projectName);
			String scriptName = configuration.getAttribute(LuaRemoteDebugConstant.SCRIPT_NAME, ""); //$NON-NLS-1$
			if (!scriptName.equals(scriptNameText.getText()))
				scriptNameText.setText(scriptName);
			IHost host = LuaRemoteLaunchConfigurationUtil.getHost(configuration);
			if (hostCombo.getHost() != host)
				hostCombo.select(host);

			if (breakOnFirstLineButton != null) {
				boolean breakOnFirstLine = configuration.getAttribute(LuaRemoteDebugConstant.BREAK_ON_FIRST_LINE, false);
				breakOnFirstLineButton.setSelection(breakOnFirstLine);
			}
			if (enableLoggingButton != null) {
				boolean enableDBGPLogging = configuration.getAttribute(LuaRemoteDebugConstant.DBGP_LOGGING, false);
				enableLoggingButton.setSelection(enableDBGPLogging);
			}

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
		configuration.setAttribute(LuaRemoteDebugConstant.PROJECT_NAME, projectNameText.getText());
		configuration.setAttribute(LuaRemoteDebugConstant.SCRIPT_NAME, scriptNameText.getText());
		IHost host = hostCombo.getHost();
		if (host != null)
			LuaRemoteLaunchConfigurationUtil.setConnectionId(configuration, hostCombo.getHost());
		else
			configuration.removeAttribute(LuaRemoteDebugConstant.HOST_ID);
		if (breakOnFirstLineButton != null) {
			configuration.setAttribute(LuaRemoteDebugConstant.BREAK_ON_FIRST_LINE, breakOnFirstLineButton.getSelection());
		}
		if (enableLoggingButton != null) {
			configuration.setAttribute(LuaRemoteDebugConstant.DBGP_LOGGING, enableLoggingButton.getSelection());
		}
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
		String projectName = projectNameText.getText();
		String scriptName = scriptNameText.getText();
		IHost host = hostCombo.getHost();
		return innerIsValuesValid(projectName, scriptName, host);
	}

	/**
	 * internal method to validate the value of the current tab
	 * 
	 * @param luaInspectorPort
	 * 
	 * @return true if value is valid
	 */
	private boolean innerIsValuesValid(String projectName, String scriptName, IHost host) {
		String error = LuaRemoteLaunchConfigurationUtil.validateRemoteLaunchConfiguration(projectName, scriptName, host);
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

	/**
	 * Open Script selection dialog
	 */
	private void selectScript() {
		String currentProjectName = projectNameText.getText();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(currentProjectName);
		IFile selectedScript = LuaDialogUtil.openSelectScriptFromProjectDialog(getShell(), project);
		if (selectedScript != null && !selectedScript.getName().equals(scriptNameText.getText())) {
			scriptNameText.setText(selectedScript.getProjectRelativePath().toPortableString());
		}
	}
}
