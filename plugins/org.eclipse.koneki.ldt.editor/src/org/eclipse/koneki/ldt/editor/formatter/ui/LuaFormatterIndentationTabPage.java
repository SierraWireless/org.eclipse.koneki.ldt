/*******************************************************************************
 * Copyright (c) 2011, 2012 Sierra Wireless and others.
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
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.koneki.ldt.editor.formatter.LuaFormatterFactory;
import org.eclipse.koneki.ldt.editor.formatter.LuaFormatterPreferenceConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

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
		createFormatTable(manager, parent);
	}

	/**
	 * Initialize check box for enable formating in tables
	 */
	protected void createFormatTable(final IFormatterControlManager manager, final Composite parent) {
		final Group tablePolicyGroup = SWTFactory.createGroup(parent, Messages.LuaFormatterIndentationTabPageTableIndentationPolicy, 2, 1,
				GridData.FILL_HORIZONTAL);
		manager.createCheckbox(tablePolicyGroup, LuaFormatterPreferenceConstants.FORMATTER_INDENT_TABLE_VALUES,
				Messages.LuaFormatterIndentationTabPageIndentTableValues, 1);
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
