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
package org.eclipse.koneki.ldt.debug.core.internal;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.debug.core.model.ScriptStackFrame;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;
import org.eclipse.dltk.launching.sourcelookup.DBGPSourceModule;

@SuppressWarnings("restriction")
public class LuaSourceLookupDirector extends AbstractSourceLookupDirector {

	private static class LuaSourceLookupParticipant extends AbstractSourceLookupParticipant {
		/**
		 * Extract the source name from the selected debug model element (stackframe, thread ...). This source name will be used by SourcePathComputer
		 * to retrieve the SourceElement.
		 * 
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
			return uri.toString();
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
		// if the element is an unreachable stack frame we don't need to search trough the source path computer.
		if (element instanceof IScriptStackFrame) {
			IScriptStackFrame frame = (ScriptStackFrame) element;
			UnreachableStackFrame unreachableStackFrame = UnreachableStackFrame.checkReachable(frame);
			if (unreachableStackFrame != null) {
				return unreachableStackFrame;
			}
		}

		// search in all container of the source path computer.
		Object o = super.getSourceElement(element);

		// a file or a IStorage was found, we return it, we can display it.
		if (o instanceof IFile || o instanceof IStorage) {
			return o;
		}

		// at this time, if we still have a ScriptStackFrame
		// we could have a fallback and create a DBGPSourceModule
		// (the source code will be return by the DBGP client via the command "source"
		if (element instanceof ScriptStackFrame) {
			ScriptStackFrame frame = (ScriptStackFrame) element;

			URI uri = frame.getSourceURI();
			String path = uri.getPath();
			IProject project = LaunchConfigurationUtils.getProject(getLaunchConfiguration());
			if (project == null) {
				return null;
			}
			IScriptProject scriptProject = DLTKCore.create(project);

			/*
			 * XXX: this should probably use some kind of IStorable implementation instead of directly relying on the stack frame - that allows for
			 * re-use of the ExternalStorageEditorInput object
			 */
			return new DBGPSourceModule((ScriptProject) scriptProject, path, DefaultWorkingCopyOwner.PRIMARY, frame);
		}

		// we not managed the other case, so return null
		return null;
	}
}