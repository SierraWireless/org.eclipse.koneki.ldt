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
package org.eclipse.koneki.ldt.remote.ui.internal.lua;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.koneki.ldt.remote.core.internal.lua.LuaSubSystem;
import org.eclipse.rse.ui.view.ISystemViewElementAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

public class LuaSubSystemAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adaptableObject instanceof LuaSubSystem) {
			ISystemViewElementAdapter luaSubSystemAdapter = new LuaSubSystemAdapter((LuaSubSystem) adaptableObject);
			luaSubSystemAdapter.setPropertySourceInput(adaptableObject);
			return luaSubSystemAdapter;
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
		return new Class<?>[] { ISystemViewElementAdapter.class, IPropertySource.class };
	}
}
