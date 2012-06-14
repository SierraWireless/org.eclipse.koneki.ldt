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

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.koneki.ldt.core.LuaUtils;

public final class LuaModuleURIUtil {
	public static final String MODULE_SCHEME = "module"; //$NON-NLS-1$

	private LuaModuleURIUtil() {
	}

	/**
	 * @return the Module URI of a {@link ISourceModule}
	 */
	public static URI getModuleURI(ISourceModule module) throws URISyntaxException {
		String moduleFullName = LuaUtils.getModuleFullName(module);
		return new URI(MODULE_SCHEME + ":///" + moduleFullName); //$NON-NLS-1$
	}

	/**
	 * @return the Module URI of a {@link ISourceModule}
	 */
	public static Boolean isModuleURI(URI uri) {
		return MODULE_SCHEME.equalsIgnoreCase(uri.getScheme());
	}

	/**
	 * @return the module name from the module uri
	 */
	public static String getModuleName(URI uri) {
		if (isModuleURI(uri)) {
			String path = uri.getPath();
			if (path != null && path.startsWith("/")) //$NON-NLS-1$
				return path.substring(1);
		}
		return null;
	}
}
