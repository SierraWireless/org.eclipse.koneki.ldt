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

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.koneki.ldt.ui.internal.editor.templates.LuaTemplateCompletionProcessor;

public class LuaCompletionProposalComputer extends ScriptCompletionProposalComputer {

	@Override
	protected TemplateCompletionProcessor createTemplateProposalComputer(ScriptContentAssistInvocationContext context) {
		return new LuaTemplateCompletionProcessor(context);
	}

	protected ScriptCompletionProposalCollector createCollector(ScriptContentAssistInvocationContext context) {
		return new LuaCompletionProposalCollector(context.getSourceModule());
	}
}
