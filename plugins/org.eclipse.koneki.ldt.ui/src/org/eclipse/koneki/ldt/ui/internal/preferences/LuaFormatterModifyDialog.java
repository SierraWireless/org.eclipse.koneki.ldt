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
package org.eclipse.koneki.ldt.ui.internal.preferences;

import org.eclipse.dltk.ui.formatter.FormatterModifyDialog;
import org.eclipse.dltk.ui.formatter.IFormatterModifyDialogOwner;
import org.eclipse.dltk.ui.formatter.IScriptFormatterFactory;

public class LuaFormatterModifyDialog extends FormatterModifyDialog {

	/**
	 * @param dialogOwner
	 *            Parent, tab container
	 * @param formatterFactory
	 *            Source code formatter factory
	 */
	public LuaFormatterModifyDialog(final IFormatterModifyDialogOwner dialogOwner, final IScriptFormatterFactory formatterFactory) {
		super(dialogOwner, formatterFactory);
	}

	/**
	 * @see FormatterModifyDialog#addPages()
	 */
	@Override
	protected void addPages() {
		addTabPage(Messages.LuaFormatterModifyDialogIndentation, new LuaFormatterIndentationTabPage(this));
	}

}
