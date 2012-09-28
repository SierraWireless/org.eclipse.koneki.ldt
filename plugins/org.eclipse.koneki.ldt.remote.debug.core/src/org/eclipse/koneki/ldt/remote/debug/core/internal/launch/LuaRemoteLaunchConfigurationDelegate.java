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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.koneki.ldt.core.LuaConstants;
import org.eclipse.koneki.ldt.remote.core.internal.lua.LuaRSEUtil;
import org.eclipse.koneki.ldt.remote.core.internal.lua.LuaSubSystem;
import org.eclipse.koneki.ldt.remote.core.internal.lua.RSEUtil;
import org.eclipse.koneki.ldt.remote.debug.core.internal.Activator;
import org.eclipse.koneki.ldt.remote.debug.core.internal.LuaRemoteDebugConstant;
import org.eclipse.koneki.ldt.remote.debug.core.internal.sshprocess.SshProcess;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystem;

import com.jcraft.jsch.Session;

public class LuaRemoteLaunchConfigurationDelegate extends LaunchConfigurationDelegate {
	//private static final String[] DEBUG_FILES = { "/script/debugintrospection.lua", "script/debugger.lua" }; //$NON-NLS-1$ //$NON-NLS-2$

	//private static final String DEBGUGGER_MODULE = "debugger"; //$NON-NLS-1$

