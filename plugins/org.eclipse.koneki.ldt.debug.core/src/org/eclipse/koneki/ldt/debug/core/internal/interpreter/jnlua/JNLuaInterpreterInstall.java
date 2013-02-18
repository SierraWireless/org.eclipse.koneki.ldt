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

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.launching.AbstractInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.IInterpreterRunner;

public abstract class JNLuaInterpreterInstall extends AbstractInterpreterInstall {

	public JNLuaInterpreterInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}

	@Override
	public IInterpreterRunner getInterpreterRunner(String mode) {
		final IInterpreterRunner runner = super.getInterpreterRunner(mode);

		if (runner != null) {
			return runner;
		}

		if (ILaunchManager.RUN_MODE.equals(mode)) {
			return createInterpreterRunner();
		}
		return null;
	}

	protected abstract IInterpreterRunner createInterpreterRunner();

	@Override
	public String[] getInterpreterArguments() {
		// TODO BUG_ECLIPSE 390358
		String interpreterArgs = getInterpreterArgs();
		if (interpreterArgs == null || interpreterArgs.isEmpty())
			return null;
		return new String[] { getInterpreterArgs() };
	}

	public String toString() {
		return getName();
	}

	@Override
	protected abstract IInterpreterRunner getDebugInterpreterRunner();

}
