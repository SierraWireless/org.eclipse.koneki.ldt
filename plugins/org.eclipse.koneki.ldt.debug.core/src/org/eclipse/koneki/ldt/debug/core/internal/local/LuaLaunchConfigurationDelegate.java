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
package org.eclipse.koneki.ldt.debug.core.internal.local;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.koneki.ldt.core.LuaNature;

public class LuaLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegate {

	@Override
	public String getLanguageId() {
		return LuaNature.ID;
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		super.launch(configuration, mode, launch, monitor);
	}

	@Override
	protected void validateLaunchConfiguration(ILaunchConfiguration configuration, String mode, IProject project) throws CoreException {
		// TODO reactivate validation ?
	}

	@Override
	public IInterpreterInstall verifyInterpreterInstall(ILaunchConfiguration configuration) throws CoreException {
		// TODO reactivate verification
		return getInterpreterInstall(configuration);
	}
}
