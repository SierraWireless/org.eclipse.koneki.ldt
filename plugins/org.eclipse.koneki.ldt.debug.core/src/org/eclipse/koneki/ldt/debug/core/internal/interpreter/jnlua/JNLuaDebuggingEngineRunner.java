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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.generic.LuaGenericDebuggingEngineRunner;

public abstract class JNLuaDebuggingEngineRunner extends LuaGenericDebuggingEngineRunner {

	public JNLuaDebuggingEngineRunner(IInterpreterInstall install) {
		super(install);
	}

	@Override
	protected String[] renderCommandLine(InterpreterConfig config) {
		JNLuaDebugginEngineCommandLineRenderer commandLineRenderer = createRenderCommandLine();
		return commandLineRenderer.renderCommandLine(config, getInstall());
	}

	@Override
	protected InterpreterConfig addEngineConfig(InterpreterConfig config, PreferencesLookupDelegate delegate, ILaunch launch) throws CoreException {
		JNLuaDebuggingEngineConfigurer configurer = new JNLuaDebuggingEngineConfigurer();
		InterpreterConfig alteredConfig = configurer.alterConfig(launch, config, getInstall());
		return alteredConfig;
	}

	protected abstract JNLuaDebugginEngineCommandLineRenderer createRenderCommandLine();
}
