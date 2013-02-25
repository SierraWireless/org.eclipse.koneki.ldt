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

import org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua.JNLuaInterpreterCommandLineRenderer;
import org.eclipse.koneki.ldt.support.lua52.internal.Activator;
import org.osgi.framework.Bundle;

public class JNLua52InterpreterCommandLineRenderer extends JNLuaInterpreterCommandLineRenderer{

	public static final String JNLUA_BUNDLE_VERSION = "1.0.3"; //$NON-NLS-1$
	
	@Override
	protected String getClassToRun() {
		return JNLua52Launcher.class.getCanonicalName();
	}

	@Override
	protected String getJNLuaBundleVersion() {
		return JNLUA_BUNDLE_VERSION;
	}
	
	@Override
	protected Bundle getLauncherClassBundle() {
		return Activator.getDefault().getBundle();
	}

}
