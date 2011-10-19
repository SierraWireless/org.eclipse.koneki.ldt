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
package org.eclipse.koneki.ldt.completion.internal;

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

public class LuaCompletionProposalComputer extends
	ScriptCompletionProposalComputer {
    public LuaCompletionProposalComputer() {

    }

    @Override
    protected TemplateCompletionProcessor createTemplateProposalComputer(
	    ScriptContentAssistInvocationContext context) {
	// TODO Auto-generated method stub
	return null;
    }

    protected ScriptCompletionProposalCollector createCollector(
	    ScriptContentAssistInvocationContext context) {
	return new LuaCompletionProposalCollector(context.getSourceModule());
    }
}
