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
package org.eclipse.koneki.ldt.remote.debug.core.internal.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.debug.core.IDbgpService;
import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget;
import org.eclipse.dltk.launching.DebugSessionAcceptor;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.koneki.ldt.debug.core.internal.attach.LuaAttachDebuggingEngineRunner;
import org.eclipse.koneki.ldt.remote.debug.core.internal.sshprocess.SshProcess;

/**
 * Debuging Engine Runner for lua embedded project
 */
// TODO not sure we will extend RemoteDebuggingEngineRunner
// it will be better to extends DebuggingEngineRunner or create our own class
public class LuaRemoteDebuggingEngineRunner extends LuaAttachDebuggingEngineRunner {

	private SshProcess process;
	private String sessionId;
	private String remoteFolder;

	/**
	 * @param process
	 * @param install
	 */
	public LuaRemoteDebuggingEngineRunner(SshProcess process, String sessionId, String remoteFolder) {
		super();
		this.process = process;
		this.sessionId = sessionId;
		this.remoteFolder = remoteFolder;
	}

	/**
	 * @see org.eclipse.dltk.launching.RemoteDebuggingEngineRunner#run(org.eclipse.dltk.launching.InterpreterConfig, org.eclipse.debug.core.ILaunch,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void run(InterpreterConfig config, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		try {
			initializeLaunch(launch, config, createPreferencesLookupDelegate(launch));
			final ScriptDebugTarget target = (ScriptDebugTarget) launch.getDebugTarget();

			DebugSessionAcceptor debugSessionAcceptor = new DebugSessionAcceptor(target, monitor);
			startProcess();
			waitDebuggerConnected(launch, debugSessionAcceptor);
		} catch (CoreException e) {
			launch.terminate();
			throw e;
		}
	}

	protected void startProcess() throws CoreException {
		process.start();
	}

	@Override
	protected IScriptDebugTarget createDebugTarget(ILaunch launch, IDbgpService dbgpService) throws CoreException {
		return new LuaRemoteDebugTarget(getDebugModelId(), dbgpService, sessionId, launch, process) {
			@Override
			protected String folder() {
				return remoteFolder;
			}
		};
	}
}
