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
package org.eclipse.koneki.ldt.debug.core.internal;

import java.net.URI;

import org.eclipse.dltk.debug.core.model.AtomicScriptType;
import org.eclipse.dltk.debug.core.model.IScriptValue;

/**
 * Represents a function defined in Lua and allows to retrieve its URI and file name.
 */
public class LuaFunctionType extends AtomicScriptType {
	public static final class FunctionData {
		private final String repr;
		private final URI path;
		private final int line;

		private FunctionData(IScriptValue value) {
			String[] lines = value.getRawValue().split("\\r?\\n"); //$NON-NLS-1$
			repr = lines[0];
			path = URI.create(lines[1]);
			line = Integer.valueOf(lines[2]);
		}

		public int getLine() {
			return line;
		}

		public URI getPath() {
			return path;
		}

		public String getRepr() {
			return repr;
		}
	}

	/**
	 * @param name
	 */
	public LuaFunctionType() {
		super(LuaDebugConstant.TYPE_LUAFUNC);
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.AtomicScriptType#formatDetails(org.eclipse.dltk.debug.core.model.IScriptValue)
	 */
	@Override
	public String formatDetails(IScriptValue value) {
		return value.getRawValue();
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.AtomicScriptType#formatValue(org.eclipse.dltk.debug.core.model.IScriptValue)
	 */
	@Override
	public String formatValue(IScriptValue value) {
		return value.getRawValue().split("\\r?\\n")[0]; //$NON-NLS-1$
	}

	public FunctionData getData(IScriptValue value) {
		return new FunctionData(value);
	}

}
