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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.koneki.ldt.internal.parser.DLTKObjectFactory;
import org.eclipse.koneki.ldt.metalua.MetaluaStateFactory;
import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;

import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;

/**
 * Generates AST from Metalua analysis, {@link ASTNode}s are created straight from Lua
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class AlternativeLuaSourceParser extends AbstractSourceParser {
	public static final String LIB_PATH = "/scriptMetalua/";//$NON-NLS-1$
	public static final String BUILDER = "dltk_ast_builder";//$NON-NLS-1$
	public static final String BUILDER_SCRIPT = BUILDER + ".mlua";//$NON-NLS-1$
	//	public static final String BUILDER_BINARY = BUILDER + ".luac";//$NON-NLS-1$
	public static final String MARKER = "declaration_marker";//$NON-NLS-1$
	public static final String MARKER_SCRIPT = MARKER + ".mlua";//$NON-NLS-1$
	private static LuaState lua = null;

	// BEGIN CACHE MANAGEMENT
	// TODO DLTK has already a cache system but it can be used to keep the last valid AST.
	// so we have to cache system.
	// Ideally, the parser should manage file with syntax errors..
	private static Map<IModelElement, IModuleDeclaration> cache = new Hashtable<IModelElement, IModuleDeclaration>();
	private static IElementChangedListener changedListener = new IElementChangedListener() {
		public void elementChanged(ElementChangedEvent event) {
			synchronized (AlternativeLuaSourceParser.class) {
				IModelElementDelta delta = event.getDelta();
				processDelta(delta);
			}
		}

		private void processDelta(IModelElementDelta delta) {
			IModelElement element = delta.getElement();
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				if (delta.getKind() == IModelElementDelta.REMOVED) {
					cache.remove(element);
				} else if (delta.getKind() == IModelElementDelta.CHANGED && delta.getFlags() == IModelElementDelta.F_PRIMARY_WORKING_COPY) {
					cache.remove(element);
				}
			}
			if (delta.getFlags() == IModelElementDelta.F_REMOVED_FROM_BUILDPATH) {
				if (delta.getAffectedChildren().length == 0) {
					for (IModelElement sourcemodule : new ArrayList<IModelElement>(cache.keySet())) {
						if (LuaASTUtils.isAncestor(sourcemodule, element)) {
							cache.remove(sourcemodule);
						}
					}
				}
			}
			if ((delta.getFlags() & IModelElementDelta.F_CHILDREN) != 0) {
				IModelElementDelta[] affectedChildren = delta.getAffectedChildren();
				for (int i = 0; i < affectedChildren.length; i++) {
					IModelElementDelta child = affectedChildren[i];
					processDelta(child);
				}
			}
		}
	};
	static {
		DLTKCore.addElementChangedListener(changedListener);
	}

	// END CACHE MANAGEMENT

	public AlternativeLuaSourceParser() {
	}

	private static synchronized LuaState getLuaState() {
		if (lua == null) {
			final String require = "require"; //$NON-NLS-1$
			lua = MetaluaStateFactory.newLuaState();
			// Load module which helps avoiding reflection between Lua and Java
			DLTKObjectFactory.register(lua);
			// Load needed files
			try {
				/*
				 * Load compiled Metalua script from path
				 */
				final URL folderUrl = FileLocator.toFileURL(Platform.getBundle(Activator.PLUGIN_ID).getEntry(LIB_PATH));
				final File folder = new File(folderUrl.getFile());
				compileMetaluaFile(folder, BUILDER_SCRIPT);
				compileMetaluaFile(folder, MARKER_SCRIPT);

				// Change path
				final StringBuffer code = new StringBuffer("package.path=[["); //$NON-NLS-1$
				code.append(folder.getPath());
				code.append(File.separatorChar);
				code.append("?.luac;]]..package.path"); //$NON-NLS-1$
				lua.load(code.toString(), "reloadingPath"); //$NON-NLS-1$
				lua.call(0, 0);
				lua.getGlobal(require);
				lua.pushString(MARKER);
				lua.call(1, 1);
				lua.setGlobal("mark"); //$NON-NLS-1$
				lua.getGlobal(require);
				lua.pushString(BUILDER);
				lua.call(1, 1);
				lua.setGlobal("parsemod"); //$NON-NLS-1$
			} catch (IOException e) {
				Activator.logError(Messages.AlternativeLuaSourceParserUnableToBuild, e);
			}
		}
		return lua;
	}

	private static void compileMetaluaFile(final File folder, final String fileName) throws IOException {
		final File regular = new File(folder, fileName);
		final String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
		final File build = new File(folder, fileNameWithoutExtension + ".luac"); //$NON-NLS-1$
		// Compile metalua lib
		final LuaState justForCompilation = MetaluaStateFactory.newLuaState();
		try {
			final StringBuffer command = new StringBuffer("local bin  = mlc.luafile_to_luacstring([["); //$NON-NLS-1$
			command.append(regular.getPath());
			command.append("]]) "); //$NON-NLS-1$
			// Write compiled file on disk
			command.append("local file = io.open([["); //$NON-NLS-1$
			command.append(build.getPath());
			command.append("]], 'wb') file:write(bin) file:close()"); //$NON-NLS-1$
			justForCompilation.load(command.toString(), "libraryCompilation"); //$NON-NLS-1$
			justForCompilation.call(0, 0);
		} finally {
			justForCompilation.close();
		}
	}

	/**
	 * Generate DLTK AST straight from Lua
	 * 
	 * @param input
	 *            Source to parse
	 * @param reporter
	 *            Enable to report errors in parsed source code
	 */
	@Override
	public IModuleDeclaration parse(IModuleSource input, IProblemReporter reporter) {
		LuaSourceRoot module = new LuaSourceRoot(input.getSourceContents().length());

		synchronized (AlternativeLuaSourceParser.class) {
			try {
				// parse module
				// Call module's parsing function
				getLuaState().getGlobal("parsemod");//$NON-NLS-1$
				getLuaState().getField(-1, "ast_builder"); //$NON-NLS-1$
				getLuaState().pushString(input.getSourceContents());
				getLuaState().call(1, 1);
				module = getLuaState().checkJavaObject(-1, LuaSourceRoot.class);
				getLuaState().pop(2);

			} catch (LuaException e) {
				Activator.logError("Unable to load metalua ast builder :" + input.getFileName(), e); //$NON-NLS-1$
			}
			// Deal with errors on Lua side
			if (module != null) {
				if (module.hasError()) {
					final DefaultProblem problem = module.getProblem();
					problem.setOriginatingFileName(input.getFileName());
					reporter.reportProblem(problem);

					// manage cache
					if (input.getModelElement() != null) {
						final LuaSourceRoot cached = (LuaSourceRoot) cache.get(input.getModelElement());
						if (cached != null) {
							cached.setError(true);
							return cached;
						}
					}
				} else {
					if (input.getModelElement() != null) {
						cache.put(input.getModelElement(), module);
					}
				}
			}
		}
		return module;
	}
}
