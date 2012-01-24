/*******************************************************************************
 * Copyright (c) 2009, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *     Marc-André Laperle - bug 369557
 *******************************************************************************/

package org.eclipse.koneki.ldt.editor.internal.text;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.koneki.ldt.editor.completion.LuaCompletionProcessor;
import org.eclipse.koneki.ldt.parser.LuaConstants;
import org.eclipse.ui.texteditor.ITextEditor;

public class LuaSourceViewerConfiguration extends ScriptSourceViewerConfiguration {

	private AbstractScriptScanner fCodeScanner;
	private AbstractScriptScanner fStringScanner;
	private AbstractScriptScanner fSingleQuoteStringScanner;
	private AbstractScriptScanner fCommentScanner;
	private AbstractScriptScanner fMultilineCommentScanner;
	private AbstractScriptScanner fNumberScanner;

	public LuaSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

	protected void alterContentAssistant(ContentAssistant assistant) {
		IContentAssistProcessor scriptProcessor = new LuaCompletionProcessor(getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(scriptProcessor, IDocument.DEFAULT_CONTENT_TYPE);
	}

	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
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
		dr = new DefaultDamagerRepairer(this.fCodeScanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(this.fStringScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_STRING);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_STRING);

		dr = new DefaultDamagerRepairer(this.fSingleQuoteStringScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_SINGLE_QUOTE_STRING);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_SINGLE_QUOTE_STRING);

		dr = new DefaultDamagerRepairer(this.fMultilineCommentScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_MULTI_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(this.fCommentScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_COMMENT);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_COMMENT);

		dr = new DefaultDamagerRepairer(this.fNumberScanner);
		reconciler.setDamager(dr, ILuaPartitions.LUA_NUMBER);
		reconciler.setRepairer(dr, ILuaPartitions.LUA_NUMBER);

		return reconciler;
	}

	/**
	 * This method is called from base class.
	 */
	protected void initializeScanners() {
		// This is our code scanner
		this.fCodeScanner = new LuaCodeScanner(this.getColorManager(), this.fPreferenceStore);

		// This is default scanners for partitions with same color.
		this.fStringScanner = new SingleTokenScriptScanner(this.getColorManager(), this.fPreferenceStore, ILuaColorConstants.LUA_STRING);
		this.fSingleQuoteStringScanner = new SingleTokenScriptScanner(this.getColorManager(), this.fPreferenceStore, ILuaColorConstants.LUA_STRING);
		this.fMultilineCommentScanner = new SingleTokenScriptScanner(this.getColorManager(), this.fPreferenceStore,
				ILuaColorConstants.LUA_MULTI_LINE_COMMENT);
		this.fCommentScanner = new SingleTokenScriptScanner(this.getColorManager(), this.fPreferenceStore, ILuaColorConstants.LUA_SINGLE_LINE_COMMENT);
		this.fNumberScanner = new SingleTokenScriptScanner(this.getColorManager(), this.fPreferenceStore, ILuaColorConstants.LUA_NUMBER);
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
	}

	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return this.fCodeScanner.affectsBehavior(event) || this.fStringScanner.affectsBehavior(event)
				|| this.fSingleQuoteStringScanner.affectsBehavior(event);
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
}
