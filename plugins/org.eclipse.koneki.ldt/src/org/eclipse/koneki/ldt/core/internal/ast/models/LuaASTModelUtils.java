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
package org.eclipse.koneki.ldt.core.internal.ast.models;

import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.koneki.ldt.core.internal.Activator;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Item;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.LuaFileAPI;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.RecordTypeDef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.TypeDef;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaSourceRoot;
import org.eclipse.koneki.ldt.core.internal.ast.models.dltk.FakeField;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LocalVar;

public final class LuaASTModelUtils {
	private LuaASTModelUtils() {
	}

	/**
	 * Get LuaSourceRoot from ISourceModule <br/>
	 * 
	 * DLTK Model => AST
	 */
	public static LuaSourceRoot getLuaSourceRoot(ISourceModule module) {
		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(module);
		if (moduleDeclaration instanceof LuaSourceRoot)
			return (LuaSourceRoot) moduleDeclaration;
		return null;
	}

	/**
	 * Get LuaSourceRoot from ISourceModule <br/>
	 * 
	 * DLTK Model => AST
	 */
	public static ASTNode getASTNode(final IModelElement modelElement) {
		if (modelElement instanceof ISourceModule)
			return getLuaSourceRoot((ISourceModule) modelElement);
		if (modelElement instanceof IType)
			return getTypeDef((IType) modelElement);
		if (modelElement instanceof IField)
			return getItem((IField) modelElement);
		if (modelElement instanceof IMethod)
			return getItem((IMethod) modelElement);
		return null;
	}

	/**
	 * Get Record type def from {@link ISourceModule} <br/>
	 * 
	 * DLTK Model => AST
	 */
	public static TypeDef getTypeDef(IType type) {
		LuaSourceRoot luaSourceRoot = getLuaSourceRoot(type.getSourceModule());
		LuaFileAPI fileapi = luaSourceRoot.getFileapi();
		return fileapi.getTypes().get(type.getElementName());
	}

	/**
	 * Get Item from {@link IField} <br/>
	 * 
	 * DLTK Model => AST
	 */
	public static Item getItem(IField field) {
		IModelElement parent = field.getParent();
		if (parent instanceof IType) {
			RecordTypeDef typeDef = (RecordTypeDef) getTypeDef((IType) parent);
			return typeDef.getFields().get(field.getElementName());
		} else if (parent instanceof ISourceModule) {
			LuaSourceRoot luaSourceRoot = getLuaSourceRoot((ISourceModule) parent);
			try {
				if (Flags.isPrivate(field.getFlags())) {
					List<LocalVar> localVars = luaSourceRoot.getInternalContent().getContent().getLocalVars();
					for (LocalVar localVar : localVars) {
						if (localVar.getVar().getName().equals(field.getElementName()))
							return localVar.getVar();
					}
				} else {

					return luaSourceRoot.getFileapi().getGlobalvars().get(field.getElementName());
				}
			} catch (ModelException e) {
				Activator.logError("unable to get item from field " + field, e); //$NON-NLS-1$
				return null;
			}
		}
		return null;
	}

	/**
	 * Get Item from IMethod <br/>
	 * 
	 * DLTK Model => AST
	 */
	public static Item getItem(IMethod method) {
		IModelElement parent = method.getParent();
		if (parent instanceof IType) {
			RecordTypeDef typeDef = (RecordTypeDef) getTypeDef((IType) parent);
			return typeDef.getFields().get(method.getElementName());
		} else if (parent instanceof ISourceModule) {
			LuaSourceRoot luaSourceRoot = getLuaSourceRoot((ISourceModule) parent);
			try {
				if (Flags.isPrivate(method.getFlags())) {
					List<LocalVar> localVars = luaSourceRoot.getInternalContent().getContent().getLocalVars();
					for (LocalVar localVar : localVars) {
						if (localVar.getVar().getName().equals(method.getElementName()))
							return localVar.getVar();
					}
				} else {

					return luaSourceRoot.getFileapi().getGlobalvars().get(method.getElementName());
				}
			} catch (ModelException e) {
				Activator.logError("unable to get item from method " + method, e); //$NON-NLS-1$
				return null;
			}
		}
		return null;
	}

	/**
	 * Get IType from RecordTypeDef <br/>
	 * 
	 * AST => DLTK Model
	 */
	public static IType getIType(ISourceModule module, RecordTypeDef recordtypeDef) {
		IType type = module.getType(recordtypeDef.getName());
		return type;
	}

	/**
	 * Get IMember from Item <br/>
	 * 
	 * AST => DLTK Model
	 */
	public static IMember getIMember(ISourceModule sourceModule, Item item) {
		LuaASTNode parent = item.getParent();
		if (LuaASTUtils.isTypeField(item)) {
			// support record field
			IType iType = getIType(sourceModule, (RecordTypeDef) parent);
			if (iType != null) {
				try {
					for (IModelElement child : iType.getChildren()) {
						if (child.getElementName().equals(item.getName()) && child instanceof IMember)
							return (IMember) child;
					}
				} catch (ModelException e) {
					Activator.logWarning("unable to get IMember corresponding to the given item " + item, e); //$NON-NLS-1$
				}
			}
		} else if (LuaASTUtils.isLocal(item)) {
			// TODO retrieve local var which are in the model (so the local var in the first block)
			// support local variable
			return new FakeField(sourceModule, item.getName(), item.sourceStart(), item.getName().length() + 1, Declaration.AccPrivate);
		} else if (LuaASTUtils.isGlobal(item)) {
			// support global var
			try {
				for (IModelElement child : sourceModule.getChildren()) {
					if (child.getElementName().equals(item.getName()) && child instanceof IMember)
						return (IMember) child;
				}
			} catch (ModelException e) {
				Activator.logWarning("unable to get IMember corresponding to the given item " + item, e); //$NON-NLS-1$
			}
		}

		return null;
	}

	/**
	 * Get IModelElement from ASTNode <br/>
	 * 
	 * AST => DLTK Model
	 */
	public static IModelElement getIModelElement(ISourceModule sourcemodule, LuaASTNode astNode) {
		if (astNode instanceof RecordTypeDef) {
			return getIType(sourcemodule, (RecordTypeDef) astNode);
		} else if (astNode instanceof Item) {
			return getIMember(sourcemodule, (Item) astNode);
		}
		return null;
	}
}
