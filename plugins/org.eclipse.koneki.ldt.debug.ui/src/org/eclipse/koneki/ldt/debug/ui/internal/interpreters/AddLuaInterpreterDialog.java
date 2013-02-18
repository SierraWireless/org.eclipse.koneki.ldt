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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.internal.environment.LazyFileHandle;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterLibraryBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.IAddInterpreterDialogRequestor;
import org.eclipse.dltk.internal.debug.ui.interpreters.IScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersMessages;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.ui.environment.IEnvironmentUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.koneki.ldt.debug.core.IEmbeddedInterpreterInstallType;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.generic.LuaGenericInterpreterInstallType;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.generic.LuaGenericInterpreterUtil;
import org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.Info;
import org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.InterpreterFactory;
import org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.impl.InterpreterFactoryImpl;
import org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.impl.InterpreterPackageImpl;
import org.eclipse.koneki.ldt.debug.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class AddLuaInterpreterDialog extends StatusDialog implements IScriptInterpreterDialog {

	private IAddInterpreterDialogRequestor requestor;
	private IEnvironment environment;
	private IInterpreterInstall currentInterperter;
	private IInterpreterInstallType[] interpreterInstallTypes;

	// UI components
	private ComboViewer typesCombo;
	private Text nameText;
	private Text pathText;
	private Button browseButton;
	private Text argsText;
	private LuaInterpreterEnvironmentVariablesBlock environementVariableBlock;
	private Button handlesExecutionOption;
	private Group capabilitiesGroup;
	private Label capabilitiesDesctiptionLabel;
	private Button handlesFilesAsArguments;

	public AddLuaInterpreterDialog(final IAddInterpreterDialogRequestor requestor, final Shell shell, final IEnvironment environment,
			final IInterpreterInstallType[] interpreterInstallTypes, final IInterpreterInstall standin) {
		super(shell);
		this.requestor = requestor;
		this.environment = environment;
		this.currentInterperter = standin;
		this.interpreterInstallTypes = interpreterInstallTypes;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		Point margin = new Point(0, 0);
		margin.x = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		margin.y = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		Point spacing = new Point(0, 0);
		spacing.x = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		spacing.y = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		GridLayoutFactory.swtDefaults().spacing(spacing).margins(margin).numColumns(3).applyTo(container);
		GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(container);

		createLabel(container, InterpretersMessages.addInterpreterDialog_InterpreterEnvironmentType);
		typesCombo = new ComboViewer(container);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(2, 1).applyTo(typesCombo.getControl());

		createLabel(container, InterpretersMessages.addInterpreterDialog_InterpreterExecutableName);
		pathText = new Text(container, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).hint(300, SWT.DEFAULT).applyTo(pathText);
		browseButton = new Button(container, SWT.PUSH);
		browseButton.setText(InterpretersMessages.addInterpreterDialog_browse1);
		GridDataFactory.swtDefaults().hint(SWTUtil.getButtonWidthHint(browseButton), -1).applyTo(browseButton);

		createLabel(container, InterpretersMessages.addInterpreterDialog_InterpreterEnvironmentName);
		nameText = new Text(container, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).applyTo(nameText);

		createLabel(container, InterpretersMessages.AddInterpreterDialog_iArgs);
		argsText = new Text(container, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).applyTo(argsText);

		environementVariableBlock = new LuaInterpreterEnvironmentVariablesBlock(new AddInterpreterDialogAdapter(requestor, getShell(),
				interpreterInstallTypes, currentInterperter));
		final Composite environmentComposite = (Composite) environementVariableBlock.createControl(container);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(environmentComposite);
		GridDataFactory.swtDefaults().grab(true, true).span(3, 1).align(SWT.FILL, SWT.FILL).applyTo(environmentComposite);

		// Interpreter Capabilities
		capabilitiesGroup = new Group(container, SWT.None);
		capabilitiesGroup.setText(Messages.AddLuaInterpreterDialog_CapabilitesGroupLabel);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).applyTo(capabilitiesGroup);
		GridDataFactory.swtDefaults().grab(true, false).span(3, 1).align(SWT.FILL, SWT.FILL).applyTo(capabilitiesGroup);

		capabilitiesDesctiptionLabel = new Label(capabilitiesGroup, SWT.NONE);
		toItalic(capabilitiesDesctiptionLabel);
		GridDataFactory.swtDefaults().span(3, 1).grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(capabilitiesDesctiptionLabel);

		handlesExecutionOption = new Button(capabilitiesGroup, SWT.CHECK);
		handlesExecutionOption.setText(Messages.AddLuaInterpreterDialog_ExecutionOption);

		handlesFilesAsArguments = new Button(capabilitiesGroup, SWT.CHECK);
		handlesFilesAsArguments.setText(Messages.AddLuaInterpreterDialog_FilesAsArguments);

		applyDialogFont(container);
		hookListeners();
		init();

		return container;
	}

	private <T extends Control> T toItalic(final T control) {
		final FontRegistry fontRegistry = JFaceResources.getFontRegistry();
		for (final FontData fontData : control.getFont().getFontData()) {
			final Font font = fontRegistry.getItalic(fontData.getName());
			control.setFont(font);
		}
		return control;
	}

	private Label createLabel(final Composite container, final String text) {
		final Label label = new Label(container, SWT.NONE);
		label.setText(text);
		GridDataFactory.swtDefaults().applyTo(label);
		return label;
	}

	private void hookListeners() {
		typesCombo.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateOnInterpreterTypeChange();
			}
		});

		browseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				browseForInstallation();
			}
		});

		pathText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateStatusLine();
			}
		});

		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateStatusLine();
			}
		});

	}

	private void init() {

		handlesExecutionOption.setSelection(LuaGenericInterpreterUtil.interpreterHandlesExecuteOption(currentInterperter));
		handlesFilesAsArguments.setSelection(LuaGenericInterpreterUtil.interpreterHandlesFilesAsArgument(currentInterperter));

		// init type combo
		typesCombo.setContentProvider(new ArrayContentProvider());
		typesCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IInterpreterInstallType) {
					return ((IInterpreterInstallType) element).getName();
				}
				return super.getText(element);
			}
		});
		typesCombo.setInput(interpreterInstallTypes);
		typesCombo.getControl().setEnabled(currentInterperter == null);

		// init field values
		if (currentInterperter != null) {
			typesCombo.setSelection(new StructuredSelection(currentInterperter.getInterpreterInstallType()));
			updateOnInterpreterTypeChange();

			if (currentInterperter.getInstallLocation().length() > 0)
				pathText.setText(currentInterperter.getInstallLocation().toOSString());

			nameText.setText(currentInterperter.getName());

			String args = currentInterperter.getInterpreterArgs();
			if (args != null)
				argsText.setText(args);

			environementVariableBlock.initializeFrom(currentInterperter, currentInterperter.getInterpreterInstallType());
		} else {
			// for user experience, the selected type by default should be Lua Generic
			for (IInterpreterInstallType type : interpreterInstallTypes) {
				if (type instanceof LuaGenericInterpreterInstallType && typesCombo.getSelection().isEmpty())
					typesCombo.setSelection(new StructuredSelection(type));
			}
		}

		// update environment block buttons
		environementVariableBlock.update();
	}

	/** Disables all {@link Control}s from {@link #capabilitiesGroup} */
	private void setCapabilityGroupEnabled(final boolean enabled) {
		final Control[] controls = { capabilitiesGroup, handlesExecutionOption, handlesFilesAsArguments };
		for (final Control control : controls)
			if (control != null)
				control.setEnabled(enabled);
		if (enabled)
			capabilitiesDesctiptionLabel.setText(Messages.AddLuaInterpreterDialog_WhatAreCapabilitiesLabel);
		else
			capabilitiesDesctiptionLabel.setText(Messages.AddLuaInterpreterDialog_InterpreterNotConfigurable);
	}

	private void updateOnInterpreterTypeChange() {
		final IInterpreterInstallType selectedType = getSelectedInterpreterType();
		final boolean isEmbedded = selectedType instanceof IEmbeddedInterpreterInstallType;
		browseButton.setEnabled(!isEmbedded);
		pathText.setEnabled(!isEmbedded);
		setCapabilityGroupEnabled(!isEmbedded);

		// Set path text as Embedded because we are unable to retrieve the default path of the embedded interpreter type
		final String embeddedPathValue = "(Embedded)"; //$NON-NLS-1$
		if (isEmbedded) {
			pathText.setText(embeddedPathValue);
		} else if (embeddedPathValue.equals(pathText.getText())) {
			pathText.setText(""); //$NON-NLS-1$
		} else if (currentInterperter != null) {
			pathText.setText(currentInterperter.getInstallLocation().toOSString());
		}
	}

	private IInterpreterInstallType getSelectedInterpreterType() {
		return (IInterpreterInstallType) ((IStructuredSelection) typesCombo.getSelection()).getFirstElement();
	}

	private void browseForInstallation() {
		IEnvironmentUI environmentUI = (IEnvironmentUI) environment.getAdapter(IEnvironmentUI.class);
		if (environmentUI != null) {
			String defaultPath = currentInterperter != null ? currentInterperter.getInstallLocation().toOSString() : null;
			String newPath = environmentUI.selectFile(getShell(), IEnvironmentUI.EXECUTABLE, defaultPath);
			if (newPath != null) {
				pathText.setText(newPath);
			}
		}
	}

	@Override
	protected void okPressed() {
		if (currentInterperter == null) {
			IInterpreterInstallType selectedType = getSelectedInterpreterType();
			currentInterperter = new InterpreterStandin(selectedType, createUniqueId(selectedType));

			// notify the preference page of the new interpreter, the interpreter have to be filled
			setFieldValuesToInterpreter();
			requestor.interpreterAdded(currentInterperter);
		} else {
			setFieldValuesToInterpreter();
		}
		super.okPressed();
	}

	private String createUniqueId(IInterpreterInstallType interpreterType) {
		String id = null;
		do {
			id = String.valueOf(System.currentTimeMillis());
		} while (interpreterType.findInterpreterInstall(id) != null);
		return id;
	}

	protected void setFieldValuesToInterpreter() {
		currentInterperter.setInstallLocation(new LazyFileHandle(environment.getId(), new Path(pathText.getText().trim())));
		currentInterperter.setName(nameText.getText().trim());

		final String argString = argsText.getText().trim();
		if (argString != null && argString.length() > 0) {
			currentInterperter.setInterpreterArgs(argString);
		} else {
			currentInterperter.setInterpreterArgs(null);
		}

		/*
		 * Update interpreter capabilities
		 */

		// Execution option
		final boolean executionOptionChecked = handlesExecutionOption != null && handlesExecutionOption.getSelection();

		// Files as argument option
		final boolean filesAsArgumentOptionChecked = handlesFilesAsArguments != null && handlesFilesAsArguments.getSelection();

		final InterpreterFactory factory = InterpreterFactoryImpl.eINSTANCE;
		final Info info = factory.createInfo();
		info.setExecuteOptionCapable(executionOptionChecked);
		info.setFileAsArgumentsCapable(filesAsArgumentOptionChecked);
		currentInterperter.replaceExtension(InterpreterPackageImpl.eINSTANCE.getInfo(), info);

		environementVariableBlock.performApply(currentInterperter);
	}

	@Override
	public boolean execute() {
		return open() == Window.OK;
	}

	@Override
	public void updateStatusLine() {
		String path = pathText.getText().trim();
		if (path.isEmpty()) {
			updateStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, InterpretersMessages.addInterpreterDialog_enterLocation));
		} else {
			String name = nameText.getText().trim();
			if (name.isEmpty()) {
				updateStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, InterpretersMessages.addInterpreterDialog_enterName));
			} else if (requestor.isDuplicateName(name, currentInterperter)) {
				updateStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, InterpretersMessages.addInterpreterDialog_duplicateName));
			} else {
				updateStatus(Status.OK_STATUS);
			}
		}
	}

	/**
	 * This method is override just to fix a scope problem between IStatus and Dialog super class
	 */
	@Override
	public void setButtonLayoutData(Button button) {
		super.setButtonLayoutData(button);
	}

	/**
	 * Adapter for the Environment Variable Block The block doesn't take a IAddScriptInterpreter dialog but a AddScriptInterpreterDialog
	 */
	private class AddInterpreterDialogAdapter extends AddScriptInterpreterDialog {

		public AddInterpreterDialogAdapter(IAddInterpreterDialogRequestor requestor, Shell shell, IInterpreterInstallType[] interpreterInstallTypes,
				IInterpreterInstall editedInterpreter) {
			super(requestor, shell, interpreterInstallTypes, editedInterpreter);
		}

		@Override
		protected AbstractInterpreterLibraryBlock createLibraryBlock(AddScriptInterpreterDialog dialog) {
			return null;
		}

		@Override
		public IEnvironment getEnvironment() {
			return environment;
		}

		@Override
		public void updateLibraries(EnvironmentVariable[] newVars, EnvironmentVariable[] oldVars) {
		}

		@Override
		protected void updateValidateInterpreterLocation() {
		}

		@Override
		public Shell getShell() {
			return AddLuaInterpreterDialog.this.getShell();
		}

		@Override
		public void updateStatusLine() {
			AddLuaInterpreterDialog.this.updateStatusLine();
		}

		@Override
		public void setSystemLibraryStatus(IStatus status) {
		}

		@Override
		public void setButtonLayoutData(Button button) {
			AddLuaInterpreterDialog.this.setButtonLayoutData(button);
		}

		@Override
		public int open() {
			return OK;
		}
	}

}
