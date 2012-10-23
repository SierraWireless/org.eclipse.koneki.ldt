/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.support.lua51.internal.interpreter;

import java.io.IOException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.LibraryLocation;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.debug.core.IEmbeddedInterpreterInstallType;
import org.eclipse.koneki.ldt.support.lua51.internal.Activator;

public class JNLua51InterpreterInstallType extends AbstractInterpreterInstallType implements IEmbeddedInterpreterInstallType {

	@Override
	public String getName() {
		return "JNLua 5.1"; //$NON-NLS-1$
	}

	@Override
	public String getNatureId() {
		return LuaNature.ID;
	}

	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new JNLua51InterpreterInstall(this, id);
	}

	@Override
	protected String getPluginId() {
		return Activator.PLUGIN_ID;
	}

	@Override
	protected ILog getLog() {
		return Activator.getDefault().getLog();
	}

	@Override
	protected String[] getPossibleInterpreterNames() {
		// TODO verify if it's useful
		return null;
	}

	@Override
	public synchronized LibraryLocation[] getDefaultLibraryLocations(final IFileHandle installLocation, EnvironmentVariable[] variables,
			IProgressMonitor monitor) {
		return new LibraryLocation[0];
	}

	@Override
	protected IPath createPathFile(IDeployment deployment) throws IOException {
		// TODO verify if it's useful
		return null;
	}

	@Override
	public IStatus validateInstallLocation(IFileHandle installLocation) {
		// No validation on install location as interpreter is embedded.
		return Status.OK_STATUS;
	}

	@Override
	public IStatus validateInstallLocation(IFileHandle installLocation, EnvironmentVariable[] variables, LibraryLocation[] libraryLocations,
			IProgressMonitor monitor) {
		// No validation on install location as interpreter is embedded.
		return Status.OK_STATUS;
	}

	@Override
	public IStatus validatePossiblyName(IFileHandle installLocation) {
		// No validation
		return Status.OK_STATUS;
	}
}
