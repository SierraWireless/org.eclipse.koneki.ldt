/*******************************************************************************
 * Copyright (c) 2013 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.koneki.ldt.debug.core.internal.Activator;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.generic.LuaGenericDebuggingEngineConfigurer;

public class JNLuaDebuggingEngineConfigurer extends LuaGenericDebuggingEngineConfigurer {

	private static final String SCRIPT_LOCATION = "script/internal"; //$NON-NLS-1$

	@Override
	protected List<IPath> getLuaPath(ILaunch launch, InterpreterConfig config) throws CoreException {
		List<IPath> luaPath = super.getLuaPath(launch, config);

		// add transport layer module path to lua path
		try {
			URL debuggerEntry = Activator.getDefault().getBundle().getEntry(SCRIPT_LOCATION);
			File debuggerFolder = new File(FileLocator.toFileURL(debuggerEntry).getFile());
			luaPath.add(new Path(debuggerFolder.getPath()));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to extract debugger files.")); //$NON-NLS-1$
		}

		return luaPath;
	}

	@Override
	protected String getTransportLayer() {
		return "debugger.transport.java"; //$NON-NLS-1$
	}
}
