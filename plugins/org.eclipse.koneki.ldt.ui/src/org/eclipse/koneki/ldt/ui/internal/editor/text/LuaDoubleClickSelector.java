/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     QNX Software System
 *     Anton Leherbauer (Wind River Systems)
 *     Marc-Andre Laperle - Adapted to LDT
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.editor.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;

public class LuaDoubleClickSelector implements ITextDoubleClickStrategy {

	private static char[] fgBrackets = { '{', '}', '(', ')', '[', ']' };

	private DefaultCharacterPairMatcher fPairMatcher = new DefaultCharacterPairMatcher(fgBrackets);

	public LuaDoubleClickSelector() {
		super();
	}

	/*
	 * @see org.eclipse.jface.text.ITextDoubleClickStrategy#doubleClicked(org.eclipse.jface.text.ITextViewer)
	 */
	@Override
	public void doubleClicked(ITextViewer textViewer) {
		int offset = textViewer.getSelectedRange().x;

		if (offset < 0)
			return;

		IDocument document = textViewer.getDocument();

		IRegion region = fPairMatcher.match(document, offset);
		if (region != null && region.getLength() >= 2) {
			textViewer.setSelectedRange(region.getOffset() + 1, region.getLength() - 2);
		} else {
			region = LuaWordFinder.findWord(document, offset);
			if (region != null && region.getLength() > 0) {
				textViewer.setSelectedRange(region.getOffset(), region.getLength());
			}
		}
	}

}
