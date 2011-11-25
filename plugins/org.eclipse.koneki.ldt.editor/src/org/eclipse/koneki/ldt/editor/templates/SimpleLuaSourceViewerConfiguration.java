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
package org.eclipse.koneki.ldt.editor.templates;

import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.koneki.ldt.editor.internal.text.LuaSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

public class SimpleLuaSourceViewerConfiguration extends LuaSourceViewerConfiguration {

	public SimpleLuaSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning,
			boolean configureFormatter) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

}
