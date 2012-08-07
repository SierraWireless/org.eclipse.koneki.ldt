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

import org.eclipse.debug.internal.ui.launchConfigurations.EnvironmentVariable;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.dltk.ui.dialogs.MultipleInputDialog;
import org.eclipse.jface.window.Window;

@SuppressWarnings("restriction")
public class LuaEnvironmentTab extends EnvironmentTab {

	private static final String NAME_LABEL = Messages.LuaEnvironmentTabNewVariableDialogName;
	private static final String VALUE_LABEL = Messages.LuaEnvironmentTabNewVariableDialogValue;

	@Override
	protected void handleEnvAddButtonSelected() {
		final MultipleInputDialog dialog = new MultipleInputDialog(getShell(), Messages.LuaEnvironmentTabNewVariableDialogTitle);
		dialog.addTextField(NAME_LABEL, null, false);
		dialog.addVariablesField(VALUE_LABEL, null, true);

		if (dialog.open() != Window.OK) {
			return;
		}

		final String name = dialog.getStringValue(NAME_LABEL);
		final String value = dialog.getStringValue(VALUE_LABEL);

		addVariable(new EnvironmentVariable(name.trim(), value.trim()));
		updateAppendReplace();
	}
}
