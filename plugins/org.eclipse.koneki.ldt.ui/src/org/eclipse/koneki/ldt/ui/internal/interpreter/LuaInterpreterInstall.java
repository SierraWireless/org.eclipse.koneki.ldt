/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.interpreter;

import org.eclipse.dltk.launching.AbstractInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.koneki.ldt.core.LuaNature;

public class LuaInterpreterInstall extends AbstractInterpreterInstall {

	public LuaInterpreterInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}

	@Override
	public String getNatureId() {
		return LuaNature.ID;
	}

}
