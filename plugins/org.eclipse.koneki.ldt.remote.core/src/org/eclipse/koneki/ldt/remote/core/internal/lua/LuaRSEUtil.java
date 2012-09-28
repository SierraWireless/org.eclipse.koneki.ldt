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
package org.eclipse.koneki.ldt.remote.core.internal.lua;

import java.util.EnumSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.koneki.ldt.core.IProjectSourceVisitor;
import org.eclipse.koneki.ldt.core.LuaUtils;
import org.eclipse.koneki.ldt.remote.core.internal.Activator;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystem;

/**
 * Utility class, contains some helpers to use RSE
 */
public final class LuaRSEUtil {
	private LuaRSEUtil() {
	}

	/**
	 * upload of the given script project and all dependencies (define in buildpath) except for archive
	 * 
	 * @throws CoreException
	 */
	public static void uploadFiles(final IRemoteFileSubSystem subsystem, final IScriptProject project, final String destinationFolderPath,
			final IProgressMonitor monitor) throws CoreException {
		final IProjectSourceVisitor visitor = new IProjectSourceVisitor() {

			@Override
			public void processFile(IPath absolutePath, IPath relativePath, String charset, IProgressMonitor monitor) throws CoreException {
				final String destinationPath = destinationFolderPath + subsystem.getSeparator() + relativePath.toPortableString();
				final String destinationEncoding = subsystem.getRemoteEncoding();
				final SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
				try {
					// Create file directory
					subsystem.upload(absolutePath.toOSString(), charset, destinationPath, destinationEncoding, subMonitor.newChild(1));
				} catch (final SystemMessageException e) {
					throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to upload files", e)); //$NON-NLS-1$
				}
			}

			@Override
			public void processDirectory(IPath absolutePath, IPath relativePath, IProgressMonitor monitor) throws CoreException {
				final String innerDestinationFolderPath = destinationFolderPath + subsystem.getSeparator() + relativePath.toPortableString();
				final SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
				try {
					// Create remote directory
					final IRemoteFile remoteFolder = subsystem.getRemoteFileObject(innerDestinationFolderPath, subMonitor);
					subsystem.createFolder(remoteFolder, subMonitor.newChild(1));
				} catch (final SystemMessageException e) {
					throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to upload files", e)); //$NON-NLS-1$
				}

			}

		};

		LuaUtils.visitSourceFiles(project,
				EnumSet.complementOf(EnumSet.of(LuaUtils.ProjectFragmentFilter.ARCHIVE, LuaUtils.ProjectFragmentFilter.EXECUTION_ENVIRONMENT)),
				visitor, monitor);
	}

	public static LuaSubSystem getLuaSubSystem(IHost host) {
		for (ISubSystem subsytem : host.getSubSystems()) {
			if (subsytem instanceof LuaSubSystem) {
				return (LuaSubSystem) subsytem;
			}
		}
		return null;
	}
}
