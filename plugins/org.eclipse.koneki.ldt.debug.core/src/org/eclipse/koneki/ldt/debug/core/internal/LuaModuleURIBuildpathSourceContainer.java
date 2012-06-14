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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainer;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.koneki.ldt.core.LuaUtils;

/**
 * A source path container which searches in a lua project buildpath
 */
public class LuaModuleURIBuildpathSourceContainer extends AbstractSourceContainer {

	public LuaModuleURIBuildpathSourceContainer() {
	}

	public Object[] findSourceElements(String name) throws CoreException {

		// we support only module:// protocol
		if (name == null || !name.startsWith("module:///")) //$NON-NLS-1$
			return new Object[0];

		String moduleName = name.substring(10);

		// get launch configuration
		final ILaunchConfiguration configuration = getDirector().getLaunchConfiguration();
		String projectName;

		// get configuration from launch configuration
		projectName = configuration.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
		if (projectName == null)
			return new Object[0];
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IScriptProject prj = DLTKCore.create(project);

		// find IModuleSource from module name in buildpath of the project
		IModuleSource moduleSource = LuaUtils.getModuleSource(moduleName, prj);

		if (moduleSource != null) {
			IModelElement modelElement = moduleSource.getModelElement();
			if (modelElement instanceof IStorage) {
				return new Object[] { modelElement };
			} else if (modelElement != null && modelElement.getResource() instanceof IStorage)
				return new Object[] { modelElement.getResource() };
		}

		return new Object[0];
	}

	/**
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getName()
	 */
	@Override
	public String getName() {
		return "Lua Module URI in Buildpath Source Container"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getType()
	 */
	@Override
	public ISourceContainerType getType() {
		return null;
	}
}
