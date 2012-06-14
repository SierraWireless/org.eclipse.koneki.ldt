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
package org.eclipse.koneki.ldt.debug.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.debug.ui.breakpoints.ScriptToggleBreakpointAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

public class LuaToggleBreakpointAdapter extends ScriptToggleBreakpointAdapter {

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension#toggleBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void toggleBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleLineBreakpoints(part, selection);
	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension#canToggleBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public boolean canToggleBreakpoints(IWorkbenchPart part, ISelection selection) {
		return true;
	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		// not allowed

	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleWatchpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		// not allowed

	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleWatchpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	/**
	 * @see org.eclipse.dltk.debug.ui.breakpoints.ScriptToggleBreakpointAdapter#getDebugModelId()
	 */
	@Override
	protected String getDebugModelId() {
		return "org.eclipse.koneki.ldt.nature.debugModel"; //$NON-NLS-1$
	}

}
