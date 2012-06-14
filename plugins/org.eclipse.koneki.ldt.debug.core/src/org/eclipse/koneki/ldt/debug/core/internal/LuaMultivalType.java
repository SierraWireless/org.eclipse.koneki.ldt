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
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.dltk.debug.core.model.CollectionScriptType;
import org.eclipse.dltk.debug.core.model.IScriptValue;

/**
 * Represents a "multival" type in Lua like function return values, ...
 */
public class LuaMultivalType extends CollectionScriptType {
	public LuaMultivalType() {
		super(LuaDebugConstant.TYPE_MULTIVAL);
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.CollectionScriptType#formatDetails(org.eclipse.dltk.debug.core.model.IScriptValue)
	 */
	@Override
	public String formatDetails(IScriptValue value) {
		final StringBuffer sb = new StringBuffer();

		try {
			for (IVariable var : value.getVariables()) {
				sb.append(buildDetailString(var));
				sb.append('\t');
			}
			if (sb.length() > 0) {
				sb.setLength(sb.length() - 1);
			}
		} catch (DebugException ex) {
			Activator.logError("Building details string", ex); //$NON-NLS-1$
		}

		return sb.toString();
	}
}
