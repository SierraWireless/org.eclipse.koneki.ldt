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

package org.eclipse.koneki.ldt.ui.internal.preferences;

import java.util.ArrayList;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This class is a copy of the {@link org.eclipse.dltk.ui.preferences.CodeAssistConfigurationBlock} without the option
 * "Override or insert content assist" because this option is not working in LDT
 */
public class LuaCodeAssistConfigurationBlock extends AbstractConfigurationBlock {

	public LuaCodeAssistConfigurationBlock(PreferencePage mainPreferencePage, OverlayPreferenceStore store) {
		super(store, mainPreferencePage);
		getPreferenceStore().addKeys(createOverlayStoreKeys());
	}

	private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {
		ArrayList<OverlayKey> overlayKeys = new ArrayList<OverlayKey>();

		getOverlayKeys(overlayKeys);

		OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return keys;
	}

	protected void getOverlayKeys(ArrayList<OverlayKey> overlayKeys) {
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.CODEASSIST_AUTOACTIVATION));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.INT, PreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.CODEASSIST_AUTOINSERT));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.CODEASSIST_INSERT_COMPLETION));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.CODEASSIST_SORTER));
	}

	/**
	 * Creates page for appearance preferences.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the control for the preference page
	 */
	public Control createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());

		Composite composite;

		composite = createSubsection(control, null, PreferencesMessages.CodeAssistConfigurationBlock_insertionSection_title);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		addInsertionSection(composite);

		composite = createSubsection(control, null, PreferencesMessages.CodeAssistConfigurationBlock_autoactivationSection_title);
		composite.setLayout(layout);
		addAutoActivationSection(composite);

		return control;
	}

	Control autoActivation;

	protected void addAutoActivationSection(Composite composite) {
		String label;
		label = PreferencesMessages.DLTKEditorPreferencePage_enableAutoActivation;
		final Button autoactivation = addCheckBox(composite, label, PreferenceConstants.CODEASSIST_AUTOACTIVATION, 2);
		autoactivation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateAutoactivationControls();
			}
		});

		label = PreferencesMessages.DLTKEditorPreferencePage_autoActivationDelay;
		Control[] ctrl = addLabelledTextField(composite, label, PreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY, 4, 2, true);
		autoActivation = ctrl[1];
	}

	private void updateAutoactivationControls() {
		boolean autoactivation = getPreferenceStore().getBoolean(PreferenceConstants.CODEASSIST_AUTOACTIVATION);
		if (autoActivation != null) {
			autoActivation.setEnabled(autoactivation);
		}
	}

	protected void addInsertionSection(Composite composite) {

		String label;
		label = PreferencesMessages.DLTKEditorPreferencePage_insertSingleProposalsAutomatically;
		addCheckBox(composite, label, PreferenceConstants.CODEASSIST_AUTOINSERT, 2);
	}

	protected void initializeFields() {
		super.initializeFields();

		updateAutoactivationControls();
	}

}
