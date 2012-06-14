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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.koneki.ldt.core.LuaUtils;
import org.eclipse.koneki.ldt.core.internal.Activator;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.ExprTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.ExternalTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.FunctionTypeDef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.InternalTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Item;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.LuaFileAPI;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.ModuleTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.PrimitiveTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.RecordTypeDef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Return;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.TypeDef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.TypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaSourceRoot;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Block;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Call;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Identifier;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Index;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Invoke;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LocalVar;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LuaExpression;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LuaInternalContent;

public final class LuaASTUtils {
	private LuaASTUtils() {
	}

	private static class ClosestItemVisitor extends ASTVisitor {
		private Item result = null;
		private int position;

		private String identifierName;

		public ClosestItemVisitor(int position, String identifierName) {
			super();
			this.position = position;
			this.identifierName = identifierName;
		}

		public Item getResult() {
			return result;
		}

		@Override
		public boolean visit(ASTNode node) throws Exception {
			if (node instanceof LocalVar)
				return false;

			// we go down util we found the closer block.
			if (node instanceof Block) {
				if (node.sourceStart() <= position && position <= node.sourceEnd()) {
					return true;
				}
				return false;
			}
			return false;
		}

		@Override
		public boolean endvisit(ASTNode node) throws Exception {
			if (result == null && node instanceof Block) {
				// we go up only on the parent block
				List<LocalVar> localVars = ((Block) node).getLocalVars();
				for (LocalVar localVar : localVars) {
					Item item = localVar.getVar();
					if (item.getName().equals(identifierName)) {
						result = item;
					}
				}
				return true;
			}
			return false;
		}
	};

	public static Item getClosestLocalVar(final LuaSourceRoot luaSourceRoot, final String identifierName, final int position) {
		// traverse the root block on the file with this visitor
		try {
			ClosestItemVisitor closestItemVisitor = new ClosestItemVisitor(position, identifierName);
			luaSourceRoot.getInternalContent().getContent().traverse(closestItemVisitor);
			return closestItemVisitor.getResult();
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			Activator.logError("unable to collect local var", e); //$NON-NLS-1$
		}
		return null;
	}

	public static TypeResolution resolveType(ISourceModule sourceModule, TypeRef typeRef) {
		if (typeRef instanceof PrimitiveTypeRef)
			return null;

		if (typeRef instanceof InternalTypeRef) {
			return resolveType(sourceModule, (InternalTypeRef) typeRef);
		}

		if (typeRef instanceof ExternalTypeRef) {
			return resolveType(sourceModule, (ExternalTypeRef) typeRef);
		}

		if (typeRef instanceof ModuleTypeRef) {
			return resolveType(sourceModule, (ModuleTypeRef) typeRef);
		}

		if (typeRef instanceof ExprTypeRef) {
			return resolveType(sourceModule, (ExprTypeRef) typeRef);
		}

		return null;
	}

	public static TypeResolution resolveType(ISourceModule sourceModule, InternalTypeRef internalTypeRef) {
		LuaSourceRoot luaSourceRoot = LuaASTModelUtils.getLuaSourceRoot(sourceModule);
		TypeDef typeDef = luaSourceRoot.getFileapi().getTypes().get(internalTypeRef.getTypeName());
		return new TypeResolution(sourceModule, typeDef);
	}

	public static TypeResolution resolveType(ISourceModule sourceModule, ExternalTypeRef externalTypeRef) {
		ISourceModule externalSourceModule = LuaUtils.getSourceModule(externalTypeRef.getModuleName(), sourceModule.getScriptProject());
		if (externalSourceModule == null)
			return null;
		LuaSourceRoot luaSourceRoot = LuaASTModelUtils.getLuaSourceRoot(externalSourceModule);
		TypeDef typeDef = luaSourceRoot.getFileapi().getTypes().get(externalTypeRef.getTypeName());
		return new TypeResolution(externalSourceModule, typeDef);
	}

