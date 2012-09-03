/*******************************************************************************
 * Copyright (c) 2011, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.parser.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.koneki.ldt.module.AbstractMetaLuaModule;
import org.eclipse.koneki.ldt.parser.Activator;

import com.naef.jnlua.LuaRuntimeException;
import com.naef.jnlua.LuaState;

/**
 * All about Lua source code transformations.
 * 
 * This class uses <strong>Metalua</strong> to gather information about source code depth and thus enable to modify if adequately.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public final class LuaFormatterModule extends AbstractMetaLuaModule {
	public static final String FORMATTER_PATH = "/script/external"; //$NON-NLS-1$
	public static final String FORMATTER_LIB_NAME = "luaformatter"; //$NON-NLS-1$
	public static final String INDENTATION_FUNTION = "indentcode"; //$NON-NLS-1$

	public LuaFormatterModule() {
	}

	/**
	 * Indents Lua source code
	 * 
	 * @param source
	 *            Lua code to indent
	 * @param delimiter
	 *            Line delimiter, <code>\n</code> for Linux and Unix
	 * @param indentInTable
	 *            Indicates if formating is required for table values
	 * @param tabulation
	 *            String used as tabulation, it could be one or several white space character like <code>' '</code> of <code>'\t'</code>
	 * @return Indented Lua source code
	 */
	public String indent(final String source, final String delimiter, final boolean indentInTable, final String tabulation) {
		// Load function
		final LuaState lua = loadLuaModule();
		pushLuaModule(lua);
		lua.getField(-1, INDENTATION_FUNTION);
		lua.pushString(source);
		lua.pushString(delimiter);
		lua.pushBoolean(indentInTable);
		lua.pushString(tabulation);
		try {
			lua.call(4, 1);
		} catch (final LuaRuntimeException e) {
			Activator.logWarning(Messages.LuaSourceFormatIndentationError, e);
			return source;
		}
		final String formattedCode = lua.toString(-1);
		lua.close();
		return formattedCode;
	}

	/**
	 * Indent Lua source code mixing tabulation and spaces. It will indent with space and reach indentation size with spaces.
	 * 
	 * @param source
	 *            Lua Source code to indent
	 * @param delimiter
	 *            Line delimiter, <code>\n</code> for Linux and Unix
	 * @param indentInTable
	 *            Indicates if formating is required for table values
	 * @param tabSize
	 *            Count of spaces a tabulation mean
	 * @param indentationSizeCount
	 *            of spaces an indentation mean
	 * @return indented Lua source code
	 * @see #indent(String, String, String, int)
	 */
	public String indent(final String source, final String delimiter, final boolean indentInTable, final int tabSize, final int indentationSize) {
		final LuaState lua = loadLuaModule();
		pushLuaModule(lua);
		lua.getField(-1, INDENTATION_FUNTION);
		lua.pushString(source);
		lua.pushString(delimiter);
		lua.pushBoolean(indentInTable);
		lua.pushInteger(tabSize);
		lua.pushInteger(indentationSize);
		try {
			lua.call(5, 1);
		} catch (final LuaRuntimeException e) {
			Activator.logWarning(Messages.LuaSourceFormatIndentationError, e);
			return source;
		}
		final String formattedCode = lua.toString(-1);
		lua.close();
		return formattedCode;
	}

	/**
	 * @see com.naef.jnlua.eclipse.AbstractLuaModule#getLuaSourcePaths()
	 */
	@Override
	protected List<String> getLuaSourcePaths() {
		ArrayList<String> sourcepaths = new ArrayList<String>();
		sourcepaths.add(FORMATTER_PATH);
		return sourcepaths;
	}

	/**
	 * @see com.naef.jnlua.eclipse.AbstractLuaModule#getLuacSourcePaths()
	 */
	@Override
	protected List<String> getLuacSourcePaths() {
		return null;
	}

	/**
	 * @see com.naef.jnlua.eclipse.AbstractLuaModule#getPluginID()
	 */
	@Override
	protected String getPluginID() {
		return Activator.PLUGIN_ID;
	}

	/**
	 * @see com.naef.jnlua.eclipse.AbstractLuaModule#getModuleName()
	 */
	@Override
	protected String getModuleName() {
		return FORMATTER_LIB_NAME;
	}

	/**
	 * @see org.eclipse.koneki.ldt.metalua.AbstractMetaLuaModule#getMetaLuaSourcePaths()
	 */
	@Override
	protected List<String> getMetaLuaSourcePaths() {
		return Collections.emptyList();
	}

	/**
	 * @see org.eclipse.koneki.ldt.metalua.AbstractMetaLuaModule#getMetaLuaFileToCompile()
	 */
	@Override
	protected List<String> getMetaLuaFileToCompile() {
		return Collections.emptyList();
	}
}