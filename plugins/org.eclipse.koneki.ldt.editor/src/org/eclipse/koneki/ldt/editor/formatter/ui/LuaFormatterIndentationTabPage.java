/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.editor.formatter.ui;

import java.net.URL;

import org.eclipse.dltk.ui.formatter.FormatterIndentationGroup;
import org.eclipse.dltk.ui.formatter.FormatterModifyTabPage;
import org.eclipse.dltk.ui.formatter.IFormatterControlManager;
import org.eclipse.dltk.ui.formatter.IFormatterModifyDialog;
import org.eclipse.koneki.ldt.editor.formatter.LuaFormatterFactory;
import org.eclipse.swt.widgets.Composite;

public class LuaFormatterIndentationTabPage extends FormatterModifyTabPage {

	public LuaFormatterIndentationTabPage(final IFormatterModifyDialog dialog) {
		super(dialog);
	}

	/**
	 * @see FormatterModifyTabPage#createOptions(IFormatterControlManager, oComposite)
	 */
	@Override
	protected void createOptions(final IFormatterControlManager manager, final Composite parent) {
		new FormatterIndentationGroup(manager, parent);
	}

	/**
	 * Source code sample to preview
	 * 
	 * @see FormatterModifyTabPage#getPreviewContent()
	 */
	@Override
	public URL getPreviewContent() {
		return LuaFormatterFactory.getPreviewSample();
	}
}