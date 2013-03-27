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
package org.eclipse.koneki.ldt.lua.tests.internal.ast.models.internal;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.koneki.ldt.lua.tests.internal.utils.AbstractLuaTestSuite;
import org.eclipse.koneki.ldt.lua.tests.internal.utils.LuaTestCase;

public class InternalModelTestSuite extends AbstractLuaTestSuite {

	public InternalModelTestSuite(boolean ignore) {
		super("internalmodel", "tests/internalmodel", "serialized.lua", ignore); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	protected TestCase createTestCase(final String testName, final String testModuleName, final IPath source, final IPath ref, final List<String> path) {
		return new LuaTestCase(testName, testModuleName, source, ref, path);
	}

}
