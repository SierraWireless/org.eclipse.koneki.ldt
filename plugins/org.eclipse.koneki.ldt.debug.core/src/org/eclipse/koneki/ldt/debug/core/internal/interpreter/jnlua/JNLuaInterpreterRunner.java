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
package org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.generic.LuaGenericInterpreterRunner;

public abstract class JNLuaInterpreterRunner extends LuaGenericInterpreterRunner {

	public JNLuaInterpreterRunner(IInterpreterInstall install) {
		super(install);
	}

	@Override
	protected String[] renderCommandLine(InterpreterConfig config) {
		JNLuaInterpreterCommandLineRenderer commandLineRenderer = createRenderCommandLine();
		return commandLineRenderer.renderCommandLine(config, getInstall());
	}

	protected abstract JNLuaInterpreterCommandLineRenderer createRenderCommandLine();
}
