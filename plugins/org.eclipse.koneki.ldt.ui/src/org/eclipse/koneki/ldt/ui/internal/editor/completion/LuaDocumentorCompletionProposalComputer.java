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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.templates.ScriptTemplateProposal;
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.editor.LuaDocumentorTags;
import org.eclipse.koneki.ldt.ui.internal.editor.templates.LuaDocumentorTemplateCompletionProcessor;

public class LuaDocumentorCompletionProposalComputer implements IScriptCompletionProposalComputer {

	public LuaDocumentorCompletionProposalComputer() {
	}

	public void sessionStarted() {
	}

	public void sessionEnded() {
	}

	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		IDocument document = context.getDocument();
		try {
			final IRegion region = document.getLineInformationOfOffset(context.getInvocationOffset());
			final char[] line = document.get(region.getOffset(), region.getLength()).toCharArray();
			final int offsetInLine = context.getInvocationOffset() - region.getOffset();
			int index = 0;
			index = skipSpaces(line, index, offsetInLine);
			if (index < offsetInLine && line[index] == '-') {
				++index;
			}
			if (index < offsetInLine && line[index] == '-') {
				++index;
			}
			index = skipSpaces(line, index, offsetInLine);
			if (!(index < offsetInLine && line[index] == '@')) {
				return Collections.emptyList();
			}
			final int tagStart = index;
			++index;
			if (index < offsetInLine && Character.isJavaIdentifierStart(line[index])) {
				++index;
				while (index < offsetInLine && (Character.isJavaIdentifierPart(line[index]) || line[index] == '.' || line[index] == '-')) {
					++index;
				}
			}
			if (index == offsetInLine) {
				return completionOnTag(context, new String(line, tagStart, index - tagStart));
			}
		} catch (BadLocationException e) {
			Activator.logError("Compute completion proposal error", e); //$NON-NLS-1$
		}
		return Collections.emptyList();
	}

	private static int skipSpaces(final char[] line, int index, int offsetInLine) {
		int i = index;
		while (i < offsetInLine && Character.isWhitespace(line[index])) {
			i++;
		}
		return index;
	}

	private List<ICompletionProposal> completionOnTag(ContentAssistInvocationContext context, String tag) {
		final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		final LuaDocumentorTemplateCompletionProcessor processor = new LuaDocumentorTemplateCompletionProcessor(
				(ScriptContentAssistInvocationContext) context);
		Collections.addAll(proposals, processor.computeCompletionProposals(context.getViewer(), context.getInvocationOffset()));
		//
		final Set<String> tags = new HashSet<String>();
		Collections.addAll(tags, LuaDocumentorTags.getTags());
		// collect used tags, to show keywords only for missing ones
		final Set<String> usedTags = new HashSet<String>();
		for (ICompletionProposal proposal : proposals) {
			if (proposal instanceof ScriptTemplateProposal) {
				usedTags.add(((ScriptTemplateProposal) proposal).getTemplateName());
			}
		}
		for (String jsdocTag : tags) {
			if (CharOperation.prefixEquals(tag, jsdocTag) && !usedTags.contains(jsdocTag)) {
				proposals.add(new ScriptCompletionProposal(jsdocTag + ' ', context.getInvocationOffset() - tag.length(), tag.length(),
						DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_JAVADOCTAG), jsdocTag, 90, true));
			}
		}
		return proposals;
	}

	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Collections.emptyList();
	}

	public String getErrorMessage() {
		return null;
	}

}
