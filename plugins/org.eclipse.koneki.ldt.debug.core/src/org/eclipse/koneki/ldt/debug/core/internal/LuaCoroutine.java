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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.dltk.dbgp.breakpoints.IDbgpBreakpoint;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationEngine;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.internal.debug.core.model.IScriptStreamProxy;
import org.eclipse.dltk.internal.debug.core.model.ScriptThread;

/**
 * Represents a coroutine for Lua debugger. This differ from a regular script thread because it cannot be controlled, and the way to get info about it
 * is different (use non standard commands and switches).
 * 
 * FIXME: extends {@link ScriptThread} instead of implementing {@link IScriptThread} because of hard-coded casts in DLTK code. This causes a lot of
 * hacky code (duplicate stack, way to deal with DbgpSession, ...), in future versions of DLTY, try to see if there is a chance of stick only with
 * interfaces.
 */
public class LuaCoroutine extends ScriptThread {
	private LuaDebugTarget debugTarget;
	private String coroutineId;
	private String name;

	private LuaCoroutineStack stack;

	/**
	 * @param debugTarget
	 * @param frames
	 * @throws CoreException
	 * @throws DbgpException
	 */
	public LuaCoroutine(LuaDebugTarget debugTarget, String id, String name) throws DbgpException, CoreException {
		// we can't access to this in LuaCoroutineDbgpSession ctor, so the coroutine is bound on a second time
		super(debugTarget, new LuaCoroutineDbgpSession(debugTarget.getMainThread().getDbgpSession(), null), null);
		this.debugTarget = debugTarget;
		this.coroutineId = id;
		this.name = name;

		((LuaCoroutineDbgpSession) super.getDbgpSession()).bindToCoroutine(this);
		// the way to handle stack is a bit different here (lazy loading)
		this.stack = new LuaCoroutineStack(this);
	}

	public String getCoroutineId() {
		return this.coroutineId;
	}

	protected void coroNotSupported() throws DebugException {
		makeNotSupported("Not supported for a coroutine", null); //$NON-NLS-1$
	}

	// stack handling
	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptThread#hasStackFrames()
	 */
	@Override
	public boolean hasStackFrames() {
		return stack.hasFrames();
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptThread#getStackFrames()
	 */
	@Override
	public IStackFrame[] getStackFrames() throws DebugException {
		return stack.getFrames();
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptThread#updateStackFrames()
	 */
	@Override
	public void updateStackFrames() {
		stack.updateFrames();
	}

	// other methods
	/**
	 * @see org.eclipse.debug.core.model.IThread#getPriority()
	 */
	@Override
	public int getPriority() throws DebugException {
		return 0;
	}

	/**
	 * @see org.eclipse.debug.core.model.IThread#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	@Override
	public IBreakpoint[] getBreakpoints() {
		return DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(getModelIdentifier());
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	@Override
	public IDebugTarget getDebugTarget() {
		return debugTarget;
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	@Override
	public boolean canResume() {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	@Override
	public boolean canSuspend() {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	@Override
	public boolean isSuspended() {
		IScriptThread mainThread = debugTarget.getMainThread();
		return mainThread == null || mainThread.isSuspended();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	@Override
	public void resume() throws DebugException {
		coroNotSupported();
	}

	/**
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	@Override
	public void suspend() throws DebugException {
		coroNotSupported();
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	@Override
	public boolean canStepInto() {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	@Override
	public boolean canStepOver() {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	@Override
	public boolean canStepReturn() {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	@Override
	public boolean isStepping() {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	@Override
	public void stepInto() throws DebugException {
		coroNotSupported();
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	@Override
	public void stepOver() throws DebugException {
		coroNotSupported();
	}

	/**
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	@Override
	public void stepReturn() throws DebugException {
		coroNotSupported();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	@Override
	public boolean canTerminate() {
		IScriptThread mainThread = debugTarget.getMainThread();
		return mainThread != null && mainThread.canTerminate();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		IScriptThread mainThread = debugTarget.getMainThread();
		return mainThread == null || mainThread.isTerminated();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	@Override
	public void terminate() throws DebugException {
		IScriptThread mainThread = debugTarget.getMainThread();
		if (mainThread != null) {
			mainThread.terminate();
		}
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.IScriptThread#getDbgpBreakpoint(java.lang.String)
	 */
	@Override
	public IDbgpBreakpoint getDbgpBreakpoint(String id) {
		IScriptThread mainThread = debugTarget.getMainThread();
		return mainThread == null ? null : mainThread.getDbgpBreakpoint(id);
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.IScriptThread#getStreamProxy()
	 */
	@Override
	public IScriptStreamProxy getStreamProxy() {
		IScriptThread mainThread = debugTarget.getMainThread();
		return mainThread == null ? null : mainThread.getStreamProxy();
	}

	/**
	 * Returns {@link IScriptEvaluationEngine} from main thread
	 * 
	 * This method is tricky because we could technically evaluate snippets in coroutine context but DBGp protocol does not provide a way to evaluate
	 * in a particular stack level and all coroutines are displayed are paused (that is in a yield call) so the context is not really useful.
	 * 
	 * @see org.eclipse.dltk.debug.core.model.IScriptThread#getEvaluationEngine()
	 */
	@Override
	public IScriptEvaluationEngine getEvaluationEngine() {
		IScriptThread mainThread = debugTarget.getMainThread();
		return mainThread == null ? null : mainThread.getEvaluationEngine();
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.IScriptThread#getModificationsCount()
	 */
	@Override
	public int getModificationsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.IScriptThread#sendTerminationRequest()
	 */
	@Override
	public void sendTerminationRequest() throws DebugException {
		IScriptThread mainThread = debugTarget.getMainThread();
		if (mainThread != null) {
			debugTarget.getMainThread().sendTerminationRequest();
		}
	}

	// some methods does not require any processing for coroutines

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptThread#initialize(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initialize(IProgressMonitor monitor) throws DbgpException {
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptThread#objectTerminated(java.lang.Object, java.lang.Exception)
	 */
	@Override
	public void objectTerminated(Object object, Exception e) {
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptThread#handleTermination(org.eclipse.dltk.dbgp.exceptions.DbgpException)
	 */
	@Override
	public void handleTermination(DbgpException e) {
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptThread#handleResume(int)
	 */
	@Override
	public void handleResume(int detail) {
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptThread#handleSuspend(int)
	 */
	@Override
	public void handleSuspend(int detail) {
	}

}
