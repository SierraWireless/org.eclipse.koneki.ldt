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

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.swt.graphics.Image;

public class LuaCompletionProposalCollector extends ScriptCompletionProposalCollector {

	private static final char[] VAR_TRIGGER = { ';' };

	public LuaCompletionProposalCollector(ISourceModule module) {
		super(module);
	}

	@Override
	protected ScriptCompletionProposal createScriptCompletionProposal(String completion, int replaceStart, int length, Image image,
			String displayString, int i) {
		return new LuaCompletionProposal(completion, replaceStart, length, image, displayString, i);
	}

	@Override
	protected ScriptCompletionProposal createScriptCompletionProposal(String completion, int replaceStart, int length, Image image,
			String displayString, int i, boolean isInDoc) {
		return new LuaCompletionProposal(completion, replaceStart, length, image, displayString, i, isInDoc);
	}

	@Override
	protected IScriptCompletionProposal createMethodReferenceProposal(CompletionProposal methodProposal) {
		return new LuaScriptMethodCompletionProposal(methodProposal, getInvocationContext());
	}

	@Override
	protected char[] getVarTrigger() {
		return VAR_TRIGGER;
	}

	@Override
	protected String getNatureId() {
		return LuaNature.ID;
	}

}
