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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IIndexedValue;
import org.eclipse.dltk.debug.core.model.CollectionScriptType;
import org.eclipse.dltk.debug.core.model.IScriptValue;

/**
 * Represents a special case of Lua table. A sequence is a table with only 1..n consecutive keys. It is identified as "sequcence" type by Lua
 * debugger.
 */
public class LuaSequenceType extends CollectionScriptType {

	protected LuaSequenceType(String name) {
		super(name);
	}

	public LuaSequenceType() {
		this(LuaDebugConstant.TYPE_SEQUENCE);
	}

	public String formatValue(IScriptValue value) {
		StringBuffer sb = new StringBuffer();

		sb.append(getName());

		try {
			int size;
			// TODO BUG ECLIPSE TOOLSLINUX-99 352826
			if (value instanceof IIndexedValue) {
				// getting size directly can be munch faster when available
				size = ((IIndexedValue) value).getSize();
			} else {
				size = value.getVariables().length;
			}

			sb.append("[" + size + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (DebugException e) {
			sb.append("[]"); //$NON-NLS-1$
		}

		appendInstanceId(value, sb);

		return sb.toString();
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.CollectionScriptType#getOpenBrace()
	 */
	@Override
	protected char getOpenBrace() {
		return '{';
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.CollectionScriptType#getCloseBrace()
	 */
	@Override
	protected char getCloseBrace() {
		return '}';
	}
}
