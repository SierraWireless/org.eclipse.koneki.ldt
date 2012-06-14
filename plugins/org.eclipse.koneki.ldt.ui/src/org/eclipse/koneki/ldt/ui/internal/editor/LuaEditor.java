/*******************************************************************************
 * Copyright (c) 2009, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/

package org.eclipse.koneki.ldt.ui.internal.editor;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.BracketInserter;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.koneki.ldt.core.LuaUtils;
import org.eclipse.koneki.ldt.core.internal.LuaLanguageToolkit;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.editor.navigation.LuaOutlinePage;
import org.eclipse.koneki.ldt.ui.internal.editor.text.ILuaPartitions;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaASTFoldingStructureProvider;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaBracketInserter;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaTextTools;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

/**
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class LuaEditor extends ScriptEditor {

	public static final String EDITOR_CONTEXT = "#LuaEditorContext"; //$NON-NLS-1$
	public static final String EDITOR_ID = Activator.PLUGIN_ID + ".editor"; //$NON-NLS-1$
	private IFoldingStructureProvider foldingStructureProvider = null;

	/**
	 * Will inspect typed character to close automatically string, brackets and braces
	 */
	private BracketInserter bracketInserter = new LuaBracketInserter(this, getPreferenceStore());

	/** Connects partitions used to deal with comments or strings in editor. */
	protected void connectPartitioningToElement(IEditorInput input, IDocument document) {
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension = (IDocumentExtension3) document;
			if (extension.getDocumentPartitioner(ILuaPartitions.LUA_PARTITIONING) == null) {
				LuaTextTools tools = Activator.getDefault().getTextTools();
				tools.setupDocumentPartitioner(document, ILuaPartitions.LUA_PARTITIONING);
			}
		}
	}

	/**
	 * Retrieve ID of editor it is composed from plug-in ID.
	 */
	@Override
	public String getEditorId() {
		return EDITOR_ID;
	}

	@Override
	protected IFoldingStructureProvider getFoldingStructureProvider() {
		if (foldingStructureProvider == null) {
			foldingStructureProvider = new LuaASTFoldingStructureProvider();
		}
		return foldingStructureProvider;
	}

	@Override
	public IDLTKLanguageToolkit getLanguageToolkit() {
		return LuaLanguageToolkit.getDefault();
	}

	/**
	 * @return Editor's preferences
	 */
	@Override
	public IPreferenceStore getScriptPreferenceStore() {
		// TODO BUG_ECLIPSE ???? 360689
		IPreferenceStore uiLanguageToolkitStore = super.getScriptPreferenceStore();
		IPreferenceStore dltkUIStore = DLTKUIPlugin.getDefault().getPreferenceStore();
		if (uiLanguageToolkitStore != null)
			return new ChainedPreferenceStore(new IPreferenceStore[] { uiLanguageToolkitStore, dltkUIStore });
		else
			return dltkUIStore;
	}

	/**
	 * @see org.eclipse.dltk.internal.ui.editor.ScriptEditor#doSelectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	protected void doSelectionChanged(SelectionChangedEvent event) {
		// TODO BUG_ECLIPSE ???? 360693
		if (getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE))
			super.doSelectionChanged(event);
	}

	@Override
	public ScriptTextTools getTextTools() {
		return Activator.getDefault().getTextTools();
	}

	/**
	 * Initialize language specific and parent content.
	 */
	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT);
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#setPartName(java.lang.String)
	 */
	@Override
	protected void setPartName(String partName) {
		// search moduleName
		String moduleFullName = null;
		IModelElement input = getInputModelElement();
		if (input instanceof ISourceModule) {
			moduleFullName = LuaUtils.getModuleFullName((ISourceModule) input);
		}

		// use module as title if is find else use the previous one
		if (moduleFullName != null)
			super.setPartName(moduleFullName);
		else
			super.setPartName(partName);
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeKeyBindingScopes()
	 */
	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.eclipse.dltk.ui.luaEditorScope" }); //$NON-NLS-1$
	}

	/**
	 * @return Bracket matcher for Lua
	 * @see ScriptEditor#createBracketMatcher()
	 */
	@Override
	protected ICharacterPairMatcher createBracketMatcher() {
		return new DefaultCharacterPairMatcher("()[]{}".toCharArray(), ILuaPartitions.LUA_PARTITIONING); //$NON-NLS-1$
	}

	@Override
	protected ScriptOutlinePage doCreateOutlinePage() {
		return new LuaOutlinePage(this, Activator.getDefault().getPreferenceStore());
	}

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		final ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension)
			// Pass typed character to auto insert object
			((ITextViewerExtension) sourceViewer).prependVerifyKeyListener(bracketInserter);
	}
}