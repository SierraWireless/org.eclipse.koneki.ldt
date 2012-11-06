/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.lua.tests.internal.template.engine;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.koneki.ldt.lua.tests.internal.utils.AbstractLuaTestSuite;

/**
 * Template Engine tests. The template engine is responsible to generate HTML This test start from lua files, generate associed HTML documentation
 * witch is compared to an HTML reference
 */
public class TemplateEngineTestSuite extends AbstractLuaTestSuite {

	public TemplateEngineTestSuite(boolean ignore) {
		super("Template Engine", "tests/templateengine/", "html", ignore); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	protected List<String> createTestBlacklist() {
		ArrayList<String> blacklist = new ArrayList<String>();

		// Bug 389828
		blacklist.add("markdown/code2.lua"); //$NON-NLS-1$

		// Bug 389991
		blacklist.add("markdown/title2.lua"); //$NON-NLS-1$

		return blacklist;
	}

}