	public static TypeResolution resolveType(ISourceModule sourceModule, ModuleTypeRef moduleTypeRef) {
		ISourceModule referencedSourceModule = LuaUtils.getSourceModule(moduleTypeRef.getModuleName(), sourceModule.getScriptProject());
		if (referencedSourceModule == null)
			return null;

		LuaSourceRoot luaSourceRoot = LuaASTModelUtils.getLuaSourceRoot(referencedSourceModule);
		LuaFileAPI fileapi = luaSourceRoot.getFileapi();
		if (fileapi != null) {
			ArrayList<Return> returns = fileapi.getReturns();
			if (returns.size() > 0) {
				Return returnValues = returns.get(0);
				if (returnValues.getTypes().size() > moduleTypeRef.getReturnPosition() - 1) {
					TypeRef typeRef = returnValues.getTypes().get(moduleTypeRef.getReturnPosition() - 1);
					return resolveType(referencedSourceModule, typeRef);
				}
			}
		}
		return null;
	}

	public static TypeResolution resolveType(ISourceModule sourceModule, ExprTypeRef exprTypeRef) {
		LuaExpression expression = exprTypeRef.getExpression();
		if (expression == null)
			return null;

		return resolveType(sourceModule, expression, exprTypeRef.getReturnPosition());
	}

	public static TypeResolution resolveType(ISourceModule sourceModule, LuaExpression expr) {
		return resolveType(sourceModule, expr, 1);
	}

