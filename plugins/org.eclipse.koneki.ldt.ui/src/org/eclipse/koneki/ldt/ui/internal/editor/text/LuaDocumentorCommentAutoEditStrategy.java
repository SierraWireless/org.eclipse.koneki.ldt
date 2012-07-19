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
package org.eclipse.koneki.ldt.ui.internal.editor.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.koneki.ldt.ui.internal.Activator;

public class LuaDocumentorCommentAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {
	private Pattern linePortionToDuplicatePattern = Pattern.compile("(\\s*).*--(\\s*)"); //$NON-NLS-1$

	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		if (c.length == 0 && c.text != null && TextUtilities.endsWith(d.getLegalLineDelimiters(), c.text) != -1) {
			// 'RETURN' has been pressed, with no text selected
			try {
				IRegion line = d.getLineInformationOfOffset(c.offset);
				String lineContent = d.get(line.getOffset(), line.getLength());
				Matcher m = linePortionToDuplicatePattern.matcher(lineContent);
				if (m.find()) {
					String toAppend = m.group(1) + "--" + m.group(2); //$NON-NLS-1$
					int start = c.offset - line.getOffset();
					// we want to append the exact same " --         "-like stuff that has been found
					// on the line where "RETURN" has been pressed, but no further than the caret position we where at
					toAppend = toAppend.substring(0, Math.min(start, toAppend.length()));
					c.text += toAppend;
				}
			} catch (BadLocationException e) {
				// should really not happen but...
				Activator.logWarning("Auto-edit failed", e); //$NON-NLS-1$
			}
		}
	}
}
