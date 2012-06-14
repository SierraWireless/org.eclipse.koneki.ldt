/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.metalua.internal;

import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaRuntimeException;
import com.naef.jnlua.LuaState;

/**
 * Enables to run Metalua code and source files quickly.
 * 
 * It works with an unique inner {@link LuaState} instance as loading Metalua could be pretty time costly.
 * 
 * @author Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 */
public final class Metalua {

	private Metalua() {
	}

	/** Provides a new LuaState with Metalua capabilities */
	public static synchronized LuaState newState() {
		return MetaluaStateFactory.newLuaState();
	}

	/**
	 * Retrieve error message from a LuaState.
	 * 
	 * @param l
	 *            the l
	 * 
	 * @throws LuaException
	 *             the lua exception
	 */
	public static void raise(LuaState l) {

		// Get message at top of stack
		String msg = l.toString(-1);

		// Clean stack
		l.pop(1);
		throw new LuaRuntimeException(msg);
	}

	/**
	 * Indicate if code contains syntax errors
	 * 
	 * @param code
	 *            to run
	 * @return true is code is correct, otherwise false
	 */
	public static boolean isValid(final String code) {

		// Try to load code without run it
		LuaState state = null;
		try {
			state = newState();
			state.load(code, "isCodeValid"); //$NON-NLS-1$
		} catch (LuaException e) {
			return false;
		} finally {
			if (state != null)
				state.close();
		}

		// Clear stack
		state.pop(1);
		return true;
	}

	public static String path() {
		return MetaluaStateFactory.sourcesPath();
	}
}
