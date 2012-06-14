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
package org.eclipse.koneki.ldt.metalua.tests;

import org.eclipse.koneki.ldt.metalua.tests.internal.cases.TestMetalua;
import org.eclipse.koneki.ldt.metalua.tests.internal.cases.TestMetaluaStateFactory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

// CHECKSTYLE:OFF
@RunWith(Suite.class)
@SuiteClasses({ TestMetalua.class, TestMetaluaStateFactory.class })
public class AllMetaluaTests {
	public static final String PLUGIN_ID = "org.eclipse.koneki.ldt.metalua.tests"; //$NON-NLS-1$
}
// CHECKSTYLE:ON
