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
package org.eclipse.koneki.ldt.debug.core.internal.interpreter.generic;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.launching.AbstractInterpreterRunner;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.koneki.ldt.debug.core.internal.Activator;

public class LuaGenericInterpreterRunner extends AbstractInterpreterRunner {

	public LuaGenericInterpreterRunner(IInterpreterInstall install) {
		super(install);
	}

	@Override
	protected void alterConfig(ILaunch launch, InterpreterConfig config) {
		LuaGenericInterpreterConfigurer luaGenericInterpreterConfigurer = new LuaGenericInterpreterConfigurer();

		try {
			luaGenericInterpreterConfigurer.alterConfig(launch, config, getInstall());
		} catch (CoreException e) {
			// TODO we should be able to raise a core exception here (We should open a DLTK bug ?)
			Activator.log(e.getStatus());
		}
	}

	@Override
	protected String[] renderCommandLine(InterpreterConfig config) {
		LuaGenericInterpreterCommandLineRenderer commandLineRenderer = new LuaGenericInterpreterCommandLineRenderer();
		return commandLineRenderer.renderCommandLine(config, getInstall());
	}

	// TODO HACK ENV_VAR : make environment variable defined at interpreter
	// level less priority
	// ****************************************************************************
	@Override
	protected String[] getEnvironmentVariablesAsStrings(InterpreterConfig config) {
		return config.getEnvironmentAsStrings();
	}
	// END HACK
	// ****************************************************************************

}
