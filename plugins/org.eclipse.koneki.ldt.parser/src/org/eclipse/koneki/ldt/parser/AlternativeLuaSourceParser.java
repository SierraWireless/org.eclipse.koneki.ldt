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
import java.util.Hashtable;
import java.util.Map;

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
import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;

import com.naef.jnlua.LuaException;

/**
 * Generates AST from Metalua analysis, {@link ASTNode}s are created straight from Lua
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class AlternativeLuaSourceParser extends AbstractSourceParser {

	private static ModelsBuilderLuaModule astBuilder = new ModelsBuilderLuaModule();

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
						if (LuaDLTKModelUtils.isAncestor(sourcemodule, element)) {
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
				module = astBuilder.buildAST(input.getSourceContents());
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
