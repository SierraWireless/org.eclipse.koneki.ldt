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

/**
 * A general Lua table with arbitrary keys.
 */
public class LuaTableType extends LuaSequenceType {

	public LuaTableType() {
		super(LuaDebugConstant.TYPE_TABLE);
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.CollectionScriptType#buildDetailString(org.eclipse.debug.core.model.IVariable)
	 */
	@Override
	protected String buildDetailString(IVariable variable) throws DebugException {
		StringBuffer sb = new StringBuffer();

		sb.append(variable.getName());
		sb.append(" = "); //$NON-NLS-1$
		sb.append(variable.getValue().getValueString());

		return sb.toString();
	}

}
