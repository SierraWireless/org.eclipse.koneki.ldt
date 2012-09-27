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
package org.eclipse.koneki.ldt.remote.core.internal.lua;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.rse.core.IRSESystemType;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.IProperty;
import org.eclipse.rse.core.model.IPropertySet;
import org.eclipse.rse.core.model.PropertySet;
import org.eclipse.rse.core.model.PropertyType;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.SubSystem;

public class LuaSubSystem extends SubSystem implements ISubSystem {

	public static final String LUACOMMAND_PROPERTY_KEY = "lua_command"; //$NON-NLS-1$
	public static final String LUAPATH_PROPERTY_KEY = "lua_path"; //$NON-NLS-1$
	public static final String LUACPATH_PROPERTY_KEY = "lua_cpath"; //$NON-NLS-1$
	public static final String LDLIBRARYPATH_PROPERTY_KEY = "ld_library_path"; //$NON-NLS-1$
	public static final String OUTPUTDIRECTORY_PROPERTY_KEY = "output_dir"; //$NON-NLS-1$

	private static final String LUA_PROPERTY_SET_KEY = "luaSet"; //$NON-NLS-1$

	// Define default value for properties
	private static final PropertySet DEFAULT_PROPERTY_SET;
	private static final String DEFAULT_LUACOMMAND_PROPERTY_VALUE = "lua"; //$NON-NLS-1$
	private static final String DEFAULT_OUTPUTDIRECTORY_PROPERTY_VALUE = "/tmp"; //$NON-NLS-1$

	static {
		DEFAULT_PROPERTY_SET = new PropertySet("defaultLuaPropertySet"); //$NON-NLS-1$
		DEFAULT_PROPERTY_SET.addProperty(LUACOMMAND_PROPERTY_KEY, DEFAULT_LUACOMMAND_PROPERTY_VALUE);
		DEFAULT_PROPERTY_SET.addProperty(OUTPUTDIRECTORY_PROPERTY_KEY, DEFAULT_OUTPUTDIRECTORY_PROPERTY_VALUE);
	}

	/**
	 * Create a new Lua SubSystem
	 */
	protected LuaSubSystem(IHost host, IConnectorService connectorService) {
		super(host, connectorService);
		// create propertySet which hold all lua properties
		createPropertySet(LUA_PROPERTY_SET_KEY);
	}

	/**
	 * @see org.eclipse.rse.core.subsystems.SubSystem#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/**
	 * @see org.eclipse.rse.core.subsystems.SubSystem#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		// If adapter factory host plugin is not loaded yet
		final IAdapterManager manager = Platform.getAdapterManager();
		if (IAdapterManager.NOT_LOADED == manager.queryAdapter(this, adapter.getName())) {
			// Require its load
			return manager.loadAdapter(this, adapter.getName());
		}
		// Adapter factory must be available
		return super.getAdapter(adapter);
	}

	public void setLuaPropertyValue(String key, String value) {
		IPropertySet propertySet = getPropertySet(LUA_PROPERTY_SET_KEY);
		IProperty property = propertySet.getProperty(key);
		if (property == null) {
			propertySet.addProperty(key, value, PropertyType.getStringPropertyType());
			commit();
		} else {
			property.setValue(value);
			commit();
		}
	}

	public String getLuaPropertyValue(String key) {
		// search in Lua SubSystem propertySet
		IPropertySet propertySet = getPropertySet(LUA_PROPERTY_SET_KEY);
		if (propertySet != null) {
			String propertyValue = propertySet.getPropertyValue(key);
			if (propertyValue != null) {
				return propertyValue;
			}
		}

		// if not found, search in SystemPropertySet
		IHost host = getHost();
		if (host != null) {
			IRSESystemType systemType = host.getSystemType();
			if (systemType != null) {
				String propertyValue = systemType.getProperty(key);
				if (propertyValue != null)
					return propertyValue;
			}
		}

		// if not found search in default propertySet
		String propertyValue = DEFAULT_PROPERTY_SET.getPropertyValue(key);
		if (propertyValue != null) {
			return propertyValue;
		}

		// no value found ...
		return ""; //$NON-NLS-1$
	}
}