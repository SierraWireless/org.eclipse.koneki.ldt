/*******************************************************************************
 * Copyright (c) 2009, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *     Marc-André Laperle - bug 369557, 363501
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.editor.text;

import org.eclipse.dltk.compiler.task.ITodoTaskPreferences;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptCommentScanner;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.dltk.ui.text.completion.ContentAssistProcessor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.koneki.ldt.core.LuaConstants;
import org.eclipse.koneki.ldt.ui.internal.editor.completion.LuaCompletionProcessor;
import org.eclipse.koneki.ldt.ui.internal.editor.completion.LuaContentAssistPreference;
import org.eclipse.ui.texteditor.ITextEditor;

public class LuaSourceViewerConfiguration extends ScriptSourceViewerConfiguration {

	// CHECKSTYLE:OFF
	// we declare abstract type because of DLTK architecture
	private AbstractScriptScanner fCodeScanner;

	private AbstractScriptScanner fStringScanner;
	private AbstractScriptScanner fSingleQuoteStringScanner;
	private AbstractScriptScanner fMultilineStringScanner;

	private AbstractScriptScanner fCommentScanner;

	private AbstractScriptScanner fLuaDocScanner;
	private AbstractScriptScanner fMultilineCommentScanner;

	// CHECKSTYLE:ON

	public LuaSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		return new LuaDoubleClickSelector();
	}

	protected void alterContentAssistant(ContentAssistant assistant) {
		IContentAssistProcessor scriptProcessor = new LuaCompletionProcessor(getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(scriptProcessor, IDocument.DEFAULT_CONTENT_TYPE);

		ContentAssistProcessor luaDocumentorProcessor = new LuaCompletionProcessor(getEditor(), assistant, ILuaPartitions.LUA_DOC);
		assistant.setContentAssistProcessor(luaDocumentorProcessor, ILuaPartitions.LUA_DOC);
		assistant.setContentAssistProcessor(luaDocumentorProcessor, ILuaPartitions.LUA_DOC_MULTI);
	}

	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		if (ILuaPartitions.LUA_DOC.equals(contentType)) {
			return new IAutoEditStrategy[] { new LuaDocumentorCommentAutoEditStrategy() };
		}

		return new IAutoEditStrategy[] { new DefaultIndentLineAutoEditStrategy() };
	}

	@Override
	protected ContentAssistPreference getContentAssistPreference() {
		return LuaContentAssistPreference.getDefault();
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new ScriptPresentationReconciler();
		reconciler.setDocumentPartitioning(this.getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr;
		// code
		dr = new DefaultDamagerRepairer(this.fCodeScanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		// strings
		dr = new DefaultDamagerRepairer(this.fStringScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_STRING);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_STRING);

		dr = new DefaultDamagerRepairer(this.fSingleQuoteStringScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_SINGLE_QUOTE_STRING);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_SINGLE_QUOTE_STRING);

		dr = new DefaultDamagerRepairer(this.fMultilineStringScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_MULTI_LINE_STRING);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_MULTI_LINE_STRING);

		// comments
		dr = new DefaultDamagerRepairer(this.fMultilineCommentScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_MULTI_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(this.fCommentScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_COMMENT);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_COMMENT);

		// luadocs
		dr = new DefaultDamagerRepairer(this.fLuaDocScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_DOC_MULTI);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_DOC_MULTI);

		dr = new DefaultDamagerRepairer(this.fLuaDocScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_DOC);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_DOC);

		return reconciler;
	}

	/**
	 * This method is called from base class.
	 */
	protected void initializeScanners() {
		// This is our code scanner
		this.fCodeScanner = new LuaCodeScanner(this.getColorManager(), this.fPreferenceStore);

		// This is default scanners for String partitions.
		this.fStringScanner = new SingleTokenScriptScanner(this.getColorManager(), this.fPreferenceStore, ILuaColorConstants.LUA_STRING);
		this.fSingleQuoteStringScanner = new SingleTokenScriptScanner(this.getColorManager(), this.fPreferenceStore, ILuaColorConstants.LUA_STRING);
		this.fMultilineStringScanner = new SingleTokenScriptScanner(this.getColorManager(), this.fPreferenceStore, ILuaColorConstants.LUA_STRING);

		// This is default scanners for comments partitions.
		this.fMultilineCommentScanner = createCommentScanner(ILuaColorConstants.LUA_MULTI_LINE_COMMENT, ILuaColorConstants.COMMENT_TASK_TAGS);
		this.fCommentScanner = createCommentScanner(ILuaColorConstants.LUA_SINGLE_LINE_COMMENT, ILuaColorConstants.COMMENT_TASK_TAGS);

		// This is scanner for LuaDoc partitions.
		this.fLuaDocScanner = new LuaDocumentorScanner(this);
	}

	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		if (this.fCodeScanner.affectsBehavior(event)) {
			this.fCodeScanner.adaptToPreferenceChange(event);
		}
		if (this.fStringScanner.affectsBehavior(event)) {
			this.fStringScanner.adaptToPreferenceChange(event);
		}
		if (this.fSingleQuoteStringScanner.affectsBehavior(event)) {
			this.fSingleQuoteStringScanner.adaptToPreferenceChange(event);
		}
		if (this.fMultilineStringScanner.affectsBehavior(event)) {
			this.fMultilineStringScanner.adaptToPreferenceChange(event);
		}
		if (this.fMultilineCommentScanner.affectsBehavior(event)) {
			this.fMultilineCommentScanner.adaptToPreferenceChange(event);
		}
		if (this.fCommentScanner.affectsBehavior(event)) {
			this.fCommentScanner.adaptToPreferenceChange(event);
		}
		if (this.fLuaDocScanner.affectsBehavior(event)) {
			this.fLuaDocScanner.adaptToPreferenceChange(event);
		}
	}

	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		boolean affectCode = this.fCodeScanner.affectsBehavior(event);

		boolean affectString = this.fStringScanner.affectsBehavior(event) || this.fSingleQuoteStringScanner.affectsBehavior(event)
				|| this.fMultilineStringScanner.affectsBehavior(event);

		boolean affectComments = this.fCommentScanner.affectsBehavior(event) || this.fMultilineCommentScanner.affectsBehavior(event);

		boolean affectLuaDoc = this.fLuaDocScanner.affectsBehavior(event);

		return affectCode || affectString || affectComments || affectLuaDoc;
	}

	/**
	 * Lua specific one line comment
	 * 
	 * @see ScriptSourceViewerConfiguration#getCommentPrefix()
	 */
	@Override
	protected String getCommentPrefix() {
		return LuaConstants.COMMENT_STRING;
	}

	@Override
	public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer) {
		return ILuaPartitions.LUA_PARTITION_TYPES;
	}

	// CHECKSTYLE:OFF
	// we declare abstract type because of DLTK architecture
	protected AbstractScriptScanner createCommentScanner(String commentColor, String tagColor, ITodoTaskPreferences taskPrefs) {
		// CHECKSTYLE:ON
		return new ScriptCommentScanner(getColorManager(), fPreferenceStore, commentColor, tagColor, taskPrefs) {

			@Override
			protected int skipCommentChars() {
				if (read() == '-') {
					if (read() == '-') {
						return 2;
					} else {
						unread();
						unread();
						return 0;
					}
				} else {
					unread();
					return 0;
				}
			}
		};
	}
}
