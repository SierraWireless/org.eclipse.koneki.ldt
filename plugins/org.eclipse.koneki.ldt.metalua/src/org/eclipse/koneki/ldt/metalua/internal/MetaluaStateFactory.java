/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.metalua.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.koneki.ldt.metalua.Activator;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;

/**
 * Provides {@link LuaState} loaded with Metalua.
 * 
 * @author Kévin KIN-FOO <kkinfoo@anwyware-tech.com> {@linkplain http ://metalua.luaforge.net/manual000.html}
 */
public final class MetaluaStateFactory {

	private static String sourcePath = null;

	private MetaluaStateFactory() {
	}

	/**
	 * Provides a LuaState that can run Metalua code
	 * 
	 * Just gives a LuaState loaded with the Metalua library.
	 * 
	 * @return LuaState able to run Metalua code
	 * 
	 * @throws LuaException
	 *             the lua exception
	 * 
	 * @see {@link LuaState}
	 * @since 1.0
	 */
	public static LuaState newLuaState() {

		/*
		 * Create a regular LuaState, then enable it to run Metalua
		 */
		LuaState l = new LuaState();

		// Load default libraries, in order to modify PATH
		l.openLibs();

		// Update path in order to be able to load Metalua
		String metaluaPath = MetaluaStateFactory.sourcesPath();
		StringBuilder path = new StringBuilder();
		path.append(NLS.bind("package.path  = [[{0}?.luac;{0}?.lua]]", metaluaPath));//$NON-NLS-1$
		path.append(NLS.bind("package.mpath = [[{0}?.mlua]]", metaluaPath));//$NON-NLS-1$
		path.append("package.cpath = ''");//$NON-NLS-1$

		// Detect problems
		l.load(path.toString(), "pathLoading"); //$NON-NLS-1$
		l.call(0, 0);

		// State is ready
		return l;
	}

	public static String sourcesPath() {

		// Define source path at first call
		if (sourcePath == null) {

			/**
			 * Locate fragment root, it will be Metalua's include path
			 */

			// Retrieve parent bundle
			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);

			// Stop when fragment's root can't be located
			try {
				/*
				 * A folder called as below is available only from fragments, it contains Metalua files.
				 */
				final URL ressource = bundle.getResource("/lib"); //$NON-NLS-1$
				final String path = FileLocator.toFileURL(ressource).getPath();

				/*
				 * Remove folder name at the end of path in order to obtain fragment location on disk. It is the real Metalua path.
				 */
				sourcePath = new File(path).getPath() + File.separator;
			} catch (final IOException e) {
				Activator.logError("Unable to extract Metalua sources.", e); //$NON-NLS-1$
				return ""; //$NON-NLS-1$
			}
		}
		return sourcePath;
	}

}
