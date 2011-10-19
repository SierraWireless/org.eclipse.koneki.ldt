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
package org.eclipse.koneki.ldt.parser.internal.modules.tests;

import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;
import org.eclipse.koneki.ldt.parser.ast.declarations.ModuleReference;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.AbstractParserTest;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.CollectVisitor;

/**
 * Module Reference Parsing Tests
 */
public class TestModuleReference extends AbstractParserTest {

	/**
	 * parse module with local module reference
	 */
	public void testOneLocalModuleReference() throws Exception {
		LuaSourceRoot module = parse("local m = require('module')"); //$NON-NLS-1$
		ModuleReference[] references = getReferences(module);
		assertEquals("module reference not found.", 1, references.length); //$NON-NLS-1$
		assertEquals("bad reference.", references[0].getModuleNameReference(), "module"); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * parse module with global module reference
	 */
	public void testOneGlobalModuleReference() throws Exception {
		LuaSourceRoot module = parse("m = require('module')"); //$NON-NLS-1$
		ModuleReference[] references = getReferences(module);
		assertEquals("module reference not found.", 1, references.length); //$NON-NLS-1$
		assertEquals("bad reference.", references[0].getModuleNameReference(), "module"); //$NON-NLS-1$//$NON-NLS-2$
	}

	private ModuleReference[] getReferences(LuaSourceRoot module) throws Exception {
		CollectVisitor collectVisitor = new CollectVisitor();
		module.traverse(collectVisitor);
		ModuleReference[] references = collectVisitor.getASTNode(ModuleReference.class);
		return references;
	}
}
