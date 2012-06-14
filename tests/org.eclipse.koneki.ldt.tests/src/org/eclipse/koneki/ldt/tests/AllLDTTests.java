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
package org.eclipse.koneki.ldt.tests;

import org.eclipse.koneki.ldt.core.tests.AllCoreTests;
import org.eclipse.koneki.ldt.jnlua.tests.AllJNLuaTests;
import org.eclipse.koneki.ldt.metalua.tests.AllMetaluaTests;
import org.eclipse.koneki.ldt.ui.tests.AllUITests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AllJNLuaTests.class, AllMetaluaTests.class, AllCoreTests.class, AllUITests.class })
public class AllLDTTests {

}
