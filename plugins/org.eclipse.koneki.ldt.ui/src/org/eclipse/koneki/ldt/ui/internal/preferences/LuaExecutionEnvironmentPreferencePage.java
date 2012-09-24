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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironment;
import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironmentConstants;
import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironmentManager;
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

public class LuaExecutionEnvironmentPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String AVAILABLE_EXECUTION_ENVIRONEMENT_URL = "http://wiki.eclipse.org/Koneki/LDT/User_Area/Available_Execution_Environments"; //$NON-NLS-1$
	
	private TreeViewer eeTreeViewer;
	private Button removeButton;

	public LuaExecutionEnvironmentPreferencePage() {
		setDescription(Messages.LuaExecutionEnvironmentPreferencePageTitle);
		noDefaultAndApplyButton();
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected Control createContents(Composite parent) {
		// ----------------
		// CREATE CONTROL
		// create container composite
		Composite containerComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(containerComposite);

		eeTreeViewer = new TreeViewer(containerComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		eeTreeViewer.setContentProvider(new LuaExecutionEnvironmentContentProvider());
		eeTreeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new LuaExecutionEnvironmentLabelProvider()));
		eeTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				refreshRemoveButton();
			}
		});
		GridDataFactory.fillDefaults().grab(true, true).applyTo(eeTreeViewer.getControl());

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
		if (eeTreeViewer == null)
			return;

		// Refresh list
		eeTreeViewer.setInput(LuaExecutionEnvironmentManager.getAvailableExecutionEnvironments());

		// As list is refreshed, they is no selection
		refreshRemoveButton();
	}
}
