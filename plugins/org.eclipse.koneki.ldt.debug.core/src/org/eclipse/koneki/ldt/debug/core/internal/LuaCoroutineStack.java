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

import org.eclipse.dltk.dbgp.IDbgpStackLevel;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.model.IScriptStack;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.internal.debug.core.model.ScriptStackFrame;
import org.eclipse.dltk.internal.debug.core.model.ScriptThread;

/**
 * Stack handling for coroutine threads
 * 
 * It behaves differently of regular ScriptThread because it does not handle smart frame refresh and does lazy loading of stack frames.
 */
public class LuaCoroutineStack implements IScriptStack {
	private IScriptStackFrame[] frames;
	private final Object framesLock = new Object();

	private final LuaCoroutine thread;

	public LuaCoroutineStack(LuaCoroutine thread) {
		this.thread = thread;
		this.frames = null;
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.ScriptStack#requrestStackLevels()
	 */
	protected IDbgpStackLevel[] requestStackLevels() throws DbgpException {
		return thread.getDbgpSession().getCoreCommands().getStackLevels();
	}

	protected void readFrames() throws DbgpException {
		final IDbgpStackLevel[] levels = requestStackLevels();
		synchronized (framesLock) {
			final IScriptStackFrame[] newFrames = new IScriptStackFrame[levels.length];
			for (int i = 0; i < newFrames.length; ++i) {
				newFrames[i] = new ScriptStackFrame(this, levels[i]);
			}
			frames = newFrames;
		}
	}

	/**
	 * @see #getIScriptThread()
	 * @see org.eclipse.dltk.debug.core.model.IScriptStack#getThread()
	 */
	public ScriptThread getThread() {
		return thread;
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.IScriptStack#size()
	 */
	public int size() {
		synchronized (framesLock) {
			return getFrames().length;
		}
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.IScriptStack#hasFrames()
	 */
	public boolean hasFrames() {
		synchronized (framesLock) {
			return frames == null ? true : frames.length > 0;
		}
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.IScriptStack#getFrames()
	 */
	public IScriptStackFrame[] getFrames() {
		synchronized (framesLock) {
			if (frames == null) {
				try {
					readFrames();
				} catch (DbgpException e) {
					Activator.logError("Cannot read stack frames", e); //$NON-NLS-1$
				}
			}
			return frames;
		}
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.IScriptStack#getTopFrame()
	 */
	public IScriptStackFrame getTopFrame() {
		synchronized (framesLock) {
			return getFrames()[0];
		}
	}

	public void updateFrames() {
		synchronized (framesLock) {
			for (int i = 0; i < frames.length; i++) {
				((ScriptStackFrame) frames[i]).updateVariables();
			}
		}
	}

	public boolean isInitialized() {
		synchronized (framesLock) {
			return frames != null;
		}
	}
}
