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
package org.eclipse.koneki.ldt.ui.internal.editor.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.text.IDocument;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.editor.text.ILuaPartitions;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaTextTools;

public class LuaTemplatePreferencePage extends ScriptTemplatePreferencePage {

	@Override
	protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleLuaSourceViewerConfiguration(getTextTools().getColorManager(), getPreferenceStore(), null, ILuaPartitions.LUA_PARTITIONING,
				false);
	}

	@Override
	protected void setDocumentPartitioner(IDocument document) {
		getTextTools().setupDocumentPartitioner(document, ILuaPartitions.LUA_PARTITIONING);
	}

	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return LuaTemplateAccess.getInstance();
	}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	private LuaTextTools getTextTools() {
		return Activator.getDefault().getTextTools();
	}
}
