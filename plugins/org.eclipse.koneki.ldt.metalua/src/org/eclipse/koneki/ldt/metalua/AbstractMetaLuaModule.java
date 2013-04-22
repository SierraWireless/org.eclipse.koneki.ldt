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
package org.eclipse.koneki.ldt.metalua;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.koneki.ldt.metalua.internal.MetaluaStateFactory;
import org.eclipse.osgi.util.NLS;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.eclipse.AbstractLuaModule;

/**
 * Abstract class to manipulate Lua module
 */
public abstract class AbstractMetaLuaModule extends AbstractLuaModule {

	private static final String METALUA_PATTERN = "?.mlua;"; //$NON-NLS-1$

	@Override
	protected LuaState loadLuaModule() {
		compileMetaluaFiles();
		return super.loadLuaModule();
	}

	@Override
	protected void definePaths(final LuaState state) {
		super.definePaths(state);
		final List<File> metaluaSourceFolders = getScriptFolders(getMetaLuaSourcePaths());
		setMetaluaPath(state, metaluaSourceFolders);
	}

	public void compileMetaluaFiles() {
		final LuaState newLuaState = MetaluaStateFactory.newLuaState();

		final List<String> metaLuaFileToCompile = getMetaLuaFileToCompile();
		for (final String metaluaSourcePath : getMetaLuaSourcePaths()) {
			final File metaluaSourceFile = getScriptFolder(metaluaSourcePath);
			if (metaLuaFileToCompile != null && metaluaSourcePath != null) {
				String filename = null;
				try {
					for (int k = 0; k < metaLuaFileToCompile.size(); k++) {
						filename = metaLuaFileToCompile.get(k);
						compileMetaluaFile(newLuaState, metaluaSourceFile, filename);
					}
				} catch (final IOException e) {
					if (filename != null)
						Activator.logWarning(NLS.bind("Unable to compile {0}.", filename), e); //$NON-NLS-1$
					else
						Activator.logWarning("Unable to compile Metalua file.", e); //$NON-NLS-1$
					newLuaState.close();
				}
			}
		}
		newLuaState.close();
	}

	public static void compileMetaluaFile(final LuaState luastate, final File folder, final String fileName) throws IOException {
		final File regular = new File(folder, fileName);
		if (regular.isFile() && regular.exists()) {
			final String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
			final File build = new File(folder, fileNameWithoutExtension + ".luac"); //$NON-NLS-1$
			// Compile metalua lib
			final StringBuffer command = new StringBuffer("require 'metalua.package'\n"); //$NON-NLS-1$
			command.append("local mlc = require ('metalua.compiler').new()\n"); //$NON-NLS-1$
			command.append("local bin = mlc:srcfile_to_bytecode([["); //$NON-NLS-1$
			command.append(regular.getPath());
			command.append("]])\n"); //$NON-NLS-1$

			// Write compiled file on disk
			command.append("local file = io.open([["); //$NON-NLS-1$
			command.append(build.getPath());
			command.append("]], 'wb') file:write(bin) file:close()\n"); //$NON-NLS-1$
			luastate.load(command.toString(), "libraryCompilation"); //$NON-NLS-1$
			luastate.call(0, 0);
		}
	}

	@Override
	protected List<String> getLuacSourcePaths() {
		return getMetaLuaSourcePaths();
	}

	@Override
	protected LuaState createLuaState() {
		return MetaluaStateFactory.newLuaState();
	}

	protected abstract List<String> getMetaLuaSourcePaths();

	protected abstract List<String> getMetaLuaFileToCompile();

	public static void setMetaluaPath(final LuaState luaState, final List<File> metaluaFolders) {
		// Update Metalua path
		final StringBuffer code = new StringBuffer("package.mpath=[["); //$NON-NLS-1$
		for (final File folder : metaluaFolders) {
			code.append(folder.getPath());
			code.append(File.separatorChar);
			code.append(METALUA_PATTERN);
		}
		code.append("]]..package.mpath"); //$NON-NLS-1$
		luaState.load(code.toString(), "reloadingMetaluaPath"); //$NON-NLS-1$
		luaState.call(0, 0);

		// Allow to load *.luac for those modules
		setLuaPath(luaState, Collections.<File> emptyList(), metaluaFolders);
	}
}
