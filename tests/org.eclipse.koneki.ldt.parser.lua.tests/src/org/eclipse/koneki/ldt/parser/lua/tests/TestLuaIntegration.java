/******************************************************************************
 * Copyright (c) 2011 Sierra Wireless.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 *          - initial API and implementation and initial documentation
 *****************************************************************************/
package org.eclipse.koneki.ldt.parser.lua.tests;

import junit.framework.TestSuite;

import org.eclipse.koneki.ldt.parser.lua.tests.internal.TestIndex;

/**
 * Gathering tests on Lua-side
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 * 
 */
public class TestLuaIntegration extends TestSuite {
	public TestLuaIntegration() {
		super();
		setName("Lua-side integration"); //$NON-NLS-1$
		addTestSuite(TestIndex.class);
	}
}
