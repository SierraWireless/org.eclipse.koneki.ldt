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
import org.eclipse.dltk.ui.templates.ScriptTemplateCompletionProcessor;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;

/**
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 * 
 */
public class LuaTemplateCompletionProcessor extends ScriptTemplateCompletionProcessor {

	private static final char[] IGNORE = { '.', ':' };

	public LuaTemplateCompletionProcessor(ScriptContentAssistInvocationContext context) {
		super(context);
	}

	@Override
	protected String getContextTypeId() {
		return LuaUniversalTemplateContextType.CONTEXT_TYPE_ID;
	}

	protected char[] getIgnore() {
		return IGNORE;
	}

	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return LuaTemplateAccess.getInstance();
	}

}
