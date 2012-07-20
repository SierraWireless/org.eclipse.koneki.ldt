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
package org.eclipse.koneki.ldt.ui.tests;


import org.eclipse.koneki.ldt.ui.tests.internal.LuaWordFinderTest;
import org.eclipse.koneki.ldt.ui.tests.internal.autoedit.LuaDocumentorCommentAutoEditStrategyTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaDocMultLineCommentTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaDocSingleCommentSeriesRuleTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaMultLineCommentRuleTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaMultLineStringRuleTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaNumberRuleTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ LuaWordFinderTest.class, LuaNumberRuleTest.class, LuaMultLineCommentRuleTest.class, LuaMultLineStringRuleTest.class,
		LuaDocMultLineCommentTest.class, LuaDocSingleCommentSeriesRuleTest.class, LuaDocumentorCommentAutoEditStrategyTest.class })
public class AllUITests {

}
