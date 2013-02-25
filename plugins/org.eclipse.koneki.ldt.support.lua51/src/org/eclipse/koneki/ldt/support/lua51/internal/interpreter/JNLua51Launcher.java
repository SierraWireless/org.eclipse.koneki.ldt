/*******************************************************************************
 * Copyright (c) 2013 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.support.lua51.internal.interpreter;

import java.util.ArrayList;
import java.util.List;

import com.naef.jnlua.LuaState;

public class JNLua51Launcher {

	protected JNLua51Launcher() {

	}

	public void run(String[] args) {
		// load lua library
		System.loadLibrary("lua5.1"); //$NON-NLS-1$

		// create Lua VM
		final LuaState l = new LuaState();

		// manage arguments
		boolean showVersion = false;
		List<Runnable> actions = new ArrayList<Runnable>();
		String scriptToLoad = null;
		List<String> scriptArg = new ArrayList<String>();

		for (int i = 0; i < args.length; i++) {
			// CHECKSTYLE:OFF
			String arg = args[i];

			if ("-i".equals(arg)) //$NON-NLS-1$
			{
				System.out.println("Warning : -i option is not supported by JNLua Interpreter."); //$NON-NLS-1$
			} else if ("-v".equals(arg)) //$NON-NLS-1$
			{
				showVersion = true;
			} else if ("-l".equals(arg)) //$NON-NLS-1$
			{
				// manage -l option
				i++;
				if (i < args.length) {
					// get library name
					final String library = args[i];

					// add loading action
					actions.add(new Runnable() {
						@Override
						public void run() {
							l.load("require ([[" + library + "]])", "-l option"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							l.call(0, 0);
						}
					});
				}
			} else if ("-e".equals(arg)) //$NON-NLS-1$
			{
				// manage -e option
				i++;
				if (i < args.length) {
					// get code to execute
					final String code = args[i];

					// add executing action
					actions.add(new Runnable() {
						@Override
						public void run() {
							l.load(code, "-e option"); //$NON-NLS-1$
							l.call(0, 0);
						}
					});
				}
			} else {
				// manage the script to execute
				scriptToLoad = args[i];
				i++;
				// manage the script arguments
				for (; i < args.length; i++) {
					scriptArg.add(args[i]);
				}
			}
		}

		// Execute the script
		try {
			// load system libraries
			loadlibraries(l);

			// show version
			if (showVersion) {
				l.getGlobal("_VERSION"); //$NON-NLS-1$
				String luaVersion = l.toString(1);
				String jnluaVersion = LuaState.VERSION;
				l.pop(1);
				System.err.println(luaVersion + " on JNLua " + jnluaVersion); //$NON-NLS-1$
			}

			// execute actions
			for (Runnable action : actions) {
				action.run();
			}

			// load script argument
			l.newTable(args.length, 0);
			for (int i = 0; i < scriptArg.size(); i++) {
				l.pushString(scriptArg.get(i));
				l.rawSet(-2, i + 1);
			}
			l.setGlobal("arg"); //$NON-NLS-1$

			// execute script
			if (scriptToLoad != null) {
				l.load("assert(loadfile([[" + scriptToLoad + "]]))(unpack(arg,1,#arg))", "main"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				l.call(0, 0);
			}

			// CHECKSTYLE:ON
		} finally {
			l.close();
		}
	}

	protected void loadlibraries(LuaState l) {
		l.openLibs();
	}

	public static void main(String[] args) {
		JNLua51Launcher jnLuaLauncher = new JNLua51Launcher();
		jnLuaLauncher.run(args);
	}
}
