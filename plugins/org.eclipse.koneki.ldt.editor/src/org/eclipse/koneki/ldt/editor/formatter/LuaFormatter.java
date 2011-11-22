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
package org.eclipse.koneki.ldt.editor.formatter;

import java.util.Map;

import org.eclipse.dltk.formatter.AbstractScriptFormatter;
import org.eclipse.dltk.ui.formatter.FormatterException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.koneki.ldt.parser.format.LuaSourceFormat;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

public class LuaFormatter extends AbstractScriptFormatter {
	final static String ID = "org.eclipse.koneki.ldt.formatter"; //$NON-NLS-1$
	final private String tabPolicy;
	final private int tabSize;
	final private int indentationSize;
	final private String delimiter;
	final private String tabulation;

	protected LuaFormatter(final String lineDelimiter, final Map<String, String> preferences) {
		super(preferences);
		delimiter = lineDelimiter;
		/*
		 * Get formatting constants from preferences
		 */
		tabPolicy = preferences.get(LuaFormatterPreferenceConstants.FORMATTER_TAB_CHAR);
		String string = preferences.get(LuaFormatterPreferenceConstants.FORMATTER_TAB_SIZE);
		tabSize = string == null || string.isEmpty() ? 0 : Integer.parseInt(string);
		string = preferences.get(LuaFormatterPreferenceConstants.FORMATTER_INDENTATION_SIZE);
		indentationSize = string == null || string.isEmpty() ? 0 : Integer.parseInt(string);
		/*
		 * Build separator character
		 */
		// Concatenate spaces
		if ("space".equals(tabPolicy)) { //$NON-NLS-1$
			final char space = ' ';
			// Create tabulation
			final StringBuilder sb = new StringBuilder(tabSize);
			for (int k = 0; k < tabSize; k++) {
				sb.append(space);
			}
			tabulation = sb.toString();
		} else {
			// Use single tabulation
			tabulation = "\t"; //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.dltk.ui.formatter.IScriptFormatter#format(String, int, int, int)
	 */
	@Override
	public TextEdit format(String source, int offset, int length, int indentationLevel) throws FormatterException {
		/*
		 * Format given source code
		 */
		final String formatted;
		// With mixed white spaces
		if ("mixed".equals(tabPolicy)) { //$NON-NLS-1$
			formatted = LuaSourceFormat.indent(source, delimiter, tabSize, indentationSize, 0);
		} else {
			// With one type of tabulation
			formatted = LuaSourceFormat.indent(source, delimiter, tabulation, 0);
		}
		if (length < source.length()) {
			final Document doc = new Document(source);
			try {
				// Get line change range form original source
				final int startLine = doc.getLineOfOffset(offset);
				final int endLine = doc.getLineOfOffset(offset + length);

				/*
				 * Get source code from those lines in formatted code
				 */
				final StringBuffer code = new StringBuffer();
				final Document formattedDoc = new Document(formatted);
				int lengthToReplace = 0;
				for (int line = startLine; line <= endLine; line++) {
					// Sum selected text length
					lengthToReplace += doc.getLineLength(line);

					// Retrieve formatted code
					final IRegion lineDescription = formattedDoc.getLineInformation(line);
					final String codeOnTheLine = formattedDoc.get(lineDescription.getOffset(), lineDescription.getLength());
					code.append(codeOnTheLine);

					// Append new line when needed
					final String lineEnd = formattedDoc.getLineDelimiter(line);
					if (lineEnd != null) {
						code.append(lineEnd);
					}
				}
				final int selectionStartOffset = doc.getLineOffset(startLine);
				return new ReplaceEdit(selectionStartOffset, lengthToReplace, code.toString());
			} catch (BadLocationException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.LuaFormatterErrorWhileFormattingTitle,
						Messages.LuaFormatterUnableToFormatSelection);
				Activator.logError(Messages.LuaFormatterUnableToFormatSelection, e);
			}
		}
		// Construct text edition
		return new ReplaceEdit(offset, length, formatted);
	}

	@Override
	public int detectIndentationLevel(final IDocument document, final int offset) {
		return LuaSourceFormat.depth(document.get(), offset);
	}
}
