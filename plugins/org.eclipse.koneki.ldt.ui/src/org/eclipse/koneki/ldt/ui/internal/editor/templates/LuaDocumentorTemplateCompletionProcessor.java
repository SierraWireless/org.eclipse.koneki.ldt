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
package org.eclipse.koneki.ldt.ui.internal.editor.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplateCompletionProcessor;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.ImageConstants;
import org.eclipse.swt.graphics.Image;

/**
 * LuaDocumentor template completion processor
 */
public class LuaDocumentorTemplateCompletionProcessor extends ScriptTemplateCompletionProcessor {

	public LuaDocumentorTemplateCompletionProcessor(ScriptContentAssistInvocationContext context) {
		super(context);
	}

	/*
	 * @see ScriptTemplateCompletionProcessor#getContextTypeId()
	 */
	@Override
	protected String getContextTypeId() {
		return LuaDocumentorTemplateContextType.CONTEXT_TYPE_ID;
	}

	/*
	 * @see ScriptTemplateCompletionProcessor#getTemplateAccess()
	 */
	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return LuaTemplateAccess.getInstance();
	}

	protected String extractPrefix(ITextViewer viewer, int offset) {
		int i = offset;
		IDocument document = viewer.getDocument();
		if (i > document.getLength())
			return ""; //$NON-NLS-1$

		try {
			while (i > 0) {
				char ch = document.getChar(i - 1);
				if (!Character.isJavaIdentifierPart(ch) && ch != '@')
					break;
				i--;
			}

			return document.get(i, offset - i);
		} catch (BadLocationException e) {
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	protected ICompletionProposal createProposal(Template template, TemplateContext context, IRegion region, int relevance) {
		return new LuaDocumentorTemplateProposal(template, context, region, getImage(template), relevance);
	}

	@Override
	protected Image getImage(Template template) {
		return Activator.getDefault().getImageRegistry().get(ImageConstants.TEMPLATE_LUADOC);
	}
}
