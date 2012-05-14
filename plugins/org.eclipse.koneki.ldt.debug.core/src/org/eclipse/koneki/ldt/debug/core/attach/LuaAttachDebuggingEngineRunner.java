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
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.debug.core.IDbgpService;
import org.eclipse.dltk.debug.core.ScriptDebugManager;
import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.launching.RemoteDebuggingEngineRunner;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.debug.core.Activator;

/**
 * Debugging Engine Runner for lua in attach mode
 */
public class LuaAttachDebuggingEngineRunner extends RemoteDebuggingEngineRunner {

	private String remoteFolder;

	/**
	 * @param process
	 * @param install
	 */
	public LuaAttachDebuggingEngineRunner() {
		super(null);
	}

	/**
	 * @see org.eclipse.dltk.launching.DebuggingEngineRunner#getDebugPreferenceQualifier()
	 */
	@Override
	protected String getDebugPreferenceQualifier() {
		return Activator.PLUGIN_ID;
	}

	/**
	 * @see org.eclipse.dltk.launching.DebuggingEngineRunner#getDebugModelId()
	 */
	@Override
	public String getDebugModelId() {
		return ScriptDebugManager.getInstance().getDebugModelByNature(LuaNature.ID);
	}

	@Override
	protected IScriptDebugTarget createDebugTarget(ILaunch launch, IDbgpService dbgpService) throws CoreException {
		remoteFolder = getRemoteFolder(launch.getLaunchConfiguration());
		return new LuaAttachDebugTarget(getDebugModelId(), dbgpService, getSessionId(launch.getLaunchConfiguration()), launch, null) {
			@Override
			protected String folder() {
				return remoteFolder;
			}
		};
	}

	public String getRemoteFolder(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_REMOTE_WORKING_DIR, ""); //$NON-NLS-1$
	}
}
