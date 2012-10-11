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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.launching.ScriptRuntime.DefaultInterpreterEntry;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.debug.ui.internal.Activator;
import org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local.tab.Messages;
import org.eclipse.koneki.ldt.ui.SWTUtil;
import org.eclipse.koneki.ldt.ui.internal.ImageConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class LuaMainLaunchConfigurationTab extends MainLaunchConfigurationTab {

	private Button defaultInterpreterButton;
	private Label defaultInterpreterLabel;
	private Button alternateInterpreterButton;
	private ComboViewer interpretersViewer;
	private Button manageButton;

	public LuaMainLaunchConfigurationTab(String mode) {
		super(mode);
	}

	@Override
	public String getNatureID() {
		return LuaNature.ID;
	}

	@Override
	public Image getImage() {
		return org.eclipse.koneki.ldt.ui.internal.Activator.getDefault().getImageRegistry().get(ImageConstants.MODULE_OBJ16);
	}

	@Override
	protected void doCreateControl(Composite composite) {
		// create default Main tab control
		super.doCreateControl(composite);

		// create interpreter selection composite
		createInterpreterSelectionComponent(composite);
	}

	private void createInterpreterSelectionComponent(Composite parent) {
		int nbColumn = 3;

		// Group container
		final Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.LuaInterpreterTabComboBlockRuntimeInterpreterLabel);
		GridLayoutFactory.swtDefaults().numColumns(nbColumn).applyTo(group);
		GridDataFactory.swtDefaults().span(nbColumn, 1).grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(group);

		// Default interpreter
		defaultInterpreterButton = new Button(group, SWT.RADIO);
		defaultInterpreterButton.setText(Messages.LuaInterpreterTabComboBlockDefaultInterpreterLabel);
		defaultInterpreterLabel = new Label(group, SWT.RADIO);
		GridDataFactory.swtDefaults().span(nbColumn - 1, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(defaultInterpreterLabel);

		// Alternate interpreters
		alternateInterpreterButton = new Button(group, SWT.RADIO);
		alternateInterpreterButton.setText(Messages.LuaInterpreterTabComboBlockAlternateInterpreterLabel);

		interpretersViewer = new ComboViewer(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		interpretersViewer.setContentProvider(new ArrayContentProvider());
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(interpretersViewer.getControl());

		// Manage interpreters
		manageButton = new Button(group, SWT.None);
		manageButton.setText(Messages.LuaInterpreterTabComboBlockManageInterpretersButton);
		GridDataFactory.swtDefaults().hint(SWTUtil.getButtonWidthHint(manageButton), -1).applyTo(manageButton);
	}

	/**
	 * @see org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab#doInitializeForm(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	protected void doInitializeForm(ILaunchConfiguration config) {
		// initialize main control
		super.doInitializeForm(config);

		try {
			// initialize interpreter control
			// fill UI
			refreshInterpretersInformation();

			String path = config.getAttribute(ScriptLaunchConfigurationConstants.ATTR_CONTAINER_PATH, (String) null);
			if (path == null) {
				// default interpreter
				defaultInterpreterButton.setSelection(true);
				alternateInterpreterButton.setSelection(false);
				if (interpretersViewer.getCombo().getItemCount() > 0)
					interpretersViewer.getCombo().select(0);

			} else {
				// alternate interpreter
				alternateInterpreterButton.setSelection(true);
				defaultInterpreterButton.setSelection(false);
				final String id = EnvironmentManager.getLocalEnvironment().getId();
				final IInterpreterInstall install = ScriptRuntime.getInterpreterInstall(getNatureID(), id, new Path(path));
				if (install != null)
					interpretersViewer.setSelection(new StructuredSelection(install));
			}
			refreshUISelection();

			addListeners();
		} catch (CoreException e) {
			Activator.logError("Launch Configuration main tab for lua application failed at initialization", e); //$NON-NLS-1$
		}

	}

	private void addListeners() {
		defaultInterpreterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshUISelection();
				updateLaunchConfigurationDialog();
			}
		});
		alternateInterpreterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshUISelection();
				updateLaunchConfigurationDialog();
			}
		});

		interpretersViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateLaunchConfigurationDialog();
			}
		});

		manageButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showInterpreterPreferencePage();
				refreshInterpretersInformation();
				if (interpretersViewer.getCombo().getItemCount() > 0)
					interpretersViewer.getCombo().select(0);
				updateLaunchConfigurationDialog();
			}
		});
	}

	private void refreshInterpretersInformation() {
		// refresh default interpreter
		String interpreterName = null;
		IInterpreterInstall defaultInterpreter = getDefaultInterpreter();
		if (defaultInterpreter != null) {
			interpreterName = defaultInterpreter.getName();
		}

		if (interpreterName != null)
			defaultInterpreterLabel.setText(interpreterName);
		else
			defaultInterpreterLabel.setText(Messages.LuaInterpreterTabUndefinedInterpreterName);

		// refresh interpreter list
		interpretersViewer.setInput(getInstalledInterpreters());
	}

	private void refreshUISelection() {
		interpretersViewer.getControl().setEnabled(alternateInterpreterButton.getSelection());
		defaultInterpreterLabel.setEnabled(!alternateInterpreterButton.getSelection());
	}

	/**
	 * Shows window with appropriate language preference page.
	 */
	protected void showInterpreterPreferencePage() {
		IDLTKUILanguageToolkit toolkit = DLTKUILanguageManager.getLanguageToolkit(getNatureID());
		if (toolkit != null) {
			final String pageId = toolkit.getInterpreterPreferencePage();
			if (pageId != null) {
				PreferencesUtil.createPreferenceDialogOn(getShell(), pageId, new String[] { pageId }, null).open();
			}
		}
	}

	/**
	 * @see org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab#doPerformApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		super.doPerformApply(config);

		// save interpreters information
		if (defaultInterpreterButton.getSelection()) {
			config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_CONTAINER_PATH, (String) null);
		} else {
			IInterpreterInstall selectedInterpreter = getSelectedInterpreter();
			if (selectedInterpreter != null) {
				IPath containerPath = ScriptRuntime.newInterpreterContainerPath(selectedInterpreter);
				if (containerPath != null)
					config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_CONTAINER_PATH, containerPath.toPortableString());
			}
		}
	}

	private IInterpreterInstall getSelectedInterpreter() {
		ISelection selection = interpretersViewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IInterpreterInstall) {
				return (IInterpreterInstall) firstElement;
			}
		}
		return null;
	}

	private IInterpreterInstall getDefaultInterpreter() {
		// get environment
		final String id = EnvironmentManager.getLocalEnvironment().getId();

		// refresh default interpreter
		return ScriptRuntime.getDefaultInterpreterInstall(new DefaultInterpreterEntry(getNatureID(), id));
	}

	private List<IInterpreterInstall> getInstalledInterpreters() {
		final String id = EnvironmentManager.getLocalEnvironment().getId();

		final IInterpreterInstallType[] types = ScriptRuntime.getInterpreterInstallTypes(getNatureID());
		final ArrayList<IInterpreterInstall> interpreters = new ArrayList<IInterpreterInstall>();

		for (int i = 0; i < types.length; i++) {
			IInterpreterInstallType type = types[i];
			IInterpreterInstall[] installs = type.getInterpreterInstalls();
			for (int j = 0; j < installs.length; j++) {
				final IInterpreterInstall install = installs[j];
				if (id.equals(install.getEnvironmentId())) {
					interpreters.add(install);
				}
			}
		}
		return interpreters;
	}

	/**
	 * @see org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public boolean isValid(final ILaunchConfiguration launchConfig) {
		final boolean valid = super.isValid(launchConfig);

		if (valid) {
			if (defaultInterpreterButton.getSelection()) {
				final IInterpreterInstall defaultInterpreter = getDefaultInterpreter();
				if (defaultInterpreter == null) {
					setErrorMessage(Messages.LuaInterpreterTabComboBlockNoDefaultInterpreter);
					return false;
				}
			} else {
				if (getSelectedInterpreter() == null) {
					if (interpretersViewer.getCombo().getItemCount() > 0) {
						setErrorMessage(Messages.LuaInterpreterTabComboBlockSelectAnInterpreter);
						return false;
					} else {
						setErrorMessage(Messages.LuaInterpreterTabComboBlockNoInterpreter);
						return false;
					}
				}
			}
		}
		return valid;
	}
}
