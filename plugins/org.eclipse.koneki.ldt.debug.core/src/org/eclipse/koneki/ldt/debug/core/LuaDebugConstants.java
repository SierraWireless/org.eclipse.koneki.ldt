/*******************************************************************************
 * Copyright (c) 2011-2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.core;

/**
 * Constant for attribute of Lua launch configuration
 */
public interface LuaDebugConstants {

	/**
	 * Extension point constants
	 */
	String ATTACH_LAUNCH_CONFIGURATION_ID = "org.eclipse.koneki.ldt.debug.core.luaattachdebug"; //$NON-NLS-1$
	String LOCAL_LAUNCH_CONFIGURATION_ID = "org.eclipse.koneki.ldt.debug.core.lualocaldebug"; //$NON-NLS-1$

	/**
	 * Launch Configuration constant
	 */
	String ATTR_LUA_SOURCE_MAPPING_TYPE = "source_mapping_type"; //$NON-NLS-1$

	/**
	 * Source mapping type
	 */
	String LOCAL_MAPPING_TYPE = "local"; //$NON-NLS-1$
	String MODULE_MAPPING_TYPE = "module"; //$NON-NLS-1$
	String REPLACE_PATH_MAPPING_TYPE = "replace_path"; //$NON-NLS-1$

	/**
	 * Type name constants
	 */
	String TYPE_TABLE = "table"; //$NON-NLS-1$
	String TYPE_SEQUENCE = "sequence"; //$NON-NLS-1$
	String TYPE_MULTIVAL = "multival"; //$NON-NLS-1$
	String TYPE_LUAFUNC = "function (Lua)"; //$NON-NLS-1$
	String TYPE_SPECIAL = "special"; //$NON-NLS-1$

	/**
	 * Plug-in path
	 */
	String SCRIPT_PATH = "script"; //$NON-NLS-1$
	String DEBUGGER_PATH = SCRIPT_PATH + "/external"; //$NON-NLS-1$

	final String LUA_PATH = "LUA_PATH"; //$NON-NLS-1$
}
