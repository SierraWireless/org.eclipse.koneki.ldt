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

package org.eclipse.koneki.ldt.metalua.tests;

import junit.framework.TestSuite;

import org.eclipse.koneki.ldt.metalua.tests.internal.cases.TestMetalua;
import org.eclipse.koneki.ldt.metalua.tests.internal.cases.TestMetaluaStateFactory;

/** Gathers tests about Metalua */
public class Suite extends TestSuite {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.koneki.ldt.metalua.tests"; //$NON-NLS-1$

	/** Registers all tests to run */
	public Suite() {
		super();
		addTestSuite(TestMetaluaStateFactory.class);
		addTestSuite(TestMetalua.class);
		setName("Metalua"); //$NON-NLS-1$
	}
}
