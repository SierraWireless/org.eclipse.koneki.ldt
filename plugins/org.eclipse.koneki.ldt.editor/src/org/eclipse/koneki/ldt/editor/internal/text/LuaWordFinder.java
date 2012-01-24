/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Marc-Andre Laperle - Adapted to LDT from DLTK's ScriptWordFinder
 *******************************************************************************/
package org.eclipse.koneki.ldt.editor.internal.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public final class LuaWordFinder {

	private LuaWordFinder() {
	}

	public static IRegion findWord(IDocument document, int offset) {
		int start = -2;
		int end = -1;

		try {
			start = findWordStart(document, offset);
			end = findWordEnd(document, offset);
		} catch (BadLocationException x) {
			return null;
		}

		if (start >= -1 && end > -1) {
			if (start == offset && end == offset)
				return new Region(offset, 0);
			else if (start == offset)
				return new Region(start, end - start);
			else
				return new Region(start + 1, end - start - 1);
		}

		return null;
	}

	private static int findWordEnd(IDocument document, int offset) throws BadLocationException {
		int length = document.getLength();
		int pos = offset;
		while (pos < length) {
			char c = document.getChar(pos);
			if (!Character.isJavaIdentifierPart(c))
				break;
			++pos;
		}
		return pos;
	}

	private static int findWordStart(IDocument document, int offset) throws BadLocationException {
		int pos = offset;
		while (pos >= 0 && offset < document.getLength()) {
			char c = document.getChar(pos);
			if (!Character.isJavaIdentifierPart(c)) {
				break;
			}
			--pos;
		}
		return pos;
	}
}
