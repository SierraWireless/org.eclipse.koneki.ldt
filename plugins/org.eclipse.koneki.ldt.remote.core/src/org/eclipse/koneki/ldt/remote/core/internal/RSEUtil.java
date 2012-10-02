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
package org.eclipse.koneki.ldt.remote.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.internal.connectorservice.ssh.SshConnectorService;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.services.files.IFileService;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystem;

import com.jcraft.jsch.Session;

/**
 * Utility class, contains some helpers to use RSE
 */
@SuppressWarnings({ "restriction" })
public final class RSEUtil {
	private RSEUtil() {
	}

	/**
	 * Return the ssh connection session in this connector services
	 */
	public static Session getCurrentSshSession(IConnectorService[] connectors) {
		// TODO find a way to not use internal class (and so remove the rse.ui dependency)
		for (IConnectorService connectorService : connectors) {
			if (connectorService instanceof SshConnectorService) {
				SshConnectorService iConnectorService = (SshConnectorService) connectorService;
				return iConnectorService.getSession();
			}
		}
		return null;
	}

	/**
	 * stop the current thread until RSE is initialized or the thread is interrupted
	 */
	public static void waitForRSEInitialization() {
		try {
			RSECorePlugin.waitForInitCompletion();
			// CHECKSTYLE:OFF
		} catch (InterruptedException e) {
			// nothing to do ..
			// CHECKSTYLE:ON
		}
	}

	/**
	 * Gets the first found remote file SubSystem in the given host
	 * 
	 * @param host
	 * @return a remote file subsystem or null if none is found
	 */
	public static IRemoteFileSubSystem getRemoteFileSubsystem(IHost host) {
		ISubSystem[] subSystems = host.getSubSystems();
		for (ISubSystem subsystem : subSystems) {
			if (subsystem instanceof IRemoteFileSubSystem) {
				return ((IRemoteFileSubSystem) subsystem);
			}
		}
		return null;
	}

	/**
	 * workaround of BUG ECLIPSE TOOLSLINUX-86 349947, no more needed since tm 3.4
	 * 
	 * @return true if the file exist
	 */
	@Deprecated
	public static boolean fileExist(IRemoteFileSubSystem subsystem, IRemoteFile file) {
		try {
			IRemoteFile[] list = subsystem.list(file.getParentRemoteFile(), file.getName(), IFileService.FILE_TYPE_FILES_AND_FOLDERS,
					new NullProgressMonitor());
			return list.length == 1;
		} catch (SystemMessageException e) {
			return false;
		}
	}

	/**
	 * upload the content of the given folder at the given destination
	 * 
	 * @throws CoreException
	 */
	public static void uploadFiles(IRemoteFileSubSystem subsystem, IFolder sourceFolder, String destinationFolderPath, IProgressMonitor monitor)
			throws CoreException {
		IResource[] members = sourceFolder.members();
		SubMonitor subMonitor = SubMonitor.convert(monitor, members.length);
		if (members.length > 0) {
			for (int i = 0; i < members.length && !subMonitor.isCanceled(); i++) {
				try {
					IResource sourceMember = members[i];
					if (sourceMember instanceof IFile) {
						IFile sourceFile = ((IFile) sourceMember);
						String sourcePath = sourceFile.getLocation().toOSString();
						String sourceEncoding = sourceFile.getCharset();
						String destinationPath = destinationFolderPath + subsystem.getSeparator() + sourceFile.getName();
						String destinationEncoding = subsystem.getRemoteEncoding();
						subsystem.upload(sourcePath, sourceEncoding, destinationPath, destinationEncoding, subMonitor.newChild(3));
					} else if (sourceMember instanceof IFolder) {
						IFolder innerSourceFolder = (IFolder) sourceMember;
						String innerDestinationFolderPath = destinationFolderPath + subsystem.getSeparator() + innerSourceFolder.getName();
						IRemoteFile remoteFolder = subsystem.getRemoteFileObject(innerDestinationFolderPath, subMonitor.newChild(1));
						subsystem.createFolder(remoteFolder, subMonitor.newChild(1));
						uploadFiles(subsystem, innerSourceFolder, innerDestinationFolderPath, subMonitor.newChild(1));
					}
				} catch (SystemMessageException e) {
					throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to upload files", e)); //$NON-NLS-1$
				}
			}

		}
	}
}
