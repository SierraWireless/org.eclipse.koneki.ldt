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

import com.naef.jnlua.LuaState;

public class JNLua51DebugLauncher extends JNLua51Launcher {

	protected void loadlibraries(LuaState l) {
		super.loadlibraries(l);
		TransportLayerModule.registerModelFactory(l);
	}

	public static void main(String[] args) {
		JNLua51DebugLauncher jnLuaLauncher = new JNLua51DebugLauncher();
		jnLuaLauncher.run(args);
	}
}
