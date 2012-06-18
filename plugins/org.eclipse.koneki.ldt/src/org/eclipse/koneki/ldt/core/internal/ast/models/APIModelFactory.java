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
import java.util.List;

import org.eclipse.koneki.ldt.core.internal.ast.models.api.ExprTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.ExternalTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.FunctionTypeDef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.InternalTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Item;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.LuaFileAPI;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.ModuleTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Parameter;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.PrimitiveTypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.RecordTypeDef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Return;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.TypeDef;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.TypeRef;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Identifier;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LuaExpression;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public final class APIModelFactory {

	private APIModelFactory() {
	}

	/**
	 * register this java module in the given lua vm
	 */
	public static final void registerAPIModelFactory(LuaState l) {
		NamedJavaFunction[] namedJavaFunctions = createFunctions();
		l.register("javaapimodelfactory", namedJavaFunctions); //$NON-NLS-1$
	}

	/* create all factory function which will be available in javaapimodelfactory module */
	private static NamedJavaFunction[] createFunctions() {
		List<NamedJavaFunction> javaFunctions = new ArrayList<NamedJavaFunction>();

		javaFunctions.add(newItem());
		javaFunctions.add(itemAddOccurrence());
		javaFunctions.add(newExternalTypeRef());
		javaFunctions.add(newInternalTypeRef());
		javaFunctions.add(newModuleTypeRef());
		javaFunctions.add(newExprTypeRef());
		javaFunctions.add(newPrimitiveTypeRef());
		javaFunctions.add(itemSetExpression());
		javaFunctions.add(newRecordTypeDef());
		javaFunctions.add(recordTypeDefAddField());
		javaFunctions.add(newFunctionTypeDef());
		javaFunctions.add(functionAddParam());
		javaFunctions.add(functionAddReturn());
		javaFunctions.add(newReturn());
		javaFunctions.add(returnAddTypeRef());
		javaFunctions.add(newLuaFileAPI());
		javaFunctions.add(fileAPIAddGlobalVar());
		javaFunctions.add(fileAPIAddReturn());
		javaFunctions.add(fileAPIAddTypeDef());

		return javaFunctions.toArray(new NamedJavaFunction[javaFunctions.size()]);
	}

	private static NamedJavaFunction newItem() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				String name = l.checkString(1);
				String documentation = l.checkString(2);
				int sourceRangeMin = l.checkInteger(3);
				int sourceRangeMax = l.checkInteger(4);
				TypeRef type = l.checkJavaObject(5, TypeRef.class, null);

				Item item = new Item();
				item.setName(name);
				item.setDocumentation(documentation);
				item.setStart(sourceRangeMin);
				item.setEnd(sourceRangeMax);
				if (type != null)
					item.setType(type);

				l.pushJavaObject(item);

				return 1;
			}

			@Override
			public String getName() {
				return "newitem"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction itemAddOccurrence() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				Item item = l.checkJavaObject(1, Item.class);
				Identifier identifier = l.checkJavaObject(2, Identifier.class);

				item.addOccurrence(identifier);
				return 0;
			}

			@Override
			public String getName() {
				return "addoccurrence"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newExternalTypeRef() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				String modulename = l.checkString(1);
				String typename = l.checkString(2);

				ExternalTypeRef typeref = new ExternalTypeRef(modulename, typename);
				l.pushJavaObject(typeref);

				return 1;
			}

			@Override
			public String getName() {
				return "newexternaltyperef"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newInternalTypeRef() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				String typename = l.checkString(1);

				InternalTypeRef typeref = new InternalTypeRef(typename);
				l.pushJavaObject(typeref);

				return 1;
			}

			@Override
			public String getName() {
				return "newinternaltyperef"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newModuleTypeRef() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				String typename = l.checkString(1);
				int returnposition = l.checkInteger(2);

				ModuleTypeRef typeref = new ModuleTypeRef(typename, returnposition);
				l.pushJavaObject(typeref);

				return 1;
			}

			@Override
			public String getName() {
				return "newmoduletyperef"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newExprTypeRef() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				int returnposition = l.checkInteger(1);

				ExprTypeRef typeref = new ExprTypeRef(returnposition);
				l.pushJavaObject(typeref);

				return 1;
			}

			@Override
			public String getName() {
				return "newexprtyperef"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newPrimitiveTypeRef() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				String typename = l.checkString(1);

				PrimitiveTypeRef typeref = new PrimitiveTypeRef(typename);
				l.pushJavaObject(typeref);

				return 1;
			}

			@Override
			public String getName() {
				return "newprimitivetyperef"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction itemSetExpression() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				Item item = l.checkJavaObject(1, Item.class);
				LuaExpression expr = l.checkJavaObject(2, LuaExpression.class);

				((ExprTypeRef) item.getType()).setExpression(expr);
				return 0;
			}

			@Override
			public String getName() {
				return "setexpression"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newRecordTypeDef() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				String name = l.checkString(1);
				String documentation = l.checkString(2);
				int sourceRangeMin = l.checkInteger(3);
				int sourceRangeMax = l.checkInteger(4);

				RecordTypeDef record = new RecordTypeDef();
				record.setName(name);
				record.setDocumentation(documentation);
				record.setStart(sourceRangeMin);
				record.setEnd(sourceRangeMax);

				l.pushJavaObject(record);

				return 1;
			}

			@Override
			public String getName() {
				return "newrecordtypedef"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction recordTypeDefAddField() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				RecordTypeDef record = l.checkJavaObject(1, RecordTypeDef.class);
				Item item = l.checkJavaObject(2, Item.class);

				record.addField(item);
				return 0;
			}

			@Override
			public String getName() {
				return "addfield"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newFunctionTypeDef() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				l.pushJavaObject(new FunctionTypeDef());

				return 1;
			}

			@Override
			public String getName() {
				return "newfunctiontypedef"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction functionAddParam() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				FunctionTypeDef function = l.checkJavaObject(1, FunctionTypeDef.class);
				String paramName = l.checkString(2);
				TypeRef type = l.checkJavaObject(3, TypeRef.class);
				String description = l.checkString(4);

				function.addParameter(new Parameter(paramName, type, description));

				return 0;
			}

			@Override
			public String getName() {
				return "addparam"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newReturn() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {

				l.pushJavaObject(new Return());
				return 1;
			}

			@Override
			public String getName() {
				return "newreturn"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction returnAddTypeRef() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				Return ret = l.checkJavaObject(1, Return.class);
				TypeRef typeRef = l.checkJavaObject(2, TypeRef.class);

				ret.addType(typeRef);

				return 0;
			}

			@Override
			public String getName() {
				return "addtype"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction functionAddReturn() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				FunctionTypeDef function = l.checkJavaObject(1, FunctionTypeDef.class);
				Return ret = l.checkJavaObject(2, Return.class);

				function.addReturn(ret);

				return 0;
			}

			@Override
			public String getName() {
				return "functionaddreturn"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newLuaFileAPI() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				String documentation = l.checkString(1);

				LuaFileAPI luafileAPI = new LuaFileAPI();
				luafileAPI.setDocumentation(documentation);

				l.pushJavaObject(luafileAPI);
				return 1;
			}

			@Override
			public String getName() {
				return "newfileapi"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction fileAPIAddGlobalVar() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				LuaFileAPI luaFileAPI = l.checkJavaObject(1, LuaFileAPI.class);
				Item item = l.checkJavaObject(2, Item.class);

				luaFileAPI.addGlobalVar(item);

				return 0;
			}

			@Override
			public String getName() {
				return "addglobalvar"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction fileAPIAddTypeDef() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				LuaFileAPI luaFileAPI = l.checkJavaObject(1, LuaFileAPI.class);
				String typeName = l.checkString(2);
				TypeDef typedef = l.checkJavaObject(3, TypeDef.class);

				luaFileAPI.addType(typeName, typedef);

				return 0;
			}

			@Override
			public String getName() {
				return "addtypedef"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction fileAPIAddReturn() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				LuaFileAPI luaFileAPI = l.checkJavaObject(1, LuaFileAPI.class);
				Return ret = l.checkJavaObject(2, Return.class);

				luaFileAPI.addReturns(ret);

				return 0;
			}

			@Override
			public String getName() {
				return "fileapiaddreturn"; //$NON-NLS-1$
			}
		};
	}
}
