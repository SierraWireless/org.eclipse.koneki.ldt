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

package org.eclipse.koneki.ldt.metalua.tests.internal.cases;

import junit.framework.TestCase;

import org.eclipse.koneki.ldt.metalua.internal.Metalua;

import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;

public class TestMetaluaStateFactory extends TestCase {

	/** Detect error at LuaState allocation */
	public void testLoadable() {
		boolean loaded = true;
		String message = ""; //$NON-NLS-1$
		try {
			LuaState state = Metalua.newState();
			message = state.getClass().getName();
			// Check stack size in order to prevent bad configuration
			int top = state.getTop();
			assertEquals(message + " not stable after initialisation.", top, 0); //$NON-NLS-1$
		} catch (LuaException e) {
			loaded = false;
			message = e.getMessage();
		}
		assertTrue("Metalua is not loaded: " + message, loaded); //$NON-NLS-1$
	}

	/**
	 * Assure that stack is free of arguments at allocation
	 * 
	 * To ensure any error message is hidden in the stack
	 */
	public void testFreeStack() {
		boolean fine = true;
		String msg = ""; //$NON-NLS-1$

		try {
			fine = Metalua.newState().getTop() == 0;
		} catch (LuaException e) {
			msg = e.getMessage();
			fine = false;
		}
		assertTrue("State's stack is not empty at instanciation. " + msg, fine); //$NON-NLS-1$
	}
}
