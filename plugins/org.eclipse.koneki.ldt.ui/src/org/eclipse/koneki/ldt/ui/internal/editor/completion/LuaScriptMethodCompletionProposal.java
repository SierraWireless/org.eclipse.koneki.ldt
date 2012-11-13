/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.editor.completion;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.ScriptMethodCompletionProposal;

public class LuaScriptMethodCompletionProposal extends ScriptMethodCompletionProposal {

	public LuaScriptMethodCompletionProposal(CompletionProposal proposal, ScriptContentAssistInvocationContext context) {
		super(proposal, context);
	}

	@Override
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		return DelegateLuaCompletionProposalMethods.getAdditionalProposalInfo(this, monitor);
	}
}
