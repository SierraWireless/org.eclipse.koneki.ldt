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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.dltk.dbgp.DbgpBaseCommands;
import org.eclipse.dltk.dbgp.DbgpRequest;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.IDbgpService;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LuaDebugTarget extends ScriptDebugTarget {
	private enum ChangeType {
		ADD, UPDATE, REMOVE;
	}

	private List<LuaCoroutine> coroutines = new ArrayList<LuaCoroutine>();

	public LuaDebugTarget(String modelId, IDbgpService dbgpService, String sessionId, ILaunch launch, IProcess process) {
		super(modelId, dbgpService, sessionId, launch, process);
		DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {

			protected List<LuaCoroutine> parseCoroutineList(Element response) throws DbgpException, CoreException {
				NodeList xmlNodes = response.getElementsByTagName("coroutine"); //$NON-NLS-1$
				// TODO recycle LuaCoroutine instances to avoid flickering (may also require to modify stack)
				List<LuaCoroutine> coroList = new ArrayList<LuaCoroutine>(xmlNodes.getLength());
				for (int i = 0; i < xmlNodes.getLength(); i++) {
					Element coro = (Element) xmlNodes.item(i);
					if (coro.getAttribute("running").equals("0")) { //$NON-NLS-1$ //$NON-NLS-2$
						coroList.add(new LuaCoroutine(LuaDebugTarget.this, coro.getAttribute("id"), coro.getAttribute("name"))); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				return coroList;
			}

			@Override
			public void handleDebugEvents(DebugEvent[] events) {
				for (DebugEvent event : events) {
					if (event.getKind() == DebugEvent.SUSPEND) {
						IScriptThread thread = (IScriptThread) event.getSource();
						DbgpRequest listRequest = DbgpBaseCommands.createRequest("coroutine_list"); //$NON-NLS-1$
						try {
							Element response = thread.getDbgpSession().getCommunicator().communicate(listRequest);
							LuaDebugTarget.this.coroutines = parseCoroutineList(response);
						} catch (DbgpException e) {
							Activator.logError(Messages.LuaDebugTarget_error_coro_list, e);
							LuaDebugTarget.this.coroutines.clear();
						} catch (CoreException e) {
							Activator.logError(Messages.LuaDebugTarget_error_coro_list, e);
							LuaDebugTarget.this.coroutines.clear();
						}
					} else if (event.getKind() == DebugEvent.TERMINATE) {
						DebugPlugin.getDefault().removeDebugEventListener(this);
					}
				}
			}
		});

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

	/**
	 * Returns the "main" thread of the instance. This is the only thread which is really mapped to a debug socket, others are just coroutines.
	 * 
	 * @return Main thread if any, null otherwise.
	 */
	public IScriptThread getMainThread() {
		IThread[] threads = super.getThreads();
		return threads.length > 0 ? (IScriptThread) threads[0] : null;
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget#getThreads()
	 */
	@Override
	public IThread[] getThreads() {
		// TODO: is rebuild thread list each call is expansive ?
		List<IThread> threads = new ArrayList<IThread>();
		threads.addAll(Arrays.asList(super.getThreads()));
		// coroutines are shown only when main thread is suspended (while thread is running, it is pointless to show them)
		if (threads.size() > 0 && threads.get(0).isSuspended()) {
			threads.addAll(coroutines);
		}
		return threads.toArray(new IThread[threads.size()]);
	}
}