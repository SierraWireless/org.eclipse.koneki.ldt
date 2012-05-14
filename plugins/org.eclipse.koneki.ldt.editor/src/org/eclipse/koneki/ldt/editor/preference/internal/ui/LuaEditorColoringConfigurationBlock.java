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
package org.eclipse.koneki.ldt.editor.preference.internal.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.preferences.AbstractScriptEditorColoringConfigurationBlock;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.koneki.ldt.editor.internal.text.ILuaColorConstants;
import org.eclipse.koneki.ldt.editor.internal.text.ILuaPartitions;
import org.eclipse.koneki.ldt.editor.internal.text.LuaTextTools;
import org.eclipse.koneki.ldt.editor.templates.SimpleLuaSourceViewerConfiguration;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditor;

public class LuaEditorColoringConfigurationBlock extends AbstractScriptEditorColoringConfigurationBlock implements IPreferenceConfigurationBlock {

	private static final String PREVIEW_FILE_NAME = "sample/formatter.lua"; //$NON-NLS-1$

	private static final String[][] fSyntaxColorListModel = new String[][] {
			{ PreferencesMessages.DLTKEditorPreferencePage_singleLineComment, ILuaColorConstants.LUA_SINGLE_LINE_COMMENT, sCommentsCategory },
			{ Messages.LuaEditorColoringConfigurationBlock_multiLineComment, ILuaColorConstants.LUA_MULTI_LINE_COMMENT, sCommentsCategory },
			{ PreferencesMessages.DLTKEditorPreferencePage_keywords, ILuaColorConstants.LUA_KEYWORD, sCoreCategory },
			{ PreferencesMessages.DLTKEditorPreferencePage_strings, ILuaColorConstants.LUA_STRING, sCoreCategory },
			{ PreferencesMessages.DLTKEditorPreferencePage_others, ILuaColorConstants.LUA_DEFAULT, sCoreCategory },
			{ PreferencesMessages.DLTKEditorPreferencePage_numbers, ILuaColorConstants.LUA_NUMBER, sCoreCategory },
			{ Messages.LuaEditorColoringConfigurationBlock_localVariable, ILuaColorConstants.LUA_LOCAL_VARIABLE, sCoreCategory },
			{ Messages.LuaEditorColoringConfigurationBlock_globalVariable, ILuaColorConstants.LUA_GLOBAL_VARIABLE, sCoreCategory },
			{ PreferencesMessages.DLTKEditorPreferencePage_CommentTaskTags, ILuaColorConstants.COMMENT_TASK_TAGS, sCommentsCategory } };

	public LuaEditorColoringConfigurationBlock(OverlayPreferenceStore store) {
		super(store);
	}

	protected String[][] getSyntaxColorListModel() {
		return fSyntaxColorListModel;
	}

	protected ProjectionViewer createPreviewViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles, IPreferenceStore store) {
		return new ScriptSourceViewer(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles, store);
	}

	protected ScriptSourceViewerConfiguration createSimpleSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, boolean configureFormatter) {
		return new SimpleLuaSourceViewerConfiguration(colorManager, preferenceStore, editor, ILuaPartitions.LUA_PARTITIONING, configureFormatter);
	}

	protected void setDocumentPartitioning(IDocument document) {
		IDocumentSetupParticipant participant = new IDocumentSetupParticipant() {
			@Override
			public void setup(IDocument document) {
				LuaTextTools tools = Activator.getDefault().getTextTools();
				tools.setupDocumentPartitioner(document, ILuaPartitions.LUA_PARTITIONING);
			}
		};
		participant.setup(document);
	}

	protected InputStream getPreviewContentReader() {
		try {
			return Activator.getDefault().getBundle().getEntry(PREVIEW_FILE_NAME).openStream();
		} catch (IOException e) {
			Activator.logError("Unable to generate code preview", e); //$NON-NLS-1$
		}
		return new ByteArrayInputStream(new byte[] {});
	}

	protected ScriptTextTools getTextTools() {
		return Activator.getDefault().getTextTools();
	}

	@Override
	protected String getNatureId() {
		return null;
		// replace the line above with the line below as soon as org.eclipse.dltk.internal.ui.editor.semantic.highlighting.SemanticHighlightingManager
		// v1.11 is released
		// return LuaNature.ID;
	}
}
