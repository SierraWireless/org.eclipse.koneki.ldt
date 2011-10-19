/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/

package org.eclipse.koneki.ldt.lua.tests;

import org.eclipse.koneki.ldt.lua.internal.tests.ConcurrencyTest;

import junit.framework.TestSuite;

public class Suite extends TestSuite {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.koneki.ldt.lua.tests"; //$NON-NLS-1$

	/** Registers all tests to run */
	public Suite() {
		super();
		setName("JNLua"); //$NON-NLS-1$
		addTestSuite(ConcurrencyTest.class);
	}
}
