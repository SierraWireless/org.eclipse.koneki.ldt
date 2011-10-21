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
package org.eclipse.koneki.ldt.wizards;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.ui.wizards.ILocationGroup;
import org.eclipse.dltk.ui.wizards.IProjectWizard;
import org.eclipse.dltk.ui.wizards.ProjectCreator;
import org.eclipse.jface.wizard.IWizardPage;

public class LuaProjectCreator extends ProjectCreator {

	/**
	 * Adds a step for creating default file in default source folder.
	 * 
	 * @param owner
	 *            IProjectWizard asking for this project creator
	 * @param locationGroup
	 *            must be a IWizardPage from IProjectWizard described above
	 */
	public LuaProjectCreator(IProjectWizard owner, ILocationGroup locationGroup) {
		super(owner, locationGroup);
		addStep(IProjectCreateStep.KIND_FINISH, 0, new CreateDefaultSourceFolderProjectCreateStep(), (IWizardPage) locationGroup);
	}

	/**
	 * Sets a specific source folder instead of project's root folder.
	 */
	@Override
	protected List<IBuildpathEntry> getDefaultBuildpathEntries() {
		// Creating source folder
		final IFolder sourcefolder = getProject().getFolder(LuaWizardContants.SOURCE_FOLDER);
		final IBuildpathEntry newSourceEntry = DLTKCore.newSourceEntry(sourcefolder.getFullPath());
		final List<IBuildpathEntry> buildPath = new ArrayList<IBuildpathEntry>();
		buildPath.add(newSourceEntry);
		return buildPath;
	}

	/**
	 * @see #getDefaultBuildpathEntries()
	 * @see ProjectCreator#initBuildpath(IProgressMonitor)
	 */
	protected IBuildpathEntry[] initBuildpath(IProgressMonitor monitor) throws CoreException {
		final List<IBuildpathEntry> entries = new ArrayList<IBuildpathEntry>(getDefaultBuildpathEntries());
		monitor.done();
		return entries.toArray(new IBuildpathEntry[entries.size()]);
	}

	/**
	 * Creates a default file named LuaWizardContants.DEFAULT_MAIN_FILE in default source folder.
	 */
	private class CreateDefaultSourceFolderProjectCreateStep extends ProjectCreateStep {
		/**
		 * @see ProjectCreateStep#execute(IProject,IProgressMonitor)
		 */
		@Override
		public void execute(IProject project, IProgressMonitor monitor) throws CoreException, InterruptedException {
			monitor.beginTask(Messages.LuaProjectCreatorInitializingSourceFolder, 1);
			final IFolder sourcefolder = project.getFolder(LuaWizardContants.SOURCE_FOLDER);
			if (sourcefolder.exists()) {
				// Create main file for application project
				final byte[] bytes = LuaWizardContants.MAIN_FILE_CONTENT.getBytes();
				final IFile mainFile = sourcefolder.getFile(LuaWizardContants.DEFAULT_MAIN_FILE);
				mainFile.create(new ByteArrayInputStream(bytes), false, new SubProgressMonitor(monitor, 1));
			}
			monitor.done();
		}
	}

}