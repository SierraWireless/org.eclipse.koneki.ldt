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
package org.eclipse.koneki.ldt.debug.core.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainer;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.koneki.ldt.core.LuaUtils;

/**
 * A source path container which searches in a lua project buildpath
 */
public class LuaAbsoluteFileURIBuildpathSourceContainer extends AbstractSourceContainer {

	public LuaAbsoluteFileURIBuildpathSourceContainer() {
	}

	public Object[] findSourceElements(String name) throws CoreException {

		// we support only module:// protocol
		if (name == null || !name.startsWith("file:///")) //$NON-NLS-1$
			return new Object[0];

		// create absolute URI
		URI sourceURI;
		try {
			sourceURI = new URI(name);
		} catch (URISyntaxException e) {
			return new Object[0];
		}

		// -----------------------------------------------------------------
		// In a first time, we search in the build path of the workspace

		// get launch configuration
		final ILaunchConfiguration configuration = getDirector().getLaunchConfiguration();
		String projectName;

		// get configuration from launch configuration
		projectName = configuration.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
		if (projectName == null)
			return new Object[0];
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IScriptProject scriptProject = DLTKCore.create(project);

		// find IModuleSource from module name in buildpath of the project
		IModuleSource moduleSource = LuaUtils.getModuleSourceFromAbsoluteURI(sourceURI, scriptProject);

		if (moduleSource != null && moduleSource.getModelElement() != null && moduleSource.getModelElement().getResource() instanceof IStorage)
			return new Object[] { moduleSource.getModelElement().getResource() };

		// -----------------------------------------------------------------
		// In we don't find resource we search in all workspace
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(sourceURI);
		Object[] result = Arrays.copyOf(files, files.length, Object[].class);
		return result;
	}

	/**
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getName()
	 */
	@Override
	public String getName() {
		return "Lua Absolute File URI in Buildpath Source Container"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getType()
	 */
	@Override
	public ISourceContainerType getType() {
		return null;
	}
}
