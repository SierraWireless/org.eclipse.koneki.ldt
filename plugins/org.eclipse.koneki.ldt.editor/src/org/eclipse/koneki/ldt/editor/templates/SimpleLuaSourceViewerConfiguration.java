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
package org.eclipse.koneki.ldt.editor.templates;

import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.koneki.ldt.editor.internal.text.LuaSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

public class SimpleLuaSourceViewerConfiguration extends LuaSourceViewerConfiguration {

	private boolean configureFormatter;

	public SimpleLuaSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning,
			boolean configureFormatter) {
		super(colorManager, preferenceStore, editor, partitioning);
		this.configureFormatter = configureFormatter;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		if (this.configureFormatter)
			return super.getContentFormatter(sourceViewer);
		else
			return null;
	}

}
