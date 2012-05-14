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
package org.eclipse.koneki.ldt.module;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.koneki.ldt.metalua.MetaluaStateFactory;

import com.naef.jnlua.LuaState;

/**
 * Abstract class to manipulate Lua module
 */
public abstract class AbstractMetaLuaModule extends AbstractLuaModule {

	@Override
	protected LuaState loadLuaModule() {
		compileMetaluaFiles();
		return super.loadLuaModule();
	}

	public void compileMetaluaFiles() {
		LuaState newLuaState = MetaluaStateFactory.newLuaState();
		try {

			List<String> metaLuaFileToCompile = getMetaLuaFileToCompile();
			for (String metaluaSourcePath : getMetaLuaSourcePaths()) {
				File metaluaSourceFile = getScriptFolder(metaluaSourcePath);
				if (metaLuaFileToCompile != null && metaluaSourcePath != null) {
					for (String filename : metaLuaFileToCompile) {
						compileMetaluaFile(newLuaState, metaluaSourceFile, filename);
					}
				}
			}
		} catch (IOException e) {
			newLuaState.close();
		}
	}

	public static void compileMetaluaFile(final LuaState luastate, final File folder, final String fileName) throws IOException {
		final File regular = new File(folder, fileName);
		if (regular.isFile() && regular.exists()) {
			final String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
			final File build = new File(folder, fileNameWithoutExtension + ".luac"); //$NON-NLS-1$

			// Compile metalua lib
			final StringBuffer command = new StringBuffer("local bin  = mlc.luafile_to_luacstring([["); //$NON-NLS-1$
			command.append(regular.getPath());
			command.append("]]) "); //$NON-NLS-1$
			// Write compiled file on disk
			command.append("local file = io.open([["); //$NON-NLS-1$
			command.append(build.getPath());
			command.append("]], 'wb') file:write(bin) file:close()"); //$NON-NLS-1$
			luastate.load(command.toString(), "libraryCompilation"); //$NON-NLS-1$
			luastate.call(0, 0);
		}
	}

	protected List<String> getLuacSourcePaths() {
		return getMetaLuaSourcePaths();
	}

	protected LuaState createLuaState() {
		return MetaluaStateFactory.newLuaState();
	}

	protected abstract List<String> getMetaLuaSourcePaths();

	protected abstract List<String> getMetaLuaFileToCompile();

	protected abstract String getPluginID();

	protected abstract String getModuleName();
}