	public static TypeResolution resolveType(ISourceModule sourceModule, LuaExpression expr, int returnposition) {
		if (expr instanceof Identifier) {
			Definition definition = getDefinition(sourceModule, expr);
			// resolve the type of the definition
			if (definition == null || definition.getItem() == null || definition.getItem().getType() == null)
				return null;
			return resolveType(definition.getModule(), definition.getItem().getType());
		} else if (expr instanceof Index) {
			Index index = ((Index) expr);
			// resolve left part of the index
			LuaExpression left = index.getLeft();
			TypeResolution resolvedLeftType = resolveType(sourceModule, left);
			if (resolvedLeftType != null && resolvedLeftType.getTypeDef() instanceof RecordTypeDef) {
				RecordTypeDef recordtype = (RecordTypeDef) resolvedLeftType.getTypeDef();
				Item item = recordtype.getFields().get(index.getRight());
				if (item != null && item.getType() != null) {
					return resolveType(resolvedLeftType.getModule(), item.getType());
				}
			}
			return null;
		} else if (expr instanceof Call) {
			Call call = ((Call) expr);
			// resolve the function which is called
			TypeResolution resolvedFunctionType = resolveType(sourceModule, call.getFunction());
			if (resolvedFunctionType != null && resolvedFunctionType.getTypeDef() instanceof FunctionTypeDef) {
				FunctionTypeDef functiontype = (FunctionTypeDef) resolvedFunctionType.getTypeDef();
				if (functiontype.getReturns().size() > 0) {
					List<TypeRef> types = functiontype.getReturns().get(0).getTypes();
					if (types.size() >= returnposition) {
						return resolveType(resolvedFunctionType.getModule(), types.get(returnposition - 1));
					}
				}
			}
			return null;
		} else if (expr instanceof Invoke) {
			Invoke invoke = ((Invoke) expr);
			// resolve the function which is called
			TypeResolution resolvedRecordType = resolveType(sourceModule, invoke.getRecord());
			if (resolvedRecordType != null && resolvedRecordType.getTypeDef() instanceof RecordTypeDef) {
				RecordTypeDef recordtype = (RecordTypeDef) resolvedRecordType.getTypeDef();
				Item item = recordtype.getFields().get(invoke.getFunctionName());
				if (item != null && item.getType() != null) {
					TypeResolution resolvedFunctionType = resolveType(resolvedRecordType.getModule(), item.getType());
					if (resolvedFunctionType != null && resolvedFunctionType.getTypeDef() instanceof FunctionTypeDef) {
						FunctionTypeDef functiontype = (FunctionTypeDef) resolvedFunctionType.getTypeDef();
						if (functiontype.getReturns().size() > 0) {
							List<TypeRef> types = functiontype.getReturns().get(0).getTypes();
							if (types.size() >= returnposition) {
								return resolveType(resolvedFunctionType.getModule(), types.get(returnposition - 1));
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static class TypeResolution {
		private ISourceModule module;
		private TypeDef typeDef;

		public TypeResolution(ISourceModule module, TypeDef typeDef) {
			super();
			this.module = module;
			this.typeDef = typeDef;
		}

		public ISourceModule getModule() {
			return module;
		}

		public TypeDef getTypeDef() {
			return typeDef;
		}

	}

	public static Collection<Item> getLocalVars(LuaSourceRoot luaSourceRoot, final int offset, final String start) {
		// the localVars collected, indexed by var name;
		final Map<String, Item> collectedLocalVars = new HashMap<String, Item>();

		// the visitor which will collect local vars and store it in the map.
		ASTVisitor localvarCollector = new ASTVisitor() {
			@Override
			public boolean visit(ASTNode node) throws Exception {
				// we go down util we found the closer block.
				if (node instanceof Block) {
					if (node.sourceStart() <= offset && offset <= node.sourceEnd()) {
						return true;
					}
					return false;
				}
				return false;
			}

			@Override
			public boolean endvisit(ASTNode node) throws Exception {
				if (node instanceof Block) {
					// we go up only on all the parent block which
					List<LocalVar> localVars = ((Block) node).getLocalVars();
					for (LocalVar localVar : localVars) {
						Item item = localVar.getVar();
						if (!collectedLocalVars.containsKey(item.getName())
								&& (start == null || item.getName().toLowerCase().startsWith(start.toLowerCase()))) {
							collectedLocalVars.put(item.getName(), item);
						}
					}
					return true;
				}
				return false;
			}
		};

		// traverse the root block on the file with this visitor
		try {
			luaSourceRoot.getInternalContent().getContent().traverse(localvarCollector);
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			Activator.logError("unable to collect local var", e); //$NON-NLS-1$
		}

		return collectedLocalVars.values();
	}

	public static LuaExpression getLuaExpressionAt(LuaSourceRoot luaSourceRoot, final int startOffset, final int endOffset) {
		// traverse the root block on the file with this visitor
		try {
			MatchNodeVisitor matchNodeVisitor = new MatchNodeVisitor(startOffset, endOffset, LuaExpression.class);
			luaSourceRoot.getInternalContent().getContent().traverse(matchNodeVisitor);
			return (LuaExpression) matchNodeVisitor.getNode();

			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			Activator.logError("unable to get expression at", e); //$NON-NLS-1$
		}
		return null;
	}

	public static class Definition {
		private ISourceModule module;
		private Item item;

		public Definition(ISourceModule module, Item item) {
			super();
			this.module = module;
			this.item = item;
		}

		public ISourceModule getModule() {
			return module;
		}

		public Item getItem() {
			return item;
		}

	}

	public static Definition getDefinition(ISourceModule sourceModule, LuaExpression luaExpression) {
		if (luaExpression instanceof Identifier) {
			Identifier identifier = (Identifier) luaExpression;
			if (identifier.getDefinition() != null) {
				Item definition = identifier.getDefinition();
				if (LuaASTUtils.isUnresolvedGlobal(definition)) {
					// in this case we have a unknown global var definition.
					// we will try to resolved it
					Definition globalVarDefinition = getGlobalVarDefinition(sourceModule, definition.getName());
					if (globalVarDefinition != null)
						return globalVarDefinition;
				}
				return new Definition(sourceModule, definition);
			}
		} else if (luaExpression instanceof Index) {
			Index index = (Index) luaExpression;
			TypeResolution resolveType = LuaASTUtils.resolveType(sourceModule, index.getLeft());
			if (resolveType != null && resolveType.getTypeDef() instanceof RecordTypeDef) {
				RecordTypeDef typeDef = (RecordTypeDef) resolveType.getTypeDef();
				Item definition = typeDef.getFields().get(index.getRight());
				return new Definition(resolveType.getModule(), definition);
			}

		} else if (luaExpression instanceof Invoke) {
			Invoke invoke = (Invoke) luaExpression;
			TypeResolution resolveType = LuaASTUtils.resolveType(sourceModule, invoke.getRecord());
			if (resolveType != null && resolveType.getTypeDef() instanceof RecordTypeDef) {
				RecordTypeDef typeDef = (RecordTypeDef) resolveType.getTypeDef();
				Item definition = typeDef.getFields().get(invoke.getFunctionName());
				return new Definition(resolveType.getModule(), definition);
			}
		} else if (luaExpression instanceof Call) {
			Call call = (Call) luaExpression;
			Definition definition = getDefinition(sourceModule, call.getFunction());
			return definition;
		}
		return null;
	}

	public static List<Definition> getAllGlobalVarsDefinition(ISourceModule sourceModule, String start) {
		// get preloaded module
		ISourceModule preloadedSourceModule = getPreloadSourceModule(sourceModule);
		if (preloadedSourceModule == null)
			return Collections.emptyList();

		// get luasourceroot
		LuaSourceRoot luaSourceRoot = LuaASTModelUtils.getLuaSourceRoot(preloadedSourceModule);
		if (luaSourceRoot == null)
			return Collections.emptyList();

		// get a global var with this name
		final List<Definition> definitions = new ArrayList<Definition>();
		for (Item globalvar : luaSourceRoot.getFileapi().getGlobalvars().values()) {
			if (start == null || start.isEmpty() || globalvar.getName().toLowerCase().startsWith(start.toLowerCase()))
				definitions.add(new Definition(preloadedSourceModule, globalvar));
		}

		return definitions;
	}

	public static Definition getGlobalVarDefinition(ISourceModule sourceModule, String varname) {
		// get preloaded module
		ISourceModule preloadedSourceModule = getPreloadSourceModule(sourceModule);
		if (preloadedSourceModule == null)
			return null;

		// get luasourceroot
		LuaSourceRoot luaSourceRoot = LuaASTModelUtils.getLuaSourceRoot(preloadedSourceModule);
		if (luaSourceRoot == null)
			return null;

		// get a global var with this name
		Item item = luaSourceRoot.getFileapi().getGlobalvars().get(varname);
		if (item == null)
			return null;

		return new Definition(preloadedSourceModule, item);
	}

	public static ISourceModule getPreloadSourceModule(ISourceModule sourceModule) {
		if (sourceModule != null && sourceModule.getScriptProject() != null) {
			return LuaUtils.getSourceModule("global", sourceModule.getScriptProject()); //$NON-NLS-1$
		}
		return null;
	}

	public static boolean isLocal(Item item) {
		return item.getParent() instanceof Block;
	}

	public static boolean isGlobal(Item item) {
		return item.getParent() instanceof LuaFileAPI;
	}

	public static boolean isTypeField(Item item) {
		return item.getParent() instanceof RecordTypeDef;
	}

	public static boolean isModuleTypeField(LuaFileAPI luaFileAPI, Item item) {
		LuaASTNode parent = item.getParent();
		if (parent instanceof RecordTypeDef) {
			return isModule(luaFileAPI, (RecordTypeDef) parent);
		}
		return false;
	}

	public static boolean isUnresolvedGlobal(Item item) {
		return item.getParent() instanceof LuaInternalContent;
	}

	public static TypeDef resolveTypeLocaly(LuaFileAPI luaFileAPI, Item item) {
		if (luaFileAPI == null)
			return null;

		TypeRef typeref = item.getType();
		if (!(typeref instanceof InternalTypeRef))
			return null;

		InternalTypeRef internaltyperef = (InternalTypeRef) typeref;
		TypeDef typeDef = luaFileAPI.getTypes().get(internaltyperef.getTypeName());

		return typeDef;
	}

	public static boolean isModule(LuaFileAPI luaFileAPI, RecordTypeDef recordTypeDef) {
		TypeRef moduleReturnTypeRef = getModuleReturnType(luaFileAPI);

		if (!(moduleReturnTypeRef instanceof InternalTypeRef))
			return false;
		String typename = ((InternalTypeRef) moduleReturnTypeRef).getTypeName();

		return luaFileAPI.getTypes().get(typename) == recordTypeDef;
	}

	public static TypeRef getModuleReturnType(LuaFileAPI luaFileAPI) {
		ArrayList<Return> returns = luaFileAPI.getReturns();
		if (returns.isEmpty())
			return null;

		Return returnValues = returns.get(0);
		if (returnValues.getTypes().isEmpty())
			return null;

		return returnValues.getTypes().get(0);
	}
}
