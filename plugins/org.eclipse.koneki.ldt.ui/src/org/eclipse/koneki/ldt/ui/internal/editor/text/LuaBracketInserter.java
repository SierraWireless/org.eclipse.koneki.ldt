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
package org.eclipse.koneki.ldt.ui.internal.editor.text;

import java.text.MessageFormat;

import org.eclipse.dltk.internal.ui.editor.BracketInserter;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.koneki.ldt.core.internal.Activator;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;

/**
 * Auto insert convenient closing characters during typing, close open strings or brackets.
 */
public class LuaBracketInserter extends BracketInserter {

	/** <strong>Must</strong> contain auto insert related preferences */
	private IPreferenceStore preferences;

	/**
	 * Will apply auto insert policy defined from preference page
	 * 
	 * @param editor
	 *            Where auto insert will be performed
	 * @param preferenceStore
	 *            <strong>Must</strong> contain auto insert related preferences
	 */
	public LuaBracketInserter(final ScriptEditor editor, final IPreferenceStore preferenceStore) {
		super(editor);
		preferences = preferenceStore;
	}

	/**
	 * Will apply or ignore auto insertion depending on typed character and preferences from {@link #getPreferenceStore()}
	 * 
	 * @see BracketInserter#verifyKey(VerifyEvent)
	 */
	@Override
	public void verifyKey(final VerifyEvent event) {
		// Early pruning to slow down normal typing as little as possible
		if (!event.doit || this.editor.getInsertMode() != ScriptEditor.SMART_INSERT)
			return;
		// Do not process if character is not handled or if auto insert has been disabled from preference
		switch (event.character) {
		case '(':
			if (!isClosingBrackets()) {
				return;
			}
			break;
		case '[':
			if (!isClosingBrackets()) {
				return;
			}
			break;
		case '{':
			if (!isClosingBraces()) {
				return;
			}
			break;
		case '\'':
			if (!isClosingStrings()) {
				return;
			}
			break;
		case '\"':
			if (!isClosingStrings()) {
				return;
			}
			break;
		default:
			return;
		}

		if (!this.editor.validateEditorInputState())
			return;
		/**
		 * Retrieve insertion information
		 */
		final ISourceViewer sourceViewer = this.editor.getScriptSourceViewer();
		final IDocument document = sourceViewer.getDocument();
		final Point selection = sourceViewer.getSelectedRange();
		final int offset = selection.x;
		final int length = selection.y;
		try {
			if (!validatePartitioning(document, offset, ILuaPartitions.LUA_PARTITIONING)) {
				return;
			}
			insertBrackets(document, offset, length, event.character, getPeerCharacter(event.character));
		} catch (BadLocationException e) {
			Activator.logWarning(MessageFormat.format("Problem when trying to do autoclose for char {0}", event.character), e); //$NON-NLS-1$
			return;
		} catch (BadPositionCategoryException e) {
			Activator.logWarning(MessageFormat.format("Problem when trying to do autoclose for char {0}", event.character), e); //$NON-NLS-1$
			return;
		}
		event.doit = false;
	}

	private IPreferenceStore getPreferenceStore() {
		return preferences;
	}

	/**
	 * Indicates if brackets should be auto closed from <strong>preferences</strong>. Is not affected by {@link #setCloseBracketsEnabled(boolean)} .
	 * 
	 * @return <code>true</code> if preference indicates that brackets should be auto closed
	 */
	private boolean isClosingBrackets() {
		return getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_CLOSE_BRACKETS);
	}

	/**
	 * Indicates if brackets should be auto closed from <strong>preferences</strong>. Is not affected by {@link #setCloseStringsEnabled(boolean)} .
	 * 
	 * @return <code>true</code> if preference indicates that strings should be auto closed
	 */
	private boolean isClosingStrings() {
		return getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_CLOSE_STRINGS);
	}

	/**
	 * Indicates if braces should be auto closed from <strong>preferences</strong>. Is not affected by
	 * {@link #setCloseAngularBracketsEnabled(boolean)} .
	 * 
	 * @return <code>true</code> if preference indicates that braces should be auto closed
	 */
	private boolean isClosingBraces() {
		return getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_CLOSE_BRACES);
	}
}