	/**
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String,
	 *      org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		SubMonitor submonitor = SubMonitor.convert(monitor, 11);
		try {
			// wait RSE is initialized
			// TODO not sure this is the good way to wait for init everywhere in the code
			RSEUtil.waitForRSEInitialization();

			// get configuration information
			String projectName = configuration.getAttribute(LuaRemoteDebugConstant.PROJECT_NAME, "");//$NON-NLS-1$
			IHost host = LuaRemoteLaunchConfigurationUtil.getHost(configuration);
			@SuppressWarnings("rawtypes")
			Map env = configuration.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, Collections.EMPTY_MAP);

			// valid configuration information
			String errorMessage = LuaRemoteLaunchConfigurationUtil.validateRemoteLaunchConfiguration(projectName, host);
			if (errorMessage != null)
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, errorMessage));
			submonitor.worked(1);

			// get Project
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

			// get the first found remote file SubSystem
			IRemoteFileSubSystem remoteFileSubSystem = RSEUtil.getRemoteFileSubsystem(host);

			// get the first found Lua SubSystem
			LuaSubSystem luaSubSystem = LuaRSEUtil.getLuaSubSystem(host);

			// try to connect to the target
			try {
				if (submonitor.isCanceled())
					return;
				remoteFileSubSystem.connect(submonitor.newChild(1), false);
				// CHECKSTYLE:OFF
			} catch (Exception e) {
				// CHECKSTYLE:ON
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						Messages.LuaRemoteLaunchConfigurationDelegate_error_connectionfailed, e));
			}

			// compute the remote project workingdir
			if (submonitor.isCanceled())
				return;
			String outputDirectory = luaSubSystem.getOutputDirectory();
			String remoteApplicationFolderPath = LuaRemoteLaunchConfigurationUtil.getRemoteApplicationPath(configuration);

			// kill Process if already running
			// could happen if connection is closed and last process launch is not terminate
			Session session = RSEUtil.getCurrentSshSession(host.getConnectorServices());
			SshProcess.killProcess(session, remoteApplicationFolderPath);

			// check an prepare remote folder
			try {
				if (submonitor.isCanceled())
					return;
				IRemoteFile remoteApplicationPath = remoteFileSubSystem.getRemoteFileObject(outputDirectory, submonitor.newChild(1));
				if (remoteApplicationPath.exists()) {
					if (remoteApplicationPath.isFile()) {
						throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, NLS.bind(
								Messages.LuaRemoteLaunchConfigurationDelegate_error_filealreadyexist, outputDirectory)));
					}
				} else {
					remoteFileSubSystem.createFolder(remoteApplicationPath, submonitor.newChild(1));
				}
				submonitor.setWorkRemaining(8);

				// remoteFile is a folder
				// create(or delete and recreate) the working directory
				if (submonitor.isCanceled())
					return;
				IRemoteFile remoteWorkingFolder = remoteFileSubSystem.getRemoteFileObject(remoteApplicationFolderPath, submonitor.newChild(1));
				if (remoteWorkingFolder.exists()) {
					if (remoteWorkingFolder.isFile()) {
						throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
								Messages.LuaRemoteLaunchConfigurationDelegate_error_filealreadyexist, remoteApplicationFolderPath)));
					} else {
						remoteFileSubSystem.delete(remoteWorkingFolder, submonitor.newChild(1));
					}
				}
				submonitor.setWorkRemaining(6);

				// create project application
				if (submonitor.isCanceled())
					return;
				remoteFileSubSystem.createFolder(remoteWorkingFolder, submonitor.newChild(1));

				// upload sourcecode
				IScriptProject scriptProject = DLTKCore.create(project);
				LuaRSEUtil.uploadFiles(remoteFileSubSystem, scriptProject, remoteApplicationFolderPath, submonitor.newChild(3));

				// upload Debug module
				// TODO support debug
				// SubMonitor debugmonitor = submonitor.newChild(1);
				// debugmonitor.setWorkRemaining(DEBUG_FILES.length);
				// if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				// String localEncoding = Charset.defaultCharset().name();
				// String remoteEncoding = remoteFileSubSystem.getRemoteEncoding();
				// for (String luaFile : DEBUG_FILES) {
				// try {
				// Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
				// URL resource = bundle.getResource(luaFile);
				// File result = new File(FileLocator.toFileURL(resource).getPath());
				// String remotePath = remoteApplicationFolderPath + remoteFileSubSystem.getSeparator() + result.getName();
				// remoteFileSubSystem.upload(result.getAbsolutePath(), localEncoding, remotePath, remoteEncoding, submonitor.newChild(1));
				// } catch (IOException e) {
				//							throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Una", e)); //$NON-NLS-1$
				// }
				// }
				// }
			} catch (SystemMessageException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, NLS.bind(
						Messages.LuaRemoteLaunchConfigurationDelegate_error_unabletoaccestoremoteapplicationdir, outputDirectory), e));
			}

			// set environment var
			Map<String, String> envVars = new HashMap<String, String>();
			// add default lua envvar
			String luaPath = luaSubSystem.getLuaPath();
			if (luaPath != null && !luaPath.isEmpty())
				envVars.put("LUA_PATH", luaPath); //$NON-NLS-1$
			String luaCPath = luaSubSystem.getCLuaPath();
			if (luaCPath != null && !luaCPath.isEmpty())
				envVars.put("LUA_CPATH", luaCPath); //$NON-NLS-1$
			String ldLibraryPath = luaSubSystem.getLDLibraryPath();
			if (ldLibraryPath != null && !ldLibraryPath.isEmpty())
				envVars.put("LD_LIBRARY_PATH", ldLibraryPath); //$NON-NLS-1$

			// TODO support debug
			// if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			// String sessionID = DbgpSessionIdGenerator.generate();
			// // dbgp env vars
			// envVars.put(LuaRemoteDebugConstant.ENV_VAR_KEY_DBGP_IDE_KEY, sessionID);
			// envVars.put(LuaRemoteDebugConstant.ENV_VAR_KEY_DBGP_IDE_PORT, String.valueOf(DLTKDebugPlugin.getDefault().getDbgpService().getPort()));
			// }
			for (Object oEntry : env.entrySet()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Entry) oEntry;
				envVars.put(entry.getKey().toString(), entry.getValue().toString());
			}

			// create lua execution command
			// TODO get value from launchconfiguration
			String mainRelativePath = LuaConstants.DEFAULT_MAIN_FILE;
			List<String> cmd = new ArrayList<String>(6);
			// FIXME is there a cleaner way to control buffering ?
			// see: http://lua-users.org/lists/lua-l/2011-05/msg00549.html
			String bootstrapCode = "io.stdout:setvbuf(\"line\");"; //$NON-NLS-1$

			// TODO Retreive properties from target to create the ssh process
			cmd.add(luaSubSystem.getLuaCommand());
			// TODO support debug
			// if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			// // load debugging libraries. The -l parameter cannot be used here because the debugger MUST be the first module to be loaded
			//				bootstrapCode += " require(\"" + DEBGUGGER_MODULE + "\")();"; //$NON-NLS-1$//$NON-NLS-2$
			// }
			cmd.add("-e"); //$NON-NLS-1$
			cmd.add(bootstrapCode);
			cmd.add(mainRelativePath);

			// Create Process
			if (submonitor.isCanceled())
				return;

			SshProcess process = new SshProcess(session, launch, remoteApplicationFolderPath, cmd.toArray(new String[cmd.size()]), envVars);

			// TODO support debug
			if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				// // Desactivate DBGP Stream redirection
				// // TODO manage DBGP Stream redirection (so desactivate process redirection in debug mode)
				//				launch.setAttribute(DLTKDebugLaunchConstants.ATTR_DEBUG_CONSOLE, "false"); //$NON-NLS-1$
				// LuaRemoteDebuggingEngineRunner debugingEngine = new LuaRemoteDebuggingEngineRunner(process, sessionID,
				// remoteApplicationFolderPath);
				// debugingEngine.run(new InterpreterConfig(), launch, new NullProgressMonitor());
				// launch.addProcess(process);
			} else {
				process.start();
				launch.addProcess(process);
			}
		} finally {
			submonitor.done();
		}
	}
}
