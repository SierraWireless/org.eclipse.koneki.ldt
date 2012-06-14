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
package org.eclipse.koneki.ldt.ui.internal;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.AbstractDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.koneki.ldt.core.internal.LuaLanguageToolkit;
import org.eclipse.koneki.ldt.ui.internal.editor.LuaEditor;
import org.eclipse.koneki.ldt.ui.internal.editor.templates.SimpleLuaSourceViewerConfiguration;
import org.eclipse.koneki.ldt.ui.internal.editor.text.ILuaPartitions;

public class LuaUILanguageToolkit extends AbstractDLTKUILanguageToolkit {

	@Override
	public IDLTKLanguageToolkit getCoreToolkit() {
		return LuaLanguageToolkit.getDefault();
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public String getPartitioningId() {
		return ILuaPartitions.LUA_PARTITIONING;
	}

	@Override
	public String getEditorId(Object inputElement) {
		return LuaEditor.EDITOR_ID;
	}

	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleLuaSourceViewerConfiguration(getTextTools().getColorManager(), getPreferenceStore(), null, getPartitioningId(), false);
	}

	@Override
	public ScriptTextTools getTextTools() {
		return Activator.getDefault().getTextTools();
	}

}
