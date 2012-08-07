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
package org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local.tab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.IInterpreterComboBlockContext;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpreterDescriptor;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.koneki.ldt.debug.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class LuaInterpreterTabComboBlock extends AbstractInterpreterComboBlock {

	private static final Status OK = new Status(Status.OK, Activator.PLUGIN_ID, "It is all good."); //$NON-NLS-1$
	private Composite control;
	private Button defaultButton;
	private Label defaultInterpreter;
	private List<IInterpreterInstall> interpreters = new ArrayList<IInterpreterInstall>();
	private Button alternateButton;
	private Combo interpretersCombo;
	private InterpreterDescriptor defaultInterpreterDescriptor;
	private IStatus status;
	private ListenerList listeners = new ListenerList();
	private boolean isControlCreated = false;
	private IInterpreterInstall interpreterInstall;

	public LuaInterpreterTabComboBlock(final IInterpreterComboBlockContext context) {
		super(context);
	}

	@Override
	public void createControl(final Composite parent) {

		final int columns = 3;
		final Font parentFont = parent.getFont();
		control = new Composite(parent, SWT.NONE);
		control.setFont(parentFont);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(control);
		GridLayoutFactory.swtDefaults().numColumns(columns).applyTo(control);

		// Group container
		final Group group = new Group(control, SWT.NONE);
		group.setFont(parentFont);
		group.setText(Messages.LuaInterpreterTabComboBlockRuntimeInterpreterLabel);
		GridLayoutFactory.swtDefaults().numColumns(columns).applyTo(group);
		GridDataFactory.swtDefaults().span(columns, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(group);

		// Default interpreter
		defaultButton = new Button(group, SWT.RADIO);
		defaultButton.setFont(parentFont);
		defaultButton.setText(Messages.LuaInterpreterTabComboBlockDefaultInterpreterLabel);
		defaultInterpreter = new Label(group, SWT.RADIO);
		defaultButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				setUseDefaultInterpreter();
			}
		});
		GridDataFactory.swtDefaults().span(columns - 1, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(defaultInterpreter);

		// Alternate interpreters
		alternateButton = new Button(group, SWT.RADIO);
		alternateButton.setFont(parentFont);
		alternateButton.setText(Messages.LuaInterpreterTabComboBlockAlternateInterpreterLabel);
		alternateButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				setUseAlternateInterpreter();
			}
		});

		interpretersCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		interpretersCombo.setFont(parentFont);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(interpretersCombo);
		interpretersCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				setInterpreterInstall(interpreters.get(interpretersCombo.getSelectionIndex()));
				refreshInterpreters();
				firePropertyChange();
			}
		});

		// Manage interpreters
		final Button manageButton = new Button(group, SWT.None);
		manageButton.setFont(parentFont);
		manageButton.setText(Messages.LuaInterpreterTabComboBlockManageInterpretersButton);
		manageButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				showInterpreterPreferencePage();
				refreshInterpreters();
			}
		});
		GridDataFactory.swtDefaults().hint(SWTUtil.getButtonWidthHint(manageButton), -1).applyTo(manageButton);
		isControlCreated = true;
	}

	/**
	 * @return true is UI elements are created
	 */
	private boolean isControlCreated() {
		return isControlCreated;
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setUseDefaultInterpreter() {
		if (isControlCreated()) {
			defaultInterpreter.setEnabled(true);
			defaultButton.setSelection(true);
			alternateButton.setSelection(false);
			interpretersCombo.setEnabled(false);
			firePropertyChange();
		}
	}

	private void setUseAlternateInterpreter() {
		if (isControlCreated()) {
			defaultButton.setSelection(false);
			defaultInterpreter.setEnabled(false);
			alternateButton.setSelection(true);
			interpretersCombo.setEnabled(true);
			firePropertyChange();
		}
	}

	@Override
	public IInterpreterInstall getInterpreter() {
		if (isDefaultInterpreter() && getDefaultInterpreterDescriptor() != null) {
			return getDefaultInterpreterDescriptor().getInterpreter();
		}
		return interpreterInstall;
	}

	@Override
	protected void setInterpreters(final List<IInterpreterInstall> interpreterList) {
		interpreters.clear();
		interpreters.addAll(interpreterList);

		// Sort by name
		Collections.sort(interpreters, new Comparator<IInterpreterInstall>() {
			public int compare(IInterpreterInstall o1, IInterpreterInstall o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});

		// Fill combo list
		if (isControlCreated()) {
			interpretersCombo.removeAll();
			for (final IInterpreterInstall install : interpreters) {
				interpretersCombo.add(install.getName());
			}

			// If there is not already an alternate one selected, Select an interpreter
			if (interpretersCombo.getSelectionIndex() == -1 && interpreters.size() > 0) {

				// Select previous interpreter if still available
				final IInterpreterInstall interpreter = getInterpreter();
				if (interpreter != null) {
					final int indexOfInterpreter = interpretersCombo.indexOf(interpreter.getName());
					interpretersCombo.select(indexOfInterpreter);
				} else {
					interpretersCombo.select(0);
				}
			}
		}
		firePropertyChange();
	}

	@Override
	public void refreshInterpreters() {

		// Update interpreters list
		fillWithWorkspaceInterpreters();

		if (isControlCreated()) {

			// Update default interpreter
			IInterpreterInstall defaultInterpreterInstall = null;
			if (getDefaultInterpreterDescriptor() != null) {
				defaultInterpreterInstall = getDefaultInterpreterDescriptor().getInterpreter();
				if (defaultInterpreterInstall != null) {
					defaultInterpreter.setText(defaultInterpreterInstall.getName());
					setStatus(OK);
				}
			}

			// Notice that there is no default interpreter
			if (defaultInterpreterInstall == null) {
				defaultInterpreter.setText("(undefined)"); //$NON-NLS-1$
				setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.LuaInterpreterTabComboBlockNoDefaultInterpreter));
			}

			// Mention that there is no interpreters in list
			if (interpretersCombo.getItemCount() < 1) {
				setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.LuaInterpreterTabComboBlockNoInterpreter));
			} else if (isDefaultInterpreter() && interpretersCombo.getSelectionIndex() == -1) {

				// Ask user to select its alternative interpreter
				setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.LuaInterpreterTabComboBlockSelectAnInterpreter));
			}
		}

		// In all cases, check given interpreter
		setPath(getInterpreterPath());
		firePropertyChange();
	}

	@Override
	public IStatus getStatus() {
		return status;
	}

	private void setStatus(IStatus istatus) {
		status = istatus;
	}

	@Override
	public IPath getInterpreterPath() {
		if (!isDefaultInterpreter()) {
			final IInterpreterInstall interpreter = getInterpreter();
			if (interpreter != null) {
				return ScriptRuntime.newInterpreterContainerPath(interpreter);
			}
			return null;
		}
		return ScriptRuntime.newDefaultInterpreterContainerPath();
	}

	@Override
	public void setDefaultInterpreterDescriptor(final InterpreterDescriptor descriptor) {
		defaultInterpreterDescriptor = descriptor;
	}

	protected InterpreterDescriptor getDefaultInterpreterDescriptor() {
		return defaultInterpreterDescriptor;
	}

	@Override
	public boolean isDefaultInterpreter() {
		return defaultButton != null && defaultButton.getSelection();
	}

	@Override
	public void refresh() {
		setDefaultInterpreterDescriptor(getDefaultInterpreterDescriptor());
	}

	@Override
	public void addPropertyChangeListener(final IPropertyChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removePropertyChangeListener(final IPropertyChangeListener listener) {
		listeners.remove(listener);
	}

	private void firePropertyChange() {
		final PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTY_INTERPRETER, null, getInterpreterPath());
		for (final Object listener : listeners.getListeners()) {
			final IPropertyChangeListener propertyChangeListenerlistener = (IPropertyChangeListener) listener;
			propertyChangeListenerlistener.propertyChange(event);
		}
	}

	@Override
	public void setPath(final IPath containerPath) {

		// Say yes, at first ...
		setStatus(OK);

		/*
		 * Check for surprises
		 */

		// No interpreters
		if (interpreters.isEmpty()) {
			setStatus(new Status(IStatus.ERROR, DLTKLaunchingPlugin.getUniqueIdentifier(),
					ScriptLaunchConfigurationConstants.ERR_NO_DEFAULT_INTERPRETER_INSTALL, Messages.LuaInterpreterTabComboBlockNoInterpreter, null));
		}

		// If it is path of default interpreter select it
		if (ScriptRuntime.newDefaultInterpreterContainerPath().equals(containerPath)) {
			setUseDefaultInterpreter();
		} else if (containerPath != null) {

			/*
			 * Check given interpreter path
			 */
			final IEnvironment environment = getContext().getEnvironment();
			IInterpreterInstall install = null;
			if (environment != null) {
				final String natureId = getContext().getNatureId();
				final String environmentId = environment.getId();
				install = ScriptRuntime.getInterpreterInstall(natureId, environmentId, containerPath);
			}

			// Is interpreter installed?
			if (install != null) {
				setInterpreterInstall(install);
				selectAlternateInterpreter(install);
			} else {
				setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.LuaInterpreterTabComboBlockNoInterpreter));

			}
		}
	}

	private void setInterpreterInstall(final IInterpreterInstall install) {
		interpreterInstall = install;
	}

	private void selectAlternateInterpreter(final IInterpreterInstall iInstall) {
		final int index = interpretersCombo.indexOf(iInstall.getName());
		if (index >= 0) {
			interpretersCombo.select(index);
		}
		setUseAlternateInterpreter();
	}
}
