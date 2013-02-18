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

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua.JNLuaInterpreterInstall;

public class JNLua51InterpreterInstall extends JNLuaInterpreterInstall {

	public JNLua51InterpreterInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}

	@Override
	protected IInterpreterRunner createInterpreterRunner() {
		return new JNLua51InterpreterRunner(this);
	}

	@Override
	public IInterpreterRunner getInterpreterRunner(final String mode) {
		final IInterpreterRunner runner = super.getInterpreterRunner(mode);

		if (runner != null) {
			return runner;
		}

		if (ILaunchManager.RUN_MODE.equals(mode)) {
			return new JNLua51InterpreterRunner(this);
		}
		return null;
	}

	@Override
	protected IInterpreterRunner getDebugInterpreterRunner() {
		return new JNLua51DebuggingEngineRunner(this);
	}
}
