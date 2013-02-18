/*******************************************************************************
 * Copyright (c) 2013 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.core.internal.interpreter.generic;

import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.Info;
import org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.InterpreterPackage;

public final class LuaGenericInterpreterUtil {

	private LuaGenericInterpreterUtil() {
	}

	public static boolean interpreterHandlesExecuteOption(final IInterpreterInstall interpreter) {

		// Fetch interpreter option
		if (interpreter != null) {
			final Info info = getInfoFromInterpreter(interpreter);
			if (info != null)
				return info.isExecuteOptionCapable();
		}

		// Use default option value
		return (Boolean) InterpreterPackage.eINSTANCE.getInfo_ExecuteOptionCapable().getDefaultValue();
	}

	public static boolean interpreterHandlesFilesAsArgument(final IInterpreterInstall interpreter) {

		// Fetch interpreter option
		if (interpreter != null) {
			final Info info = getInfoFromInterpreter(interpreter);
			if (info != null)
				return info.isFileAsArgumentsCapable();
		}

		// Use default option value
		return (Boolean) InterpreterPackage.eINSTANCE.getInfo_FileAsArgumentsCapable().getDefaultValue();
	}

	private static Info getInfoFromInterpreter(final IInterpreterInstall interpreter) {
		for (final EObject extension : interpreter.getExtensions())
			if (extension instanceof Info)
				return (Info) extension;
		return null;
	}
}
