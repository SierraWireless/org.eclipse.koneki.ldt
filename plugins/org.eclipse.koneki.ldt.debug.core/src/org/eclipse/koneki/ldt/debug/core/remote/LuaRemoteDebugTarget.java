/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.core.remote;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.debug.core.IDbgpService;
import org.eclipse.koneki.ldt.debug.core.LuaDebugTarget;

public abstract class LuaRemoteDebugTarget extends LuaDebugTarget {

	public LuaRemoteDebugTarget(String modelId, IDbgpService dbgpService, String sessionId, ILaunch launch, IProcess process) {
		super(modelId, dbgpService, sessionId, launch, process);
	}

	@Override
	public boolean isRemote() {
		return true;
	}
}
