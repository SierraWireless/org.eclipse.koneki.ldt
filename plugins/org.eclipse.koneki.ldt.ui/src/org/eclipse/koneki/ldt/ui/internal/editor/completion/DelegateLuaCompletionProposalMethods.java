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
import org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal;
import org.eclipse.koneki.ldt.ui.internal.LuaDocumentationHelper;

public final class DelegateLuaCompletionProposalMethods {

	private DelegateLuaCompletionProposalMethods() {
	}

	public static Object getAdditionalProposalInfo(AbstractScriptCompletionProposal proposal, IProgressMonitor monitor) {
		if (proposal.getProposalInfo() != null) {
			String info = proposal.getProposalInfo().getInfo(monitor);
			if (info != null && info.length() > 0) {
				info = LuaDocumentationHelper.generatePage(info);
			}
			return info;
		}
		return null;
	}

}
