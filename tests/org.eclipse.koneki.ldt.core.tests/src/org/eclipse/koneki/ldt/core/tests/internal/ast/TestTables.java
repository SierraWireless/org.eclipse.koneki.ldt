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
package org.eclipse.koneki.ldt.core.tests.internal.ast;

import org.eclipse.koneki.ldt.core.tests.internal.ast.utils.AbstractParserTest;

/**
 * Module Parsing Tests
 */
public class TestTables extends AbstractParserTest {

	/**
	 * parse table
	 */
	public void testTableWithOneScalarField() {
		parse("local t = {} t.f1, t.f2 = 2,3"); //$NON-NLS-1$		
	}
}
