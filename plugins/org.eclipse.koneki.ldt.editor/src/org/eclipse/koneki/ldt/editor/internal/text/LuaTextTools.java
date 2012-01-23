/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/

package org.eclipse.koneki.ldt.editor.internal.text;

import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.koneki.ldt.editor.internal.partition.LuaPartitionScanner;
import org.eclipse.ui.texteditor.ITextEditor;

public class LuaTextTools extends ScriptTextTools {

	private final static String[] LEGAL_CONTENT_TYPES = new String[] { ILuaPartitions.LUA_STRING, ILuaPartitions.LUA_COMMENT,
			ILuaPartitions.LUA_SINGLE_QUOTE_STRING, ILuaPartitions.LUA_MULTI_LINE_COMMENT, ILuaPartitions.LUA_NUMBER };

	public LuaTextTools(boolean autoDisposeOnDisplayDispose) {
		super(ILuaPartitions.LUA_PARTITIONING, LEGAL_CONTENT_TYPES, autoDisposeOnDisplayDispose);
	}

	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		return new LuaSourceViewerConfiguration(getColorManager(), preferenceStore, editor, partitioning);
	}

	/**
	 * @see org.eclipse.dltk.ui.text.ScriptTextTools#createPartitionScanner()
	 */
	@Override
	public IPartitionTokenScanner createPartitionScanner() {
		return new LuaPartitionScanner();
	}
}
