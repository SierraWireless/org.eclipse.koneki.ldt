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
package org.eclipse.koneki.ldt.debug.core.internal;

import org.eclipse.dltk.debug.core.model.AtomicScriptType;
import org.eclipse.dltk.debug.core.model.IScriptType;
import org.eclipse.dltk.debug.core.model.IScriptTypeFactory;

public class LuaTypeFactory implements IScriptTypeFactory {

	public LuaTypeFactory() {
	}

	@Override
	public IScriptType buildType(String type) {
		// TODO: script types are stateless, do only one instance of them
		if (type.equals(LuaDebugConstant.TYPE_TABLE))
			return new LuaTableType();
		else if (type.equals(LuaDebugConstant.TYPE_MULTIVAL))
			return new LuaMultivalType();
		else if (type.equals(LuaDebugConstant.TYPE_SEQUENCE))
			return new LuaSequenceType();
		else if (type.equals(LuaDebugConstant.TYPE_LUAFUNC))
			return new LuaFunctionType();
		else
			return new AtomicScriptType(type);
	}
}
