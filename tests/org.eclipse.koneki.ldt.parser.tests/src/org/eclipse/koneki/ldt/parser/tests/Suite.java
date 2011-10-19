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
package org.eclipse.koneki.ldt.parser.tests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.koneki.ldt.parser.LuaSourceParser;
import org.eclipse.koneki.ldt.parser.internal.tests.TestASTValidity;
import org.eclipse.koneki.ldt.parser.internal.tests.TestDeclarations;
import org.eclipse.koneki.ldt.parser.internal.tests.TestExpressions;
import org.eclipse.koneki.ldt.parser.internal.tests.TestLuaBinaryOperations;
import org.eclipse.koneki.ldt.parser.internal.tests.TestLuaSourceParser;
import org.eclipse.koneki.ldt.parser.internal.tests.TestModuleDeclaration;
import org.eclipse.koneki.ldt.parser.internal.tests.TestSourceElementRequestVisitor;
import org.eclipse.koneki.ldt.parser.internal.tests.TestStatements;
import org.eclipse.koneki.ldt.parser.internal.tests.TestUnaryOperations;
import org.eclipse.koneki.ldt.parser.internal.tests.TestVisitor;

/**
 * The Class Suite, groups all {@link TestCase} for {@link LuaSourceParser}
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class Suite extends TestSuite {

    /**
     * Instantiates a new suite registering all {@link TestCase} of the plug-in.
     * 
     */
    public Suite() {
	setName("Lua Source parser");
	addTestSuite(TestASTValidity.class);
	addTestSuite(TestDeclarations.class);
	addTestSuite(TestExpressions.class);
	addTestSuite(TestLuaBinaryOperations.class);
	addTestSuite(TestLuaSourceParser.class);
	addTestSuite(TestModuleDeclaration.class);
	addTestSuite(TestSourceElementRequestVisitor.class);
	addTestSuite(TestStatements.class);
	addTestSuite(TestUnaryOperations.class);
	addTestSuite(TestVisitor.class);
    }
}
