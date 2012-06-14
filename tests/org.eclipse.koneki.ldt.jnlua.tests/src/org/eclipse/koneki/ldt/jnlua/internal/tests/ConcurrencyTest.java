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

package org.eclipse.koneki.ldt.jnlua.internal.tests;

import junit.framework.TestCase;

import org.junit.Test;

import com.naef.jnlua.LuaState;

/**
 * The aim here is to ensure that LuaJava is able to handle concurrent calls from different {@link LuaState}.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class ConcurrencyTest extends TestCase {

	/** Quantity of thread to perform calls on LuaJava simultaneously */
	private static final int THREAD_COUNT = 5;

	/**
	 * Internal definition of thread for the only purpose to perform a function call using LuaJava
	 */
	private static class JNLuaUse extends Thread {

		private LuaState state;

		public JNLuaUse(LuaState l) {
			state = l;
		}

		/**
		 * Compute the result of the Fibonacci serie from Lua using a {@link LuaState}. The execution of this method take a few time on purpose, in
		 * order to ensure thats different threads will call LuaJava at the same time.
		 */
		@Override
		public void run() {

			// Define Fibonacci serie
			String code = "function fibo(n) if n< 2 then return 1 end return fibo(n-1) + fibo(n-2) end"; //$NON-NLS-1$

			// Load function
			state.load(code, "fibonacciFunction"); //$NON-NLS-1$
			state.call(0, 0);

			// Retrieve function in Lua
			state.getField(LuaState.GLOBALSINDEX, "fibo"); //$NON-NLS-1$

			// Pass an argument to the function
			state.pushNumber(32);

			// Call the Fibonacci serie
			assert state.isNumber(-1) && state.isFunction(-2) : "Badly formed function call."; //$NON-NLS-1$
			state.call(1, 1);

			// Clean stack
			state.pop(1);
		}
	}

	/**
	 * Here we targeting to activate several thread which are going to use a {@link LuaState} at the same time. If the component is tread safe,
	 * nothing will happen. Else way, the execution of the unit tests will crash.
	 */
	@Test
	public void testLuaJavaUse() {

		// Create several threads
		Thread[] threads = new Thread[THREAD_COUNT];
		for (int k = 0; k < THREAD_COUNT; k++) {
			threads[k] = new JNLuaUse(new LuaState());
		}

		// Activate all of them, they will start to use LuaJava randomly
		for (Thread thread : threads) {
			thread.start();
		}

		// Check if all threads terminate gently
		int count = 0;
		for (Thread thread : threads) {
			try {
				thread.join();
				count++;
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		assertEquals("A thread encounter an error", count, THREAD_COUNT); //$NON-NLS-1$
	}

}