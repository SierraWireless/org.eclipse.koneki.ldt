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

import org.eclipse.dltk.debug.core.model.IScriptStackFrame;

/**
 * Returned by {@link LuaSourceLookupDirector#getSourceElement(Object)} to identify stack frames that are unreachable for some reason.
 */
public class UnreachableStackFrame {
	public static final String TAIL_RETURN_SCHEME = "tailreturn"; //$NON-NLS-1$
	public static final String CCODE_SCHEME = "ccode"; //$NON-NLS-1$
	public static final String UNKNOWN_SCHEME = "unknown"; //$NON-NLS-1$

	private final IScriptStackFrame frame;
	private final String reason;

	public UnreachableStackFrame(IScriptStackFrame frame, String reason) {
		this.frame = frame;
		this.reason = reason;
	}

	public IScriptStackFrame getFrame() {
		return frame;
	}

	/**
	 * @return one of *_SCHEME constants explaining why the stack frame is not reachable.
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param frame
	 *            Stack frame to parse
	 * @return An UnreachableStackFrame instance if stack frame is unreachable, null if it is reachable.
	 */
	public static UnreachableStackFrame checkReachable(IScriptStackFrame frame) {
		final String scheme = frame.getSourceURI().getScheme();
		if (scheme.equals(TAIL_RETURN_SCHEME)) {
			return new UnreachableStackFrame(frame, TAIL_RETURN_SCHEME);
		} else if (scheme.equals(CCODE_SCHEME)) {
			return new UnreachableStackFrame(frame, CCODE_SCHEME);
		} else if (scheme.equals(UNKNOWN_SCHEME)) {
			return new UnreachableStackFrame(frame, UNKNOWN_SCHEME);
		}
		return null;
	}
}