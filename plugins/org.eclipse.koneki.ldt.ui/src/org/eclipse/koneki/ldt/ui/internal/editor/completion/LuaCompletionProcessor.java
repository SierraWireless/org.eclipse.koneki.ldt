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
package org.eclipse.koneki.ldt.ui.internal.editor.completion;

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProcessor;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.ui.IEditorPart;

public class LuaCompletionProcessor extends ScriptCompletionProcessor {

	public LuaCompletionProcessor(IEditorPart editor,
			ContentAssistant assistant, String partition) {
		super(editor, assistant, partition);
	}

	@Override
	protected String getNatureId() {
		return LuaNature.ID;
	}
}
