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
package org.eclipse.koneki.ldt.debug.ui.internal.interpreters;

import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.debug.ui.interpreters.InterpretersUpdater;
import org.eclipse.dltk.internal.debug.ui.IScriptDebugHelpContextIds;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersMessages;
import org.eclipse.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.launching.ScriptRuntime.DefaultInterpreterEntry;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

public class LuaInterpreterPreferencePage extends ScriptInterpreterPreferencePage {

	private LuaInterpretersBlock fInterpretersBlock;

	@Override
	public InterpretersBlock createInterpretersBlock() {
		return new LuaInterpretersBlock();
	}

	/**
	 * Copy of the super method without the initialization of the default interpreter and customizing interpreter list validation
	 */
	@Override
	protected Control createContents(final Composite ancestor) {
		initializeDialogUnits(ancestor);

		noDefaultAndApplyButton();

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		ancestor.setLayout(layout);

		fInterpretersBlock = (LuaInterpretersBlock) createInterpretersBlock();
		fInterpretersBlock.createControl(ancestor);
		Control control = fInterpretersBlock.getControl();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		control.setLayoutData(data);

		fInterpretersBlock.restoreColumnSettings(getDialogSettings(false), IScriptDebugHelpContextIds.INTERPRETER_PREFERENCE_PAGE);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(ancestor, IScriptDebugHelpContextIds.INTERPRETER_PREFERENCE_PAGE);
		checkDefaultInterpreter();
		fInterpretersBlock.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {

				validateInterpreterList();
			}
		});
		applyDialogFont(ancestor);
		validateInterpreterList();
		return ancestor;
	}

	/**
	 * Retrieve default interpreter in preference and check it is the UI
	 */
	private void checkDefaultInterpreter() {
		final IEnvironment[] environments = EnvironmentManager.getEnvironments();
		for (final IEnvironment environment : environments) {
			// Retrieve default
			final String currentNature = fInterpretersBlock.getCurrentNature();
			final DefaultInterpreterEntry defaultInterpreterEntry = new DefaultInterpreterEntry(currentNature, environment.getId());
			final IInterpreterInstall defaultInterpreter = ScriptRuntime.getDefaultInterpreterInstall(defaultInterpreterEntry);
			if (defaultInterpreter != null) {
				// Find the interpreter in the list
				for (final IInterpreterInstall interpreter : fInterpretersBlock.getInterpreters()) {
					if (defaultInterpreter.equals(interpreter)) {
						// Check it
						fInterpretersBlock.setCheckedInterpreter(interpreter);
						return;
					}
				}
			}
		}
	}

	private void validateInterpreterList() {
		// Remove old messages
		setErrorMessage(null);

		// Check if no default interpreter
		if (fInterpretersBlock.getInterpreters().length > 0 && fInterpretersBlock.getSelection().isEmpty()) {
			setErrorMessage(InterpretersMessages.InterpreterPreferencePage_pleaseSetDefaultInterpreter);

			// Check if no interpreter
		} else if (fInterpretersBlock.getInterpreters().length == 0) {
			setErrorMessage(InterpretersMessages.InterpreterPreferencePage_addInterpreter);
		}
	}

	/**
	 * Copy of the super method but using the current class's fInterpretersBlock private attribute
	 */
	@Override
	public boolean performOk() {
		final boolean[] canceled = new boolean[] { false };
		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
				IInterpreterInstall[] defaultInterpreter = fInterpretersBlock.getCheckedInterpreters();
				IInterpreterInstall[] interpreters = fInterpretersBlock.getInterpreters();

				// TODO BUG_ECLIPSE 390358
				InterpretersUpdater updater = new LuaInterpretersUpdater();
				if (!updater.updateInterpreterSettings(fInterpretersBlock.getCurrentNature(), interpreters, defaultInterpreter)) {
					canceled[0] = true;
				}
			}
		});

		if (canceled[0]) {
			return false;
		}

		// save column widths
		fInterpretersBlock.saveColumnSettings(getDialogSettings(true), IScriptDebugHelpContextIds.INTERPRETER_PREFERENCE_PAGE);

		return true;
	}

	/**
	 * Copy of the super method but using the current class's fInterpretersBlock private attribute
	 */
	protected IDialogSettings getDialogSettings(boolean isSaving) {
		final IDialogSettings settings = DLTKDebugUIPlugin.getDefault().getDialogSettings();
		final String nature = fInterpretersBlock.getCurrentNature();
		IDialogSettings section = settings.getSection(nature);
		if (section == null) {
			if (isSaving) {
				section = settings.addNewSection(nature);
			} else {
				section = settings;
			}
		}
		return section;
	}

}
