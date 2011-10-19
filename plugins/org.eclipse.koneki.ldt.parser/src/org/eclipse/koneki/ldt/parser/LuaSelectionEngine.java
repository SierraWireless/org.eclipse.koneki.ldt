/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 ******************************************************************************/
package org.eclipse.koneki.ldt.parser;

import java.util.Map;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.koneki.ldt.core.LuaUtils;
import org.eclipse.koneki.ldt.parser.ast.declarations.LuaModuleDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.ModuleReference;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;
import org.eclipse.koneki.ldt.parser.ast.expressions.Index;
import org.eclipse.koneki.ldt.parser.ast.visitor.MatchNodeVisitor;

/**
 * Retrieve {@link ASTNode}s under given source code position.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class LuaSelectionEngine extends ScriptSelectionEngine {

	/**
	 * Fetch {@link IModelElement} in an {@link IModuleSource} AST from source offset.
	 * 
	 * @param module
	 *            Browsed AST
	 * @param start
	 *            offset
	 * @param end
	 *            offset
	 * @return {@link IModelElement} representing definition of node at given offset
	 * @see org.eclipse.dltk.codeassist.ISelectionEngine#select(IModuleSource, int, int)
	 */
	@Override
	public IModelElement[] select(IModuleSource module, int start, int end) {
		// get the corresponding ISourceModule
		if (!(module instanceof ISourceModule))
			return new IModelElement[0];
		ISourceModule sourceModule = (ISourceModule) module;

		// we search the declaration of the selected object
		IModelElement result = null;
		ModuleDeclaration ast = SourceParserUtil.getModuleDeclaration(sourceModule);
		if (ast != null) {
			ASTNode node = findNodeAt(ast, start, end);
			if (node != null) {
				try {
					result = findDefinition(node, sourceModule);
				} catch (ModelException e) {
					Activator.logWarning("Unable to find definition for node:" + node, e); //$NON-NLS-1$
				}
			}
		}

		if (result == null)
			return null;

		return new IModelElement[] { result };
	}

	/**
	 * Fetch an {@link Declaration} of a {@link LuaModuleDeclaration} from source code offsets
	 * 
	 * @param ast
	 *            Syntax tree to browse
	 * @param start
	 *            offset in source code
	 * @param end
	 *            offset in source code
	 * @param sourceModule
	 * @return Declaration in LuaModuleDeclaration at given offsets
	 */
	public static Declaration findMinimalDeclaration(ModuleDeclaration ast, int start, int end) {
		MatchNodeVisitor visitor = new MatchNodeVisitor(start, end + 1);
		try {
			ast.traverse(visitor);
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			Activator.logWarning("Problem occured while seeking for minimal node.", e); //$NON-NLS-1$
		}
		ASTNode node = visitor.getNode();
		// if node is an identifier and it has a declaration get it.
		if (node instanceof Identifier) {
			Identifier id = (Identifier) node;
			if (id.hasDeclaration()) {
				return id.getDeclaration();
			}
		} else if (node instanceof Declaration) {
			return (Declaration) node;
		}
		return null;
	}

	/**
	 * Find Node corresponding to the current selection.
	 * 
	 * @param ast
	 *            Syntax tree to browse
	 * @param start
	 *            offset in source code
	 * @param end
	 *            offset in source code
	 * @return node found at the given offsets
	 */
	public static ASTNode findNodeAt(ModuleDeclaration ast, int start, int end) {
		MatchNodeVisitor visitor = new MatchNodeVisitor(start, end + 1);
		try {
			ast.traverse(visitor);
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			Activator.logWarning("Problem occured while seeking for minimal node.", e); //$NON-NLS-1$
		}
		ASTNode node = visitor.getNode();
		return node;
	}

	/**
	 * Find the definition of the current node in the given project
	 */
	public static IModelElement findDefinition(ASTNode node, ISourceModule module) throws ModelException {
		// try to find local definition
		ASTNode localDefinition = findLocalDefinition(node);
		if (localDefinition != null) {
			if (localDefinition instanceof ModuleReference) {
				String moduleNameReference = ((ModuleReference) localDefinition).getModuleNameReference();
				ISourceModule sourceModule = LuaUtils.getSourceModule(moduleNameReference, module.getScriptProject());
				if (sourceModule != null)
					return sourceModule;

			}
			try {
				return ((ISourceModule) module).getElementAt(localDefinition.sourceStart());
			} catch (ModelException e) {
				Activator.logWarning("Unable to get model element.", e); //$NON-NLS-1$
			}
		}

		// check for cross-module reference
		if (localDefinition == null) {
			if (node instanceof Index) {
				Index index = (Index) node;
				Expression root = index.getRoot();
				// find the local definition of root index
				ASTNode rootlocaldefinition = findLocalDefinition(root);
				// if local definition is a module Reference search the corresponding module
				if (rootlocaldefinition instanceof ModuleReference) {
					String moduleNameReference = ((ModuleReference) rootlocaldefinition).getModuleNameReference();
					ISourceModule sourceModule = LuaUtils.getSourceModule(moduleNameReference, module.getScriptProject());
					if (sourceModule != null) {
						Map<String, Declaration> moduleFields = LuaASTUtils.getModuleFields(sourceModule);
						Declaration declaration = moduleFields.get(index.getName());
						if (declaration != null) {
							try {
								return sourceModule.getElementAt(declaration.sourceStart());
							} catch (ModelException e) {
								Activator.logWarning("Unable to get model element.", e); //$NON-NLS-1$
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static ASTNode findLocalDefinition(ASTNode node) {
		// check if definition is local
		// if node is a declaration we already found the definition
		if (node instanceof Declaration) {
			return node;
		}
		// if the node is an identifier we search its local declaration
		else if (node instanceof Identifier) {
			Identifier identifier = ((Identifier) node);
			if (identifier.hasDeclaration()) {
				return identifier.getDeclaration();
			}
		}
		return null;
	}

}
