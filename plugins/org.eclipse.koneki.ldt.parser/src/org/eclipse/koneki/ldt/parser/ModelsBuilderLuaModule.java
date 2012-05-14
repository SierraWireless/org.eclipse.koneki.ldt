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
package org.eclipse.koneki.ldt.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.koneki.ldt.module.AbstractMetaLuaModule;
import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;

import com.naef.jnlua.LuaState;

public class ModelsBuilderLuaModule extends AbstractMetaLuaModule {

	public static final String LOCAL_LIB_PATH = "/script/local";//$NON-NLS-1$
	public static final String EXTERNAL_LIB_PATH = "/script/external";//$NON-NLS-1$

	public static final String MODELS_BUILDER = "javamodelsbuilder";//$NON-NLS-1$
	public static final String MODELS_BUILDER_SCRIPT = MODELS_BUILDER + ".mlua";//$NON-NLS-1$

	public static final String INTERNAL_MODEL_BUILDER = "models/internalmodelbuilder";//$NON-NLS-1$
	public static final String INTERNAL_MODEL_BUILDER_SCRIPT = INTERNAL_MODEL_BUILDER + ".mlua";//$NON-NLS-1$

	public static final String API_MODEL_BUILDER = "models/apimodelbuilder";//$NON-NLS-1$
	public static final String API_MODEL_BUILDER_SCRIPT = API_MODEL_BUILDER + ".mlua";//$NON-NLS-1$

	private LuaState lua = null;

	public synchronized LuaSourceRoot buildAST(final String string) {
		if (lua == null)
			lua = loadLuaModule();

		pushLuaModule(lua);
		lua.getField(-1, "build"); //$NON-NLS-1$
		lua.pushString(string);
		lua.call(1, 1);
		LuaSourceRoot luaSourceRoot = lua.checkJavaObject(-1, LuaSourceRoot.class);
		lua.pop(2);

		// lua.close();

		return luaSourceRoot;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractMetaLuaModule#loadLuaModule()
	 */
	@Override
	protected LuaState loadLuaModule() {
		LuaState luaState = super.loadLuaModule();
		return luaState;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractMetaLuaModule#getMetaLuaSourcePath()
	 */
	@Override
	protected List<String> getMetaLuaSourcePaths() {
		ArrayList<String> sourcepaths = new ArrayList<String>();
		sourcepaths.add(LOCAL_LIB_PATH);
		sourcepaths.add(EXTERNAL_LIB_PATH);
		return sourcepaths;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractMetaLuaModule#getMetaLuaFileToCompile()
	 */
	@Override
	protected List<String> getMetaLuaFileToCompile() {
		ArrayList<String> sourcepaths = new ArrayList<String>();
		sourcepaths.add(MODELS_BUILDER_SCRIPT);
		sourcepaths.add(INTERNAL_MODEL_BUILDER_SCRIPT);
		sourcepaths.add(API_MODEL_BUILDER_SCRIPT);
		return sourcepaths;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractMetaLuaModule#getPluginID()
	 */
	@Override
	protected String getPluginID() {
		return Activator.PLUGIN_ID;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractMetaLuaModule#getModuleName()
	 */
	@Override
	protected String getModuleName() {
		return MODELS_BUILDER;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractLuaModule#getLuaSourcePaths()
	 */
	@Override
	protected List<String> getLuaSourcePaths() {
		ArrayList<String> sourcepaths = new ArrayList<String>();
		sourcepaths.add(LOCAL_LIB_PATH);
		sourcepaths.add(EXTERNAL_LIB_PATH);
		return sourcepaths;
	}
}
