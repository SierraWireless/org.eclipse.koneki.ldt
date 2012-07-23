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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.koneki.ldt.ui.tests.internal.LuaWordFinderTest;
import org.eclipse.koneki.ldt.ui.tests.internal.autoedit.LuaDocumentorCommentAutoEditStrategyTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaDocMultLineCommentTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaDocSingleCommentSeriesRuleTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaMultLineCommentRuleTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaMultLineStringRuleTest;
import org.eclipse.koneki.ldt.ui.tests.internal.rules.LuaNumberRuleTest;
import org.eclipse.koneki.ldt.ui.tests.internal.scanners.LuaCodeScannerTestSuite;
import org.eclipse.koneki.ldt.ui.tests.internal.scanners.LuaDocumentorScannerTestSuite;
import org.eclipse.koneki.ldt.ui.tests.internal.scanners.LuaPartitionScannerTestSuite;

public class AllUITests extends TestCase {

	public static Test suite() {
		final TestSuite suite = new TestSuite(AllUITests.class.getName());
		suite.addTest(new LuaPartitionScannerTestSuite());
		suite.addTest(new LuaCodeScannerTestSuite());
		suite.addTest(new LuaDocumentorScannerTestSuite());

		suite.addTest(new JUnit4TestAdapter(LuaNumberRuleTest.class));
		suite.addTest(new JUnit4TestAdapter(LuaMultLineStringRuleTest.class));
		suite.addTest(new JUnit4TestAdapter(LuaMultLineCommentRuleTest.class));
		suite.addTest(new JUnit4TestAdapter(LuaDocMultLineCommentTest.class));
		suite.addTest(new JUnit4TestAdapter(LuaDocSingleCommentSeriesRuleTest.class));

		suite.addTest(new JUnit4TestAdapter(LuaDocumentorCommentAutoEditStrategyTest.class));
		suite.addTest(new JUnit4TestAdapter(LuaWordFinderTest.class));
		return suite;
	}
}
