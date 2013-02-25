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
package org.eclipse.koneki.ldt.support.lua52.internal.interpreter;

import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua.JNLuaInterpreterInstall;

public class JNLua52InterpreterInstall extends JNLuaInterpreterInstall {

	public JNLua52InterpreterInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}

	@Override
	protected IInterpreterRunner createInterpreterRunner() {
		return new JNLua52InterpreterRunner(this);
	}

	@Override
	protected IInterpreterRunner getDebugInterpreterRunner() {
		return new JNLua52DebuggingEngineRunner(this);
	}
}
