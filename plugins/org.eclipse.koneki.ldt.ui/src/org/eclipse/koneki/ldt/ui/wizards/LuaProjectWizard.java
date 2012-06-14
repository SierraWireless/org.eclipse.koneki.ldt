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
package org.eclipse.koneki.ldt.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.ui.wizards.GenericDLTKProjectWizard;
import org.eclipse.dltk.ui.wizards.ProjectCreator;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.ImageConstants;
import org.eclipse.koneki.ldt.ui.wizards.pages.LuaProjectSettingsPage;
import org.eclipse.ui.IWorkbench;

/**
 * A wizard tailored only for available functionalities.
 */
public class LuaProjectWizard extends GenericDLTKProjectWizard {
	private final LuaProjectSettingsPage firstPage;

	public LuaProjectWizard() {
		firstPage = new LuaProjectSettingsPage();
		setWindowTitle(Messages.LuaProjectWizardProjectWindowTitle);
	}

	@Override
	public void addPages() {
		addPage(getFirstPage());
		addPage(new ProjectWizardSecondPage(getFirstPage()) {
			@Override
			protected void updateStatus(IStatus status) {
				super.updateStatus(status);

				// when the buildpath change, check if there are at least one folder in the source path
				IBuildpathEntry[] rawBuildPath = getRawBuildPath();
				boolean sourcepathfound = false;
				for (IBuildpathEntry buildpathEntry : rawBuildPath) {
					if (buildpathEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE) {
						sourcepathfound = true;
						break;
					}
				}
				if (!sourcepathfound) {
					setMessage(Messages.LuaProjectWizard_warning_noSourceFolder, IMessageProvider.WARNING);
				}
			}
		});
	}

	@Override
	public String getScriptNature() {
		return LuaNature.ID;
	}

	@Override
	protected ProjectCreator createProjectCreator() {
		return new LuaProjectCreator(this, getFirstPage());
	}

	@Override
	protected LuaProjectSettingsPage getFirstPage() {
		return firstPage;
	}

	/**
	 * @see org.eclipse.dltk.ui.wizards.NewElementWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setDefaultPageImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.LUA_WIZARD_BAN));
	}
}
