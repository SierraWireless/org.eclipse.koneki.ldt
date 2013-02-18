/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.support.lua51.internal.interpreter;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua.JNLuaInterpreterCommandLineRenderer;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua.JNLuaInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.generic.LuaGenericInterpreterRunner;

public class JNLua51InterpreterRunner extends JNLuaInterpreterRunner {

	public JNLua51InterpreterRunner(IInterpreterInstall install) {
		super(install);
	}

	@Override
	protected JNLuaInterpreterCommandLineRenderer createRenderCommandLine() {
		return new JNLua51InterpreterCommandLineRenderer();
	}

	@Override
	protected String[] renderCommandLine(final InterpreterConfig config) {
		final JNLua51InterpreterCommandLineRenderer commandLineRenderer = new JNLua51InterpreterCommandLineRenderer();
		return commandLineRenderer.renderCommandLine(config, getInstall());
	}
}
