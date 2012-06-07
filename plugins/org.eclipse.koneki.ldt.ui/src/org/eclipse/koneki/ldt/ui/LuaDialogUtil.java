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
package org.eclipse.koneki.ldt.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.window.Window;
import org.eclipse.koneki.ldt.core.LuaUtils;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Utility class, contains commons methods which could be used to implements UI for Lua
 */
public final class LuaDialogUtil {
	private LuaDialogUtil() {
	}

	public static final IProject openSelectLuaProjectDialog(Shell shell, String projectName) {
		// initialise selection dialog
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new WorkbenchLabelProvider());
		dialog.setTitle(Messages.LuaDialogUtil_title);
		dialog.setMessage(Messages.LuaDialogUtil_message);
		dialog.setElements(LuaUtils.getLuaProjects());

		// initialise default selected project
		if (projectName != null && !projectName.isEmpty()) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project != null)
				dialog.setInitialSelections(new Object[] { project });
		}

		// open dialog and get result
		if (dialog.open() == Window.OK) {
			Object result = dialog.getFirstResult();
			if (result instanceof IProject) {
				return (IProject) result;

			}
		}
		return null;
	}
}
