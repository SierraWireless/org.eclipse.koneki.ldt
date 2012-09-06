/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.ui.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.dltk.core.IProjectFragment;

public class LuaDebugPropertyTester extends PropertyTester {

	static final String IS_LAUNCHABLEP_PROJECT_FRAG_PROPERTY = "isLaunchableProjectFragment"; //$NON-NLS-1$

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (IS_LAUNCHABLEP_PROJECT_FRAG_PROPERTY.equals(property) && receiver instanceof IProjectFragment) {
			IProjectFragment projectFragment = (IProjectFragment) receiver;
			return !projectFragment.isArchive() && !projectFragment.isBinary() && !projectFragment.isExternal() && !projectFragment.isBuiltin();
		}
		return false;
	}
}
