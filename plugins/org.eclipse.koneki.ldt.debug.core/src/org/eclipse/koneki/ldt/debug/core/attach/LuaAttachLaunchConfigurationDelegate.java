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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.launching.AbstractRemoteLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.RemoteDebuggingEngineRunner;
import org.eclipse.koneki.ldt.core.LuaLanguageToolkit;

public class LuaAttachLaunchConfigurationDelegate extends AbstractRemoteLaunchConfigurationDelegate {

	public LuaAttachLaunchConfigurationDelegate() {
	}

	@Override
	public IInterpreterInstall verifyInterpreterInstall(ILaunchConfiguration configuration) throws CoreException {
		// we don't need interpreter install to do attach debug
		return null;
	}

	@Override
	protected RemoteDebuggingEngineRunner getDebuggingRunner(IInterpreterInstall install) {
		return new LuaAttachDebuggingEngineRunner();
	}

	@Override
	public String getLanguageId() {
		return LuaLanguageToolkit.getDefault().getNatureId();
	}

}
