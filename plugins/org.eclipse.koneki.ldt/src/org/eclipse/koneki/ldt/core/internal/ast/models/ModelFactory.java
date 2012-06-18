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

import org.eclipse.koneki.ldt.core.internal.ast.models.api.LuaFileAPI;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaSourceRoot;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LuaInternalContent;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public final class ModelFactory {

	private ModelFactory() {
	}

	/**
	 * register this java module in the given lua vm
	 */
	public static final void registerModelFactory(LuaState l) {
		NamedJavaFunction[] namedJavaFunctions = createFunctions();
		l.register("javamodelfactory", namedJavaFunctions); //$NON-NLS-1$
	}

	/* create all factory function which will be available in javamodelfactory module */
	private static NamedJavaFunction[] createFunctions() {
		List<NamedJavaFunction> javaFunctions = new ArrayList<NamedJavaFunction>();

		javaFunctions.add(newSourceRoot());
		javaFunctions.add(sourceRootSetProblem());
		javaFunctions.add(sourceRootAddContent());

		return javaFunctions.toArray(new NamedJavaFunction[javaFunctions.size()]);
	}

	private static NamedJavaFunction newSourceRoot() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				int length = l.checkInteger(1);

				LuaSourceRoot sourceRoot = new LuaSourceRoot(length, true);
				l.pushJavaObject(sourceRoot);

				return 1;
			}

			@Override
			public String getName() {
				return "newsourceroot"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction sourceRootSetProblem() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				LuaSourceRoot sourceRoot = l.checkJavaObject(1, LuaSourceRoot.class);
				int line = l.checkInteger(2);
				int column = l.checkInteger(3);
				int offset = l.checkInteger(4);
				String message = l.checkString(5);

				sourceRoot.setProblem(line, column, offset, message);

				return 0;
			}

			@Override
			public String getName() {
				return "setproblem"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction sourceRootAddContent() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				LuaSourceRoot sourceRoot = l.checkJavaObject(1, LuaSourceRoot.class);
				LuaFileAPI fileAPI = l.checkJavaObject(2, LuaFileAPI.class);
				LuaInternalContent internalContent = l.checkJavaObject(3, LuaInternalContent.class);

				sourceRoot.setLuaFileApi(fileAPI);
				sourceRoot.setInternalContent(internalContent);

				return 0;
			}

			@Override
			public String getName() {
				return "addcontent"; //$NON-NLS-1$
			}
		};
	}
}
