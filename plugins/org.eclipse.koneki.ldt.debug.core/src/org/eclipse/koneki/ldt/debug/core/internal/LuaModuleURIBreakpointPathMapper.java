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
import java.util.HashMap;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.debug.core.model.IScriptBreakpointPathMapperExtension;
import org.eclipse.koneki.ldt.core.LuaUtils;

/**
 * A breakpoint path mapper which support "module://" URI Protocol. <br>
 * Translate absolute local URI to module URI.
 */
public class LuaModuleURIBreakpointPathMapper implements IScriptBreakpointPathMapperExtension {
	private HashMap<URI, URI> cache;
	private IScriptProject scriptProject;

	public LuaModuleURIBreakpointPathMapper(IScriptProject project) {
		this.scriptProject = project;
		this.cache = new HashMap<URI, URI>();
	}

	public void clearCache() {
		cache.clear();
	}

	public URI map(URI uri) {
		// check the cache
		if (cache.containsKey(uri)) {
			return cache.get(uri);
		}
		URI result = uri;

		// find module from an absolute local file URI
		ISourceModule module = LuaUtils.getSourceModuleFromAbsoluteURI(uri, scriptProject);

		// if we found the module
		if (module != null) {
			try {
				// return the module URI
				result = LuaModuleURIUtil.getModuleURI(module);
			} catch (URISyntaxException e) {
				Activator.logWarning("Unable to get the Module URI for file :" + uri, e); //$NON-NLS-1$
			}
		}

		cache.put(uri, result);
		return result;
	}
}
