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
package org.eclipse.koneki.ldt.core.tests.internal.ast;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * TODO Comment this class
 */
@RunWith(Suite.class)
@SuiteClasses({ TestExpressions.class, TestLuaBinaryOperations.class, TestLuaSourceParser.class, TestModuleDeclaration.class,
		TestMultipleParsing.class, TestSourceElementRequestVisitor.class, TestStatements.class, TestTables.class, TestUnaryOperations.class,
		TestVisitor.class })
public class AllASTTests {

}
