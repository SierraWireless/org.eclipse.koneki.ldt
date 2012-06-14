/*******************************************************************************
 * Copyright (c) 2011, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.core.internal;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

public class LuaVariableComparator implements Comparator<IVariable>, Serializable {
	private static final long serialVersionUID = -5828968181211469862L;

	public int category(IVariable var) throws DebugException {
		return var.getReferenceTypeName().equals(LuaDebugConstant.TYPE_SPECIAL) ? 0 : 1;
	}

	@Override
	public int compare(IVariable v1, IVariable v2) {
		try {
			int cat1 = category(v1);
			int cat2 = category(v2);

			if (cat1 != cat2) {
				return cat1 - cat2;
			}

			return v1.getName().compareTo(v2.getName());
		} catch (DebugException e) {
			return 0;
		}
	}

}
