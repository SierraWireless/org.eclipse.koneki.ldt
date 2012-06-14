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

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date$
 * $Author$
 * $Id$
 */
package org.eclipse.koneki.ldt.core.tests.internal.ast;

import junit.framework.TestCase;

import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceElementParser;
import org.eclipse.koneki.ldt.core.internal.ast.parser.LuaSourceParser;

/**
 * The Class TestSourceElementRequestVisitor aims to provide a way to trace how AST from {@linkplain LuaSourceParser} behaves.
 */
public class TestSourceElementRequestVisitor extends TestCase {

	/**
	 * Test source element request visitor.
	 */
	public void testSourceElementRequestVisitor() {
		LuaSourceElementParser visitor = new LuaSourceElementParser();
		assertNotNull("Visitor from element parser is not defined.", visitor.createVisitor()); //$NON-NLS-1$
	}
}
