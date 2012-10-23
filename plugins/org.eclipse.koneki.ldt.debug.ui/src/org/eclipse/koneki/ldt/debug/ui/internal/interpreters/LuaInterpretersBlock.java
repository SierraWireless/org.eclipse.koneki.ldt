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

import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.debug.ui.interpreters.IScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersMessages;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class LuaInterpretersBlock extends InterpretersBlock {

	private Button editButton;

	@Override
	protected String getCurrentNature() {
		return LuaNature.ID;
	}

	@Override
	protected IScriptInterpreterDialog createInterpreterDialog(IEnvironment environment, IInterpreterInstall standin) {
		return new AddLuaInterpreterDialog(this, getShell(), environment, ScriptRuntime.getInterpreterInstallTypes(getCurrentNature()), standin);
	}

	@Override
	protected Button createPushButton(Composite parent, String label) {
		Button button = super.createPushButton(parent, label);

		// Hide Search button
		if (InterpretersMessages.InstalledInterpretersBlock_6.equals(label)) {
			button.setVisible(false);
		}

		// keep a pointer on Edit button
		if (InterpretersMessages.InstalledInterpretersBlock_4.equals(label)) {
			editButton = button;
		}

		return button;
	}

	@Override
	protected void enableButtons() {
		super.enableButtons();

		// Disable Edit button if the selection contain one interpreter contributed by extension points
		Object[] selectedInterpreters = ((IStructuredSelection) fInterpreterList.getSelection()).toArray();
		boolean enableEdit = selectedInterpreters.length > 0;
		for (Object selected : selectedInterpreters) {
			if (selected instanceof IInterpreterInstall) {
				IInterpreterInstall interpreter = (IInterpreterInstall) selected;
				boolean isContributed = ScriptRuntime.isContributedInterpreterInstall(interpreter.getId());
				enableEdit = enableEdit && !isContributed;
			}
		}
		editButton.setEnabled(enableEdit);
	}
}
