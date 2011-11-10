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
package org.eclipse.koneki.ldt.debug.core.remote;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.debug.core.model.ScriptStackFrame;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.launching.sourcelookup.DBGPSourceModule;
import org.eclipse.koneki.ldt.debug.core.UnreachableStackFrame;

@SuppressWarnings("restriction")
public class LuaRemoteSourceLookupDirector extends AbstractSourceLookupDirector {

	private static class LuaSourceLookupParticipant extends AbstractSourceLookupParticipant {
		/**
		 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant#getSourceName(java.lang.Object)
		 */
		@Override
		public String getSourceName(Object object) throws CoreException {
			URI uri = null;
			if (object instanceof URI) {
				uri = (URI) object;
			} else if (object instanceof ScriptStackFrame) {
				uri = ((ScriptStackFrame) object).getSourceURI();
			} else {
				return null;
			}

			String path = uri.getPath();
			ILaunchConfiguration configuration = getDirector().getLaunchConfiguration();
			String remotePrefix = configuration.getAttribute(ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_REMOTE_WORKING_DIR, ""); //$NON-NLS-1$

			return path.startsWith(remotePrefix) ? path.substring(remotePrefix.length() + 1) : null;
		}
	}

	/**
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupDirector#initializeParticipants()
	 */
	@Override
	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[] { new LuaSourceLookupParticipant() });
	}

	/**
	 * Heavily based on RemoteScriptSourceLookupDirector#getSourceElement(Object) but adds {@link IStorage} support and checks that URI actually
	 * contains something (case of unreachable stack levels)
	 * 
	 * @see org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector#getSourceElement(java.lang.Object)
	 * @see org.eclipse.dltk.launching.sourcelookup.RemoteScriptSourceLookupDirector#getSourceElement(Object)
	 */
	@Override
	public Object getSourceElement(Object element) {
		// source element was found inside the project
		Object o = super.getSourceElement(element);
		if (o instanceof IFile || o instanceof IStorage) {
			return o;
		} else if (!(element instanceof ScriptStackFrame)) {
			return null;
		}

		// time to ask for it remotely
		ScriptStackFrame frame = (ScriptStackFrame) element;

		UnreachableStackFrame unreachableStackFrame = UnreachableStackFrame.checkReachable(frame);
		if (unreachableStackFrame != null) {
			return unreachableStackFrame;
		}

		URI uri = frame.getSourceURI();
		String path = uri.getPath();
		IProject project = LaunchConfigurationUtils.getProject(getLaunchConfiguration());
		if (project == null) {
			return null;
		}
		IScriptProject scriptProject = DLTKCore.create(project);

		/*
		 * XXX: this should probably use some kind of IStorable implementation instead of directly relying on the stack frame - that allows for re-use
		 * of the ExternalStorageEditorInput object
		 */
		return new DBGPSourceModule((ScriptProject) scriptProject, path, DefaultWorkingCopyOwner.PRIMARY, frame);
	}
}
