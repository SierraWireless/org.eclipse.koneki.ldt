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
package org.eclipse.koneki.ldt.debug.core;

import java.net.URI;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.debug.core.IDbgpService;
import org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget;

public class LuaDebugTarget extends ScriptDebugTarget {
	private enum ChangeType {
		ADD, UPDATE, REMOVE;
	}

	public LuaDebugTarget(String modelId, IDbgpService dbgpService, String sessionId, ILaunch launch, IProcess process) {
		super(modelId, dbgpService, sessionId, launch, process);

	}

	/**
	 * Just a job performing operations on {@link IBreakpoint}.
	 */
	final class ChangeJob extends Job {
		private ChangeType changeType;
		private IBreakpoint bp;
		private IMarkerDelta markerDelta;

		public ChangeJob(String name, IBreakpoint breakpoint, IMarkerDelta delta, ChangeType type) {
			super(name);
			bp = breakpoint;
			markerDelta = delta;
			changeType = type;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			breakpoint(changeType, bp, markerDelta);
			return Status.OK_STATUS;
		}

	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget#runToLine(java.net.URI, int)
	 */
	// TODO BUG_ECLIPSE TOOLSLINUX-101 355616
	@Override
	public void runToLine(URI uri, final int lineNumber) throws DebugException {
		URI remoteUri = getPathMapper().map(uri);
		super.runToLine(remoteUri, lineNumber);
	}

	private void breakpoint(final ChangeType changeType, final IBreakpoint bp, final IMarkerDelta markerDelta) {
		switch (changeType) {
		case ADD:
			super.breakpointAdded(bp);
			break;
		case UPDATE:
			super.breakpointChanged(bp, markerDelta);
			break;
		case REMOVE:
			super.breakpointRemoved(bp, markerDelta);
			break;
		default:
			break;
		}
	}

	/**
	 * Perform breakpoint update in a separate Job
	 * 
	 * @see ScriptDebugTarget#breakpointChanged(IBreakpoint, IMarkerDelta)
	 */
	// TODO BUG_ECLIPSE 360003
	@Override
	public void breakpointChanged(final IBreakpoint breakpoint, final IMarkerDelta delta) {
		final ChangeJob job = new ChangeJob(Messages.LuaDebugTargetUpdate, breakpoint, delta, ChangeType.UPDATE);
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * Perform breakpoint add in a separate Job
	 * 
	 * @see ScriptDebugTarget#breakpointAdded(IBreakpoint)
	 */
	// TODO BUG_ECLIPSE 360003
	@Override
	public void breakpointAdded(final IBreakpoint breakpoint) {
		final ChangeJob job = new ChangeJob(Messages.LuaDebugTargetAdd, breakpoint, null, ChangeType.ADD);
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * Perform breakpoint removal in a separate Job
	 * 
	 * @see ScriptDebugTarget#breakpointRemoved(IBreakpoint, IMarkerDelta)
	 */
	// TODO BUG_ECLIPSE 360003
	@Override
	public void breakpointRemoved(final IBreakpoint breakpoint, final IMarkerDelta delta) {
		final ChangeJob job = new ChangeJob(Messages.LuaDebugTargetRemove, breakpoint, delta, ChangeType.REMOVE);
		job.setSystem(true);
		job.schedule();
	}
}
