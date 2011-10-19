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

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-06-15 17:55:03 +0200 (lun., 15 juin 2009) $
 * $Author: kkinfoo $
 * $Id: MetaluaStateFactory.java 1841 2009-06-15 15:55:03Z kkinfoo $
 */
package org.eclipse.koneki.ldt.metalua;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;

// TODO: Auto-generated Javadoc
/**
 * Provides {@link LuaState} loaded with Metalua.
 * 
 * @author Kévin KIN-FOO <kkinfoo@anwyware-tech.com> {@linkplain http
 *         ://metalua.luaforge.net/manual000.html}
 */
class MetaluaStateFactory {

	private static String sourcePath = null;

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
	public static LuaState newLuaState() throws LuaException {

		/*
		 * Create a regular LuaState, then enable it to run Metalua
		 */
		LuaState l = new LuaState();

		// Load default libraries, in order to modify PATH
		l.openLibs();

		// Update path in order to be able to load Metalua
		String metaluaPath = MetaluaStateFactory.sourcesPath();
		String path = "package.path = package.path  .. [[;" + metaluaPath
				+ "?.luac;" + metaluaPath + "?.lua]]";

		// Load Metalua's byte code
		String require = "require 'metalua.compiler'";

		// Detect problems
		l.load(path, "pathLoading");
		l.call(0, 0);
		l.load(require, "requireContentFromPath");
		l.call(0, 0);
//		switch (l.LdoString(path) + l.LdoString(require)) {
//		default:
//			Metalua.raise(l);
//		case 0:
//		}

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
				 * A folder called as below is available only from fragments, it
				 * contains Metalua files.
				 */
				String folder = "metalua";

				// Locate it on disk
				URL ressource = bundle.getResource("/" + folder);
				String _sourcePath = FileLocator.toFileURL(ressource).getPath();

				/*
				 * Remove folder name at the end of path in order to obtain
				 * fragment location on disk. It is the real Metalua path.
				 */
				_sourcePath = new File(_sourcePath).getParent() + File.separator;
				sourcePath = _sourcePath;
			} catch (IOException e) {
				return new String();
			}
		}
		return sourcePath;
	}

}
