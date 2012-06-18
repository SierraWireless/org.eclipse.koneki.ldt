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

import org.eclipse.koneki.ldt.core.internal.ast.models.api.Item;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Block;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Call;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Identifier;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Index;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Invoke;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LocalVar;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LuaExpression;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LuaInternalContent;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public final class InternalModelFactory {

	private InternalModelFactory() {
	}

	/**
	 * register this java module in the given lua vm
	 */
	public static final void registerInternalModelFactory(LuaState l) {
		NamedJavaFunction[] namedJavaFunctions = createFunctions();
		l.register("javainternalmodelfactory", namedJavaFunctions); //$NON-NLS-1$
	}

	/* create all factory function which will be available in javainternalmodelfactory module */
	private static NamedJavaFunction[] createFunctions() {
		List<NamedJavaFunction> javaFunctions = new ArrayList<NamedJavaFunction>();

		javaFunctions.add(newInternalContent());
		javaFunctions.add(newIdentifier());
		javaFunctions.add(newIndex());
		javaFunctions.add(newCall());
		javaFunctions.add(newInvoke());
		javaFunctions.add(newBlock());
		javaFunctions.add(newLocalVar());
		javaFunctions.add(blockAddContent());
		javaFunctions.add(blockAddLocalVar());
		javaFunctions.add(intenalContentAddUnknownGlobalVar());

		return javaFunctions.toArray(new NamedJavaFunction[javaFunctions.size()]);
	}

	private static NamedJavaFunction newInternalContent() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				Block block = l.checkJavaObject(1, Block.class);

				LuaInternalContent content = new LuaInternalContent();
				content.setContent(block);

				l.pushJavaObject(content);
				return 1;
			}

			@Override
			public String getName() {
				return "newinternalmodel"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newIdentifier() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				int sourceRangeMin = l.checkInteger(1);
				int sourceRangeMax = l.checkInteger(2);

				Identifier identifier = new Identifier();
				identifier.setStart(sourceRangeMin);
				identifier.setEnd(sourceRangeMax);

				l.pushJavaObject(identifier);
				return 1;
			}

			@Override
			public String getName() {
				return "newidentifier"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newIndex() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				int sourceRangeMin = l.checkInteger(1);
				int sourceRangeMax = l.checkInteger(2);
				LuaExpression left = l.checkJavaObject(3, LuaExpression.class);
				String right = l.checkString(4);

				Index index = new Index();
				index.setStart(sourceRangeMin);
				index.setEnd(sourceRangeMax);
				index.setLeft(left);
				index.setRight(right);

				l.pushJavaObject(index);

				return 1;
			}

			@Override
			public String getName() {
				return "newindex"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newCall() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				int sourceRangeMin = l.checkInteger(1);
				int sourceRangeMax = l.checkInteger(2);
				LuaExpression function = l.checkJavaObject(3, LuaExpression.class);

				Call call = new Call();
				call.setStart(sourceRangeMin);
				call.setEnd(sourceRangeMax);
				call.setFunction(function);

				l.pushJavaObject(call);

				return 1;
			}

			@Override
			public String getName() {
				return "newcall"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newInvoke() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				int sourceRangeMin = l.checkInteger(1);
				int sourceRangeMax = l.checkInteger(2);
				String functionName = l.checkString(3);
				LuaExpression record = l.checkJavaObject(4, LuaExpression.class);

				Invoke invoke = new Invoke();
				invoke.setStart(sourceRangeMin);
				invoke.setEnd(sourceRangeMax);
				invoke.setFunctionName(functionName);
				invoke.setRecord(record);

				l.pushJavaObject(invoke);

				return 1;
			}

			@Override
			public String getName() {
				return "newinvoke"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newBlock() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				int sourceRangeMin = l.checkInteger(1);
				int sourceRangeMax = l.checkInteger(2);

				Block block = new Block();
				block.setStart(sourceRangeMin);
				block.setEnd(sourceRangeMax);

				l.pushJavaObject(block);

				return 1;
			}

			@Override
			public String getName() {
				return "newblock"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction blockAddContent() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				Block block = l.checkJavaObject(1, Block.class);
				LuaASTNode node = l.checkJavaObject(2, LuaASTNode.class);

				block.addContent(node);

				return 0;
			}

			@Override
			public String getName() {
				return "addcontent"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction newLocalVar() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				Item item = l.checkJavaObject(1, Item.class);
				int sourceRangeMin = l.checkInteger(2);
				int sourceRangeMax = l.checkInteger(3);

				LocalVar localVar = new LocalVar(item, sourceRangeMin, sourceRangeMax);
				l.pushJavaObject(localVar);

				return 1;
			}

			@Override
			public String getName() {
				return "newlocalvar"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction blockAddLocalVar() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				Block block = l.checkJavaObject(1, Block.class);
				LocalVar var = l.checkJavaObject(2, LocalVar.class);

				block.addLocalVar(var);

				return 0;
			}

			@Override
			public String getName() {
				return "addlocalvar"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction intenalContentAddUnknownGlobalVar() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				LuaInternalContent internalContent = l.checkJavaObject(1, LuaInternalContent.class);
				Item item = l.checkJavaObject(2, Item.class);

				internalContent.addUnknownGlobalVar(item);

				return 0;
			}

			@Override
			public String getName() {
				return "addunknownglobalvar"; //$NON-NLS-1$
			}
		};
	}
}
