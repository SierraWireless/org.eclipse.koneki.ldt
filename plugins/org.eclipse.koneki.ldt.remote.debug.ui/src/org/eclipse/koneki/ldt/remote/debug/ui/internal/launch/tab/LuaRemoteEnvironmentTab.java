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
package org.eclipse.koneki.ldt.remote.debug.ui.internal.launch.tab;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.MultipleInputDialog;
import org.eclipse.debug.internal.ui.launchConfigurations.EnvironmentVariable;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsMessages;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * A more Simple EnvironmentTab then the official one
 * 
 * @see org.eclipse.debug.ui.EnvironmentTab
 */
@SuppressWarnings("restriction")
public class LuaRemoteEnvironmentTab extends AbstractLaunchConfigurationTab {

	private static final String NAME_LABEL = LaunchConfigurationsMessages.EnvironmentTab_8;
	private static final String VALUE_LABEL = LaunchConfigurationsMessages.EnvironmentTab_9;

	private String[] envTableColumnHeaders = { LaunchConfigurationsMessages.EnvironmentTab_Variable_1,
			LaunchConfigurationsMessages.EnvironmentTab_Value_2, };

	private TableViewer environmentTable;
	private Button envAddButton;
	private Button envEditButton;
	private Button envRemoveButton;

	protected static class EnvironmentVariableContentProvider implements IStructuredContentProvider {
		@SuppressWarnings("rawtypes")
		public Object[] getElements(Object inputElement) {
			List<EnvironmentVariable> elements = new ArrayList<EnvironmentVariable>();
			ILaunchConfiguration config = (ILaunchConfiguration) inputElement;
			Map m;
			try {
				m = config.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, Collections.EMPTY_MAP);
			} catch (CoreException e) {
				DebugUIPlugin.log(new Status(IStatus.ERROR, DebugUIPlugin.getUniqueIdentifier(), IStatus.ERROR, "Error reading configuration", e)); //$NON-NLS-1$
				return elements.toArray();
			}
			if (m != null && !m.isEmpty()) {
				for (Object oEntry : m.entrySet()) {
					Entry entry = (Entry) oEntry;
					elements.add(new EnvironmentVariable((String) entry.getKey(), (String) entry.getValue()));
				}
			}
			return elements.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput == null) {
				return;
			}
			if (viewer instanceof TableViewer) {
				TableViewer tableViewer = (TableViewer) viewer;
				if (tableViewer.getTable().isDisposed()) {
					return;
				}
				tableViewer.setComparator(new ViewerComparator() {
					public int compare(Viewer iviewer, Object e1, Object e2) {
						if (e1 == null) {
							return -1;
						} else if (e2 == null) {
							return 1;
						} else {
							return ((EnvironmentVariable) e1).getName().compareToIgnoreCase(((EnvironmentVariable) e2).getName());
						}
					}
				});
			}
		}
	}

	/**
	 * Label provider for the environment table
	 */
	public static class EnvironmentVariableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			String result = null;
			if (element != null) {
				EnvironmentVariable var = (EnvironmentVariable) element;
				switch (columnIndex) {
				case 0: // variable
					result = var.getName();
					break;
				case 1: // value
					result = var.getValue();
					break;
				default:
					result = ""; //$NON-NLS-1$
					break;
				}
			}
			return result;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return DebugPluginImages.getImage(IDebugUIConstants.IMG_OBJS_ENV_VAR);
			}
			return null;
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);
		comp.setFont(parent.getFont());

		createEnvironmentTable(comp);
		createTableButtons(comp);
	}

	/**
	 * @param mainComposite
	 */
	private void createTableButtons(Composite parent) {
		// Create button composite
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().applyTo(buttonComposite);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(buttonComposite);

		// Create buttons
		envAddButton = createPushButton(buttonComposite, LaunchConfigurationsMessages.EnvironmentTab_New_4, null);
		envAddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleEnvAddButtonSelected();
			}
		});
		envEditButton = createPushButton(buttonComposite, LaunchConfigurationsMessages.EnvironmentTab_Edit_5, null);
		envEditButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleEnvEditButtonSelected();
			}
		});
		envEditButton.setEnabled(false);
		envRemoveButton = createPushButton(buttonComposite, LaunchConfigurationsMessages.EnvironmentTab_Remove_6, null);
		envRemoveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleEnvRemoveButtonSelected();
			}
		});
		envRemoveButton.setEnabled(false);
	}

	/**
	 * @param mainComposite
	 */
	private void createEnvironmentTable(Composite parent) {
		// Create label, add it to the parent to align the right side buttons with the top of the table
		Label l = new Label(parent, SWT.NONE);
		l.setText(LaunchConfigurationsMessages.EnvironmentTab_Environment_variables_to_set__3);
		GridDataFactory.swtDefaults().span(2, 1).applyTo(l);

		// Create table composite
		Composite tableComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(tableComposite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);

		// Create table
		environmentTable = new TableViewer(tableComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		Table table = environmentTable.getTable();
		GridLayoutFactory.swtDefaults().applyTo(table);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		environmentTable.setContentProvider(new EnvironmentVariableContentProvider());
		environmentTable.setLabelProvider(new EnvironmentVariableLabelProvider());
		environmentTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleTableSelectionChanged(event);
			}
		});
		environmentTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (!environmentTable.getSelection().isEmpty()) {
					handleEnvEditButtonSelected();
				}
			}
		});

		// Create columns
		final TableColumn tc1 = new TableColumn(table, SWT.NONE, 0);
		tc1.setText(envTableColumnHeaders[0]);
		final TableColumn tc2 = new TableColumn(table, SWT.NONE, 1);
		tc2.setText(envTableColumnHeaders[1]);
		final Table tref = table;
		final Composite comp = tableComposite;
		tableComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = comp.getClientArea();
				Point size = tref.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				ScrollBar vBar = tref.getVerticalBar();
				int width = area.width - tref.computeTrim(0, 0, 0, 0).width - 2;
				if (size.y > area.height + tref.getHeaderHeight()) {
					Point vBarSize = vBar.getSize();
					width -= vBarSize.x;
				}
				Point oldSize = tref.getSize();
				if (oldSize.x > area.width) {
					tc1.setWidth(width / 2 - 1);
					tc2.setWidth(width - tc1.getWidth());
					tref.setSize(area.width, area.height);
				} else {
					tref.setSize(area.width, area.height);
					tc1.setWidth(width / 2 - 1);
					tc2.setWidth(width - tc1.getWidth());
				}
			}
		});
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		environmentTable.setInput(configuration);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// Convert the table's items into a Map so that this can be saved in the
		// configuration's attributes.
		TableItem[] items = environmentTable.getTable().getItems();
		Map<String, String> map = new HashMap<String, String>(items.length);
		for (int i = 0; i < items.length; i++) {
			EnvironmentVariable var = (EnvironmentVariable) items[i].getData();
			map.put(var.getName(), var.getValue());
		}
		if (map.size() == 0) {
			configuration.removeAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES);
		} else {
			configuration.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, map);
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return LaunchConfigurationsMessages.EnvironmentTab_Environment_7;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return DebugPluginImages.getImage(IDebugUIConstants.IMG_OBJS_ENVIRONMENT);
	}

	/**
	 * Responds to a selection changed event in the environment table
	 * 
	 * @param event
	 *            the selection change event
	 */
	protected void handleTableSelectionChanged(SelectionChangedEvent event) {
		int size = ((IStructuredSelection) event.getSelection()).size();
		envEditButton.setEnabled(size == 1);
		envRemoveButton.setEnabled(size > 0);
	}

	protected void handleEnvRemoveButtonSelected() {
		IStructuredSelection sel = (IStructuredSelection) environmentTable.getSelection();
		environmentTable.getControl().setRedraw(false);
		for (Iterator<?> i = sel.iterator(); i.hasNext();) {
			EnvironmentVariable var = (EnvironmentVariable) i.next();
			environmentTable.remove(var);
		}
		environmentTable.getControl().setRedraw(true);
		updateLaunchConfigurationDialog();
	}

	protected void handleEnvAddButtonSelected() {
		MultipleInputDialog dialog = new MultipleInputDialog(getShell(), LaunchConfigurationsMessages.EnvironmentTab_22);
		dialog.addTextField(NAME_LABEL, null, false);
		dialog.addTextField(VALUE_LABEL, null, false);

		if (dialog.open() != Window.OK) {
			return;
		}

		String name = dialog.getStringValue(NAME_LABEL);
		String value = dialog.getStringValue(VALUE_LABEL);

		if (name != null && value != null && name.length() > 0 && value.length() > 0) {
			addVariable(new EnvironmentVariable(name.trim(), value.trim()));
		}
	}

	/**
	 * Attempts to add the given variable. Returns whether the variable was added or not (as when the user answers not to overwrite an existing
	 * variable).
	 * 
	 * @param variable
	 *            the variable to add
	 * @return whether the variable was added
	 */
	protected boolean addVariable(EnvironmentVariable variable) {
		String name = variable.getName();
		TableItem[] items = environmentTable.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			EnvironmentVariable existingVariable = (EnvironmentVariable) items[i].getData();
			if (existingVariable.getName().equals(name)) {
				boolean overWrite = MessageDialog.openQuestion(getShell(), LaunchConfigurationsMessages.EnvironmentTab_12,
						MessageFormat.format(LaunchConfigurationsMessages.EnvironmentTab_13, new Object[] { name })); //
				if (!overWrite) {
					return false;
				}
				environmentTable.remove(existingVariable);
				break;
			}
		}
		environmentTable.add(variable);
		updateLaunchConfigurationDialog();
		return true;
	}

	protected void handleEnvEditButtonSelected() {
		IStructuredSelection sel = (IStructuredSelection) environmentTable.getSelection();
		EnvironmentVariable var = (EnvironmentVariable) sel.getFirstElement();
		if (var == null) {
			return;
		}
		String originalName = var.getName();
		String value = var.getValue();
		MultipleInputDialog dialog = new MultipleInputDialog(getShell(), LaunchConfigurationsMessages.EnvironmentTab_11);
		dialog.addTextField(NAME_LABEL, originalName, false);
		dialog.addTextField(VALUE_LABEL, value, false);

		if (dialog.open() != Window.OK) {
			return;
		}
		String name = dialog.getStringValue(NAME_LABEL);
		value = dialog.getStringValue(VALUE_LABEL);
		if (!originalName.equals(name)) {
			if (addVariable(new EnvironmentVariable(name, value))) {
				environmentTable.remove(var);
			}
		} else {
			var.setValue(value);
			environmentTable.update(var, null);
			updateLaunchConfigurationDialog();
		}
	}
}
