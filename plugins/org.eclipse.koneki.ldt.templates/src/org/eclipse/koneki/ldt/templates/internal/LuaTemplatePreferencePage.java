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
package org.eclipse.koneki.ldt.templates.internal;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.koneki.ldt.editor.internal.text.ILuaPartitions;
import org.eclipse.koneki.ldt.editor.internal.text.LuaSourceViewerConfiguration;
import org.eclipse.koneki.ldt.editor.internal.text.LuaTextTools;

public class LuaTemplatePreferencePage extends ScriptTemplatePreferencePage {

    @Override
    protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
	return new LuaSourceViewerConfiguration(getTextTools()
		.getColorManager(), getPreferenceStore(), null,
		ILuaPartitions.LUA_PARTITIONING);
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
