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

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua.JNLuaInterpreterInstallType;

public class JNLua52InterpreterInstallType extends JNLuaInterpreterInstallType {

	@Override
	public String getName() {
		return "JNLua 5.2"; //$NON-NLS-1$
	}

	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new JNLua52InterpreterInstall(this, id);
	}

}
