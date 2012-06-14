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
package org.eclipse.koneki.ldt.core.tests.internal.ast;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParserFactory;
import org.eclipse.koneki.ldt.core.tests.internal.ast.utils.DummyReporter;

/**
 * Just checks if it is safe to parse several sources file simultaneously
 */
public class TestMultipleParsing extends TestCase {

	/** Increase this counter, in order to test harder */
	private static final int THREAD_COUNT = 5;

	private static class Parsing extends Thread {
		@Override
		public void run() {
			final String code = "do local var= 21 var = 22 end set = 12 while true do set = 0 end repeat set = 1 until true for k = 1,10 do set = 2  end for k = 1,10,2 do set = 2  end for k,i in  12,15  do set =3 end local z = var or {} local var, v local c = nil local var = 12, 0 local function var (o) end while true or false do return end while 1 do return one end while 1 do return one, two end call({}) recall(-12) recall(true, false) local mod mod:sample() mod:sample('yop') mod:sample('yop', ...) if true then call() end if true then call() else recall() end local tab = {} local tab = { var = 12, 3+0 } if true then yop() end if false then raplapla() else yop() end if true then yop() elseif false then callThePolice() end if true then yop() elseif false then callThePolice() else hangUp() end no =function  ()end function another() end function par( x, ... ) end function par( ra, me, tres, ...)end function par( o ) return o end"; //$NON-NLS-1$
			ISourceParser parser = new LuaSourceParserFactory().createSourceParser();
			ModuleSource input = new ModuleSource(code);
			parser.parse(input, new DummyReporter());
		}
	}

	public void testMultipleParsing() {

		// Create several threads
		Thread[] threads = new Thread[THREAD_COUNT];
		for (int k = 0; k < THREAD_COUNT; k++) {
			threads[k] = new Parsing();
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
