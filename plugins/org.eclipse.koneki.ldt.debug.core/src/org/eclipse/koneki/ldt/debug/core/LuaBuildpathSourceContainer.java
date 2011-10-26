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
package org.eclipse.koneki.ldt.debug.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModule;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

/**
 * A source path container which searches in a luaEmbedded project buildpath
 */
public class LuaBuildpathSourceContainer extends AbstractSourceContainer {

	public LuaBuildpathSourceContainer() {
	}

	public Object[] findSourceElements(String name) throws CoreException {
		final ILaunchConfiguration configuration = getDirector().getLaunchConfiguration();
		String projectName;
		projectName = configuration.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);

		if (projectName != null) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			IScriptProject prj = DLTKCore.create(project);

			IProjectFragment[] allProjectFragments = prj.getAllProjectFragments();
			// search in all project fragment
			IPath path = new Path(name);
			for (IProjectFragment projectFragment : allProjectFragments) {
				Object result = searchInIParent(projectFragment, path);
				if (result != null)
					return new Object[] { result };
			}
		}
		return new Object[0];
	}

	private Object searchInIParent(IParent parent, IPath path) throws CoreException {
		if (path.segmentCount() > 1) {
			// search scriptfolder
			String firstSegment = path.segment(0);
			IModelElement[] children = parent.getChildren();
			for (IModelElement child : children) {
				if (child instanceof IScriptFolder) {
					if (firstSegment.equals(child.getElementName())) {
						path = path.removeFirstSegments(1);
						Object result = searchInIParent((IScriptFolder) child, path);
						if (result != null)
							return result;
					}
				}
			}
		} else if (path.segmentCount() == 1) {
			// search module
			String firstSegment = path.segment(0);
			IModelElement[] children = parent.getChildren();
			for (IModelElement child : children) {
				if (child instanceof IScriptFolder) {
					IScriptFolder scriptFolder = (IScriptFolder) child;
					if (scriptFolder.isRootFolder()) {
						Object result = searchInIParent((IScriptFolder) child, path);
						if (result != null)
							return result;
					}
				} else if (child instanceof IModule) {
					if (firstSegment.equals(child.getElementName())) {
						if (child.getResource() != null)
							return child.getResource();
						else if (child instanceof IStorage)
							return child;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getName()
	 */
	@Override
	public String getName() {
		return "Lua Embedded Buildpath Source Container"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getType()
	 */
	@Override
	public ISourceContainerType getType() {
		return null;
	}
}
