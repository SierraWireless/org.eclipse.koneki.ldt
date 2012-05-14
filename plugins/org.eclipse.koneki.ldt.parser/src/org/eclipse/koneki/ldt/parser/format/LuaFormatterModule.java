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
import java.util.List;

import org.eclipse.koneki.ldt.metalua.MetaluaStateFactory;
import org.eclipse.koneki.ldt.module.AbstractLuaModule;
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
public final class LuaFormatterModule extends AbstractLuaModule {
	public static final String FORMATTER_PATH = "/script/external"; //$NON-NLS-1$
	public static final String FORMATTER_LIB_NAME = "luaformatter"; //$NON-NLS-1$
	public static final String INDENTATION_FUNTION = "indentCode"; //$NON-NLS-1$

	public LuaFormatterModule() {
	}

	/**
	 * Provide semantic depth of a given source code offset.
	 * 
	 * @param source
	 *            Lua source code to analyze
	 * @param offset
	 *            Source code position which depth is required
	 * @return Offset semantic depth
	 */
	public int depth(final String source, final int offset) {
		// Load function
		final LuaState lua = loadLuaModule();
		pushLuaModule(lua);
		lua.getField(-1, "indentLevel"); //$NON-NLS-1$

		// Pass arguments
		lua.pushString(source);
		lua.pushInteger(offset);

		// Call with parameters count and return values count
		try {
			lua.call(2, 1);
		} catch (final LuaRuntimeException e) {
			Activator.logWarning(Messages.LuaSourceFormatDepthError, e);
			return 0;
		}
		final int result = lua.toInteger(-1);
		lua.close();
		return result > 0 ? result - 1 : result;
	}

	/**
	 * Indents Lua source code
	 * 
	 * @param source
	 *            Lua code to indent
	 * @param delimiter
	 *            Line delimiter, <code>\n</code> for Linux and Unix
	 * @param tabulation
	 *            String used as tabulation, it could be one or several white space character like <code>' '</code> of <code>'\t'</code>
	 * @param indentInTable
	 *            Indicates if formating is required for table values
	 * @param originalIndentationLevel
	 *            Indicates original semantic depth, useful for selections
	 * @return Indented Lua source code
	 */
	public String indent(final String source, final String delimiter, final String tabulation, final boolean indentInTable,
			final int originalIndentationLevel) {
		// Load function
		final LuaState lua = loadLuaModule();
		pushLuaModule(lua);
		lua.getField(-1, INDENTATION_FUNTION);
		lua.pushString(source);
		lua.pushString(delimiter);
		lua.pushString(tabulation);
		lua.pushInteger(originalIndentationLevel);
		lua.pushBoolean(indentInTable);
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
	 * Indent Lua source code mixing tabulation and spaces. It will indent with space and reach indentation size with spaces.
	 * 
	 * @param source
	 *            Lua Source code to indent
	 * @param delimiter
	 *            Line delimiter, <code>\n</code> for Linux and Unix
	 * @param tabSize
	 *            Count of spaces a tabulation mean
	 * @param indentationSizeCount
	 *            of spaces an indentation mean
	 * @param indentInTable
	 *            Indicates if formating is required for table values
	 * @param originalIndentationLevel
	 *            Indicates original semantic depth, useful for selections
	 * @return indented Lua source code
	 * @see #indent(String, String, String, int)
	 */
	public String indent(final String source, final String delimiter, final int tabSize, final int indentationSize, final boolean indentInTable,
			final int originalIndentationLevel) {
		final LuaState lua = loadLuaModule();
		pushLuaModule(lua);
		lua.getField(-1, INDENTATION_FUNTION);
		lua.pushString(source);
		lua.pushString(delimiter);
		lua.pushInteger(tabSize);
		lua.pushInteger(indentationSize);
		lua.pushInteger(originalIndentationLevel);
		lua.pushBoolean(indentInTable);
		try {
			lua.call(6, 1);
		} catch (final LuaRuntimeException e) {
			Activator.logWarning(Messages.LuaSourceFormatIndentationError, e);
			return source;
		}
		final String formattedCode = lua.toString(-1);
		lua.close();
		return formattedCode;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractLuaModule#getLuaSourcePaths()
	 */
	@Override
	protected List<String> getLuaSourcePaths() {
		ArrayList<String> sourcepaths = new ArrayList<String>();
		sourcepaths.add(FORMATTER_PATH);
		return sourcepaths;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractLuaModule#getLuacSourcePaths()
	 */
	@Override
	protected List<String> getLuacSourcePaths() {
		return null;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractLuaModule#createLuaState()
	 */
	@Override
	protected LuaState createLuaState() {
		return MetaluaStateFactory.newLuaState();
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractLuaModule#getPluginID()
	 */
	@Override
	protected String getPluginID() {
		return Activator.PLUGIN_ID;
	}

	/**
	 * @see org.eclipse.koneki.ldt.module.AbstractLuaModule#getModuleName()
	 */
	@Override
	protected String getModuleName() {
		return FORMATTER_LIB_NAME;
	}
}
