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
package org.eclipse.koneki.ldt.ui.internal.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.koneki.ldt.core.internal.LuaLanguageToolkit;
import org.eclipse.koneki.ldt.core.internal.PreferenceInitializer;
import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironment;
import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironmentConstants;
import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironmentManager;
import org.eclipse.koneki.ldt.ui.LuaExecutionEnvironmentUIManager;
import org.eclipse.koneki.ldt.ui.SWTUtil;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.buildpath.LuaExecutionEnvironmentContentProvider;
import org.eclipse.koneki.ldt.ui.internal.buildpath.LuaExecutionEnvironmentLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.ActivityEvent;
import org.eclipse.ui.activities.IActivity;
import org.eclipse.ui.activities.IActivityListener;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class LuaExecutionEnvironmentPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String AVAILABLE_EXECUTION_ENVIRONEMENT_URL = "http://wiki.eclipse.org/Koneki/LDT/User_Area/Available_Execution_Environments"; //$NON-NLS-1$

	private CheckboxTreeViewer eeTreeViewer;
	private Button removeButton;

	private Set<IActivity> activitiesWatched = new HashSet<IActivity>();
	private IActivityListener activityListener = new IActivityListener() {
		@Override
		public void activityChanged(ActivityEvent activityEvent) {
			if (activityEvent.hasEnabledChanged()) {
				initializePage();
			}
		}
	};

	public LuaExecutionEnvironmentPreferencePage() {
		setDescription(Messages.LuaExecutionEnvironmentPreferencePageTitle);
		noDefaultAndApplyButton();
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, LuaLanguageToolkit.getDefault().getPreferenceQualifier()));
	}

	@Override
	protected Control createContents(Composite parent) {
		// ----------------
		// CREATE CONTROL
		// create container composite
		Composite containerComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(containerComposite);

		eeTreeViewer = new CheckboxTreeViewer(containerComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		eeTreeViewer.setContentProvider(new LuaExecutionEnvironmentContentProvider());
		eeTreeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new LuaExecutionEnvironmentLabelProvider()));
		eeTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				refreshRemoveButton();
			}
		});
		GridDataFactory.fillDefaults().grab(true, true).applyTo(eeTreeViewer.getControl());

		// add a listener to allow only one default EE
		eeTreeViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				LuaExecutionEnvironment defaultEE = (LuaExecutionEnvironment) event.getElement();
				if (event.getChecked()) {

					// allow to check only one element of the table
					eeTreeViewer.setCheckedElements(new Object[] { defaultEE });
					getPreferenceStore().setValue(PreferenceInitializer.EE_DEFAULT_ID, defaultEE.getEEIdentifier());

					// remove warning no default EE message if any
					setMessage(null);
				} else {

					// removing the default ee from pref
					getPreferenceStore().setValue(PreferenceInitializer.EE_DEFAULT_ID, "none"); //$NON-NLS-1$
					setMessage(Messages.LuaExecutionEnvironmentPreferencePage_warning_nodefault, WARNING);
				}
			}
		});

		// create buttons
		Composite buttonsComposite = new Composite(containerComposite, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(buttonsComposite);
		RowLayoutFactory.fillDefaults().type(SWT.VERTICAL).fill(true).applyTo(buttonsComposite);

		// Add
		Button addButton = new Button(buttonsComposite, SWT.None);
		RowDataFactory.swtDefaults().hint(SWTUtil.getButtonWidthHint(addButton), -1).applyTo(addButton);
		addButton.setText(Messages.LuaExecutionEnvironmentPreferencePage_addbutton);

		// Remove
		removeButton = new Button(buttonsComposite, SWT.None);
		RowDataFactory.swtDefaults().hint(SWTUtil.getButtonWidthHint(removeButton), -1).applyTo(removeButton);
		removeButton.setText(Messages.LuaExecutionEnvironmentPreferencePage_removeButton);

		// Link to available EEs
		Link availableEELink = new Link(containerComposite, SWT.NONE);
		availableEELink.setText(Messages.LuaExecutionEnvironmentPreferencePage_availableEELink);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(availableEELink);

		// ----------------
		// ADD LISTENERS
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				doAddButtonSelection(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				doRemoveSelection(e);
			}
		});
		availableEELink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(AVAILABLE_EXECUTION_ENVIRONEMENT_URL));
				} catch (PartInitException e1) {
					Activator.logError("Unable to open: " + AVAILABLE_EXECUTION_ENVIRONEMENT_URL, e1); //$NON-NLS-1$
				} catch (MalformedURLException e1) {
					Activator.logError("Unable to open: " + AVAILABLE_EXECUTION_ENVIRONEMENT_URL, e1); //$NON-NLS-1$
				}
			}
		});

		// add a listener to activities which can hide EE to refresh the UI
		activitiesWatched = LuaExecutionEnvironmentUIManager.addListenerToEERelatedActivity(activityListener);

		// ----------------
		// Initialize UI
		initializePage();
		return containerComposite;
	}

	private void doAddButtonSelection(SelectionEvent se) {
		/*
		 * Ask user for a file
		 */
		FileDialog filedialog = new FileDialog(Display.getDefault().getActiveShell());
		filedialog.setFilterExtensions(new String[] { LuaExecutionEnvironmentConstants.FILE_EXTENSION });
		final String selectedFilePath = filedialog.open();
		if (selectedFilePath == null) {
			return;
		}

		/*
		 * Deploy
		 */
		try {
			LuaExecutionEnvironment ee = LuaExecutionEnvironmentManager.getExecutionEnvironmentFromCompressedFile(selectedFilePath);
			List<LuaExecutionEnvironment> embeddedExecutionEnvironments = LuaExecutionEnvironmentManager.getEmbeddedExecutionEnvironments();
			if (embeddedExecutionEnvironments.contains(ee)) {
				boolean okToInstall = MessageDialog.openQuestion(getShell(), Messages.LuaExecutionEnvironmentPreferencePage_addEESupportTitle,
						NLS.bind(Messages.LuaExecutionEnvironmentPreferencePage_addEESupportMessage, ee.getEEIdentifier()));
				if (!okToInstall)
					return;
			}
			LuaExecutionEnvironmentManager.installLuaExecutionEnvironment(selectedFilePath);

			// Refresh the treeviewer
			initializePage();
		} catch (CoreException e) {
			ErrorDialog.openError(filedialog.getParent(), null, null, e.getStatus());
			Activator.log(e.getStatus());
		}
	}

	private void doRemoveSelection(final SelectionEvent event) {
		// Extract selected Execution Environment
		LuaExecutionEnvironment ee = getSelectedExecutionEnvironment();

		// Nothing to delete
		if (ee == null)
			return;

		try {
			// Remove selected Execution Environment
			LuaExecutionEnvironmentManager.uninstallLuaExecutionEnvironment(ee);

			// remove default EE if juste removed
			if (getPreferenceStore().getString(PreferenceInitializer.EE_DEFAULT_ID).equals(ee.getEEIdentifier())) {
				getPreferenceStore().setValue(PreferenceInitializer.EE_DEFAULT_ID, "none"); //$NON-NLS-1$

				setMessage(Messages.LuaExecutionEnvironmentPreferencePage_warning_nodefault, WARNING);
			}

			// Recompute page content
			initializePage();
		} catch (final CoreException e) {
			ErrorDialog.openError(getShell(), null, null, e.getStatus());
			Activator.log(e.getStatus());
		}
	}

	private LuaExecutionEnvironment getSelectedExecutionEnvironment() {
		if (eeTreeViewer == null)
			return null;

		final ISelection selection = eeTreeViewer.getSelection();
		if (selection.isEmpty())
			return null;

		if (selection instanceof StructuredSelection) {
			final StructuredSelection sSelection = (StructuredSelection) selection;
			final Object currentSelection = sSelection.getFirstElement();
			if (currentSelection instanceof LuaExecutionEnvironment)
				return (LuaExecutionEnvironment) currentSelection;
		}
		return null;
	}

	private void refreshRemoveButton() {
		if (removeButton != null) {
			// enable remove button only for non embedded Execution Environment
			LuaExecutionEnvironment ee = getSelectedExecutionEnvironment();
			removeButton.setEnabled(ee != null && !ee.isEmbedded());
		}
	}

	private void initializePage() {
		if (eeTreeViewer == null || eeTreeViewer.getControl().isDisposed())
			return;

		// Refresh list
		List<LuaExecutionEnvironment> availableExecutionEnvironments = LuaExecutionEnvironmentUIManager.getAvailableExecutionEnvironments();
		eeTreeViewer.setInput(availableExecutionEnvironments);

		// Set default interpreter
		String defaultEEId = getPreferenceStore().getString(PreferenceInitializer.EE_DEFAULT_ID);
		for (LuaExecutionEnvironment execEnv : availableExecutionEnvironments) {
			eeTreeViewer.setChecked(execEnv, execEnv.getEEIdentifier().equals(defaultEEId));
		}

		// As list is refreshed, they is no selection
		refreshRemoveButton();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		// dispose the listener watching something not build in this page
		for (IActivity activity : activitiesWatched) {
			activity.removeActivityListener(activityListener);
		}

		super.dispose();

	}
}
