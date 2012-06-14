/*******************************************************************************
 * Copyright (c) 2012 Marc-Andre Laperle and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marc-Andre Laperle - initial API and implementation
 *******************************************************************************/

package org.eclipse.koneki.ldt.ui.tests.internal;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaWordFinder;

/**
 * Tests for LuaWordFinder.
 */
public class LuaWordFinderTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBasic() {
		IDocument doc = new Document(" foo:bar(arg1, arg2)"); //$NON-NLS-1$
		// | foo:bar(arg1, arg2)
		IRegion region = LuaWordFinder.findWord(doc, 0);
		assertEquals(0, region.getOffset());
		assertEquals(0, region.getLength());

		// |foo:bar(arg1, arg2)
		region = LuaWordFinder.findWord(doc, 1);
		assertEquals(1, region.getOffset());
		assertEquals(3, region.getLength());

		// fo|o:bar(arg1, arg2)
		region = LuaWordFinder.findWord(doc, 3);
		assertEquals(1, region.getOffset());
		assertEquals(3, region.getLength());

		// foo|:bar(arg1, arg2)
		region = LuaWordFinder.findWord(doc, 4);
		assertEquals(4, region.getOffset());
		assertEquals(0, region.getLength());

		// foo:|bar(arg1, arg2)
		region = LuaWordFinder.findWord(doc, 5);
		assertEquals(5, region.getOffset());
		assertEquals(3, region.getLength());

		// foo:bar|(arg1, arg2)
		region = LuaWordFinder.findWord(doc, 8);
		assertEquals(8, region.getOffset());
		assertEquals(0, region.getLength());

		// foo:bar(|arg1, arg2)
		region = LuaWordFinder.findWord(doc, 9);
		assertEquals(9, region.getOffset());
		assertEquals(4, region.getLength());

		// foo:bar(arg1|, arg2)
		region = LuaWordFinder.findWord(doc, 13);
		assertEquals(13, region.getOffset());
		assertEquals(0, region.getLength());

		// foo:bar(arg1, |arg2)
		region = LuaWordFinder.findWord(doc, 15);
		assertEquals(15, region.getOffset());
		assertEquals(4, region.getLength());
	}

	public void testWordAtEnd() {
		IDocument doc = new Document("    foo"); //$NON-NLS-1$
		// |foo
		IRegion region = LuaWordFinder.findWord(doc, 4);
		assertEquals(4, region.getOffset());
		assertEquals(3, region.getLength());

		// fo|o
		region = LuaWordFinder.findWord(doc, 6);
		assertEquals(4, region.getOffset());
		assertEquals(3, region.getLength());

		// foo|
		region = LuaWordFinder.findWord(doc, 7);
		assertEquals(7, region.getOffset());
		assertEquals(0, region.getLength());
	}

	public void testWordAtStart() {
		IDocument doc = new Document("foo  "); //$NON-NLS-1$
		// |foo
		IRegion region = LuaWordFinder.findWord(doc, 0);
		assertEquals(0, region.getOffset());
		assertEquals(3, region.getLength());

		// f|oo
		region = LuaWordFinder.findWord(doc, 1);
		assertEquals(0, region.getOffset());
		assertEquals(3, region.getLength());
	}

}
