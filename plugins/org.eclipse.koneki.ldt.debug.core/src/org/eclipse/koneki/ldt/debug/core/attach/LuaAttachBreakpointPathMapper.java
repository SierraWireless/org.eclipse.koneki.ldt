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
package org.eclipse.koneki.ldt.debug.core.attach;

import java.net.URI;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.debug.core.model.IScriptBreakpointPathMapperExtension;
import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;
import org.eclipse.koneki.ldt.debug.core.Activator;

/**
 * Breakpoint path mapper for lua project use to translate uri of breakpoint from ide file system to remote file system
 */
public class LuaAttachBreakpointPathMapper implements IScriptBreakpointPathMapperExtension {
	private HashMap<URI, URI> cache;
	private String mapTo;
	private IScriptProject scriptProject;

	public LuaAttachBreakpointPathMapper(IScriptProject project, String mapTo) {
		this.mapTo = mapTo;
		this.scriptProject = project;
		this.cache = new HashMap<URI, URI>();
	}

	public void clearCache() {
		cache.clear();
	}

	public URI map(URI uri) {
		// no mapTo, return original uri
		if (mapTo == null || "".equals(mapTo)) { //$NON-NLS-1$
			return uri;
		}

		// check the cache
		if (cache.containsKey(uri)) {
			return cache.get(uri);
		}

		// compute distant path
		URI result;
		// get path from uri
		final IPath path = new Path(uri.getPath());
		// search path in workspace and strip source folder
		IPath strippedPath = stripSourceFolders(path);
		if (strippedPath != null) {
			// if found add remote working directory path to stripped path to find the absolute remote path
			IPath outgoing = new Path(mapTo).append(strippedPath);
			// translate path in uri
			result = ScriptLineBreakpoint.makeUri(outgoing);

		} else {
			// else return the given uri
			result = uri;
		}
		cache.put(uri, result);
		return result;
	}

	private IPath stripSourceFolders(IPath path) {
		try {
			IProjectFragment[] fragments = scriptProject.getAllProjectFragments();
			for (int i = 0; i < fragments.length; i++) {
				IProjectFragment frag = fragments[i];
				// support external fragment
				if (frag.isExternal()) {
					// support external folder
					IPath localPath = EnvironmentPathUtils.getLocalPath(frag.getPath());
					if (localPath.isPrefixOf(path)) {
						IPath temp = path.removeFirstSegments(localPath.segmentCount()).setDevice(null);
						return temp;
					}
					continue;
				} else if (frag.isArchive()) {
					// support external archive
					// TODO SDK will be surely support in next version
					continue;
				} else {
					// support source folder
					final IPath projectPath = frag.getScriptProject().getProject().getLocation();
					if (projectPath.isPrefixOf(path)) {
						// remove project path
						IPath temp = path.removeFirstSegments(projectPath.segmentCount()).setDevice(null);
						// remove source folder
						final String name = frag.getElementName();
						if (temp.segmentCount() > 0 && temp.segment(0).equals(name)) {
							return temp.removeFirstSegments(1);
						}
					} else {
						// support linked ressource
						IPath localPath = frag.getResource().getLocation();
						if (localPath.isPrefixOf(path)) {
							IPath temp = path.removeFirstSegments(localPath.segmentCount()).setDevice(null);
							return temp;
						}
					}
				}
			}
		} catch (CoreException e) {
			Activator.logError("Breakpoint path mapper failed to map this path :" + path, e); //$NON-NLS-1$
		}

		return null;
	}
}
