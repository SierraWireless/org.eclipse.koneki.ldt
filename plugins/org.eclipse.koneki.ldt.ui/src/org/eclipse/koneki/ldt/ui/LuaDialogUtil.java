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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.koneki.ldt.core.LuaUtils;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.osgi.util.NLS;
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
		// initialize selection dialog
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new WorkbenchLabelProvider());
		dialog.setTitle(Messages.LuaDialogUtil_title);
		dialog.setMessage(Messages.LuaDialogUtil_message);
		dialog.setElements(LuaUtils.getLuaProjects());

		// initialize default selected project
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

	public static final IFile openSelectScriptFromProjectDialog(Shell shell, IProject project) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new WorkbenchLabelProvider());

		dialog.setElements(getContainerScript(project, shell).toArray());
		dialog.setMessage("Select a script file.");
		dialog.setTitle("Select Script");
		if (dialog.open() == Window.OK) {
			return (IFile) dialog.getResult()[0];
		}
		return null;
	}

	private static List<IFile> getContainerScript(IContainer container, Shell shell) {
		ArrayList<IFile> list = new ArrayList<IFile>();

		try {
			if (container.exists()) {
				for (IResource child : container.members()) {

					if (child instanceof IFile) {
						IFile file = (IFile) child;
						if (file.getName().endsWith(".lua")) //$NON-NLS-1$
							list.add(file);
					} else if (child instanceof IContainer) {
						IContainer childContainer = (IContainer) child;
						list.addAll(getContainerScript(childContainer, shell));
					}
				}
			} else {
				MessageDialog.openError(shell, "Unable to browse script", "The given project doesn't exist.");
			}
		} catch (CoreException e) {
			String errorMessage = NLS.bind("Unable to find scripts files of {0}", container.getFullPath());
			Activator.logError(errorMessage, e);
			MessageDialog.openError(shell, "Unable to browse script", errorMessage);
		}
		return list;
	}
}
