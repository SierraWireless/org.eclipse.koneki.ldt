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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.koneki.ldt.core.LuaUtils;
import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;
import org.eclipse.koneki.ldt.parser.ast.declarations.LuaModuleDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.ModuleReference;
import org.eclipse.koneki.ldt.parser.ast.visitor.ModuleReferenceVisitor;
import org.eclipse.koneki.ldt.parser.ast.visitor.ScopeVisitor;

/**
 * TODO Comment this class
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public final class LuaASTUtils {
	private LuaASTUtils() {
	}

	private static boolean isModule(int flags) {
		return (flags & Flags.AccModule) != 0;
	}

	public static boolean isModule(IMember member) throws ModelException {
		return member instanceof IType && isModule(member.getFlags());
	}

	public static boolean isModuleFunction(IMember member) throws ModelException {
		return member instanceof IMethod && isModule(member.getFlags());
	}

	public static boolean isGlobalTable(IMember member) throws ModelException {
		return member instanceof IType && Flags.isPublic(member.getFlags());
	}

	public static boolean isLocalTable(IMember member) throws ModelException {
		return member instanceof IType && Flags.isPrivate(member.getFlags());
	}

	public static ASTNode getClosestScope(ModuleDeclaration ast, int sourcePosition) throws Exception {
		ScopeVisitor visitor = new ScopeVisitor(sourcePosition);
		ast.traverse(visitor);
		return visitor.getScope();
	}

	public static ModuleReference getModuleReferenceFromName(final ModuleDeclaration ast, final String lhsName) {
		final ModuleReferenceVisitor visitor = new ModuleReferenceVisitor(lhsName);
		try {
			ast.traverse(visitor);
			return visitor.getModuleReference();
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			return null;
		}
	}

	public static Map<String, Declaration> getModuleFields(ISourceModule module) throws ModelException {
		HashMap<String, Declaration> result = new HashMap<String, Declaration>();
		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(module);
		if (moduleDeclaration instanceof LuaSourceRoot) {
			LuaModuleDeclaration luaModuleDeclaration = ((LuaSourceRoot) moduleDeclaration).getDeclarationsContainer().getLuaModuleDeclaration();
			if (luaModuleDeclaration != null)
				for (MethodDeclaration declaration : luaModuleDeclaration.getMethods()) {
					result.put(declaration.getName(), declaration);
				}
		}
		return result;
	}

	public static Map<String, Declaration> getModuleFields(String name, IScriptProject project) throws ModelException {
		IModuleSource moduleSource = LuaUtils.getModuleSource(name, project);
		if (moduleSource instanceof ISourceModule) {
			return getModuleFields((ISourceModule) moduleSource);
		}
		return new HashMap<String, Declaration>();
	}

	public static boolean isAncestor(IModelElement element, IModelElement ancestor) {
		return ancestor != null && element != null && (ancestor.equals(element.getParent()) || isAncestor(element.getParent(), ancestor));
	}

}
