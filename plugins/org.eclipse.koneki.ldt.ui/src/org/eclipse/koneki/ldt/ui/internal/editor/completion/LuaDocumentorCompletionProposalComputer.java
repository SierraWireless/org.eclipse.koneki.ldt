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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.editor.LuaDocumentorTags;
import org.eclipse.koneki.ldt.ui.internal.editor.templates.LuaDocumentorTemplateCompletionProcessor;
import org.eclipse.koneki.ldt.ui.internal.editor.text.ILuaPartitions;

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

			final IRegion contentAssistRegion = document.getLineInformationOfOffset(context.getInvocationOffset());
			final char[] contentAssistLine = document.get(contentAssistRegion.getOffset(), contentAssistRegion.getLength()).toCharArray();
			final int contentAssistOffsetInLine = context.getInvocationOffset() - contentAssistRegion.getOffset();

			int offsetInCurrentLine = 0;

			// Check if we are on the first line of the lua doc bloc to known how many hyphens we have to ignore
			// Find the block region where the content assist is called
			ITypedRegion[] partitions = TextUtilities.computePartitioning(document, ILuaPartitions.LUA_PARTITIONING, 0, document.getLength(), false);
			for (ITypedRegion region : partitions) {
				if (ILuaPartitions.LUA_DOC.equals(region.getType()) || ILuaPartitions.LUA_DOC_MULTI.equals(region.getType())) {
					if (context.getInvocationOffset() >= region.getOffset()
							&& context.getInvocationOffset() < (region.getOffset() + region.getLength())) {
						// "region" is the current region
						int blockFirstLine = document.getLineOfOffset(region.getOffset());
						int invocationLine = document.getLineOfOffset(context.getInvocationOffset());
						boolean isInvocationOnFirstLine = (blockFirstLine == invocationLine);

						// On the first line, skip things before the luadoc block
						if (isInvocationOnFirstLine) {
							offsetInCurrentLine = region.getOffset() - document.getLineOffset(blockFirstLine);
						} else {
							offsetInCurrentLine = 0;
						}

						// skip openning comment chars
						if (ILuaPartitions.LUA_DOC_MULTI.equals(region.getType())) {
							// on multi-line skip "--[[-"
							if (isInvocationOnFirstLine) {
								offsetInCurrentLine = skipOpenningMultiLineChars(contentAssistLine, offsetInCurrentLine, contentAssistOffsetInLine);
							}
						} else if (ILuaPartitions.LUA_DOC.equals(region.getType())) {
							// skip 3 or 2 hyphens
							if (isInvocationOnFirstLine) {
								offsetInCurrentLine += 3;
							} else {
								offsetInCurrentLine += 2;
							}
						}
					}
				}
			}

			offsetInCurrentLine = skipSpaces(contentAssistLine, offsetInCurrentLine, contentAssistOffsetInLine);
			// the first char after comment opening have to be a @
			if (!(offsetInCurrentLine <= contentAssistOffsetInLine)) {
				return Collections.emptyList();
			}
			// we find the start of the tag
			int tagStart = offsetInCurrentLine;

			// filter proposals of the text between the @ and the cursor and compute relevance
			final boolean endOfLine = (contentAssistLine.length == contentAssistOffsetInLine);
			final boolean isCursorFollowedByWhitespace = (!endOfLine && Character.isWhitespace(contentAssistLine[contentAssistOffsetInLine]));
			final String partialTag = new String(contentAssistLine, tagStart, contentAssistOffsetInLine - tagStart);
			return completionOnTag(context, partialTag, endOfLine, isCursorFollowedByWhitespace);

		} catch (BadLocationException e) {
			Activator.logError("Compute completion proposal error", e); //$NON-NLS-1$
		}
		return Collections.emptyList();
	}

	private static int skipOpenningMultiLineChars(char[] contentAssistLine, int offsetInCurrentLine, int contentAssistOffsetInLine) {
		Pattern pattern = Pattern.compile("^--\\[=*\\[-");//$NON-NLS-1$
		String startBlockToCursor = new String(contentAssistLine, offsetInCurrentLine, contentAssistOffsetInLine - offsetInCurrentLine);
		Matcher matcher = pattern.matcher(startBlockToCursor);
		if (matcher.find()) {
			return matcher.end() + offsetInCurrentLine;
		}
		return offsetInCurrentLine;
	}

	private static int skipSpaces(final char[] line, int offsetInCurrentLine, int offsetInLine) {
		while (offsetInCurrentLine < offsetInLine && Character.isWhitespace(line[offsetInCurrentLine])) {
			offsetInCurrentLine++;
		}
		return offsetInCurrentLine;
	}

	private List<ICompletionProposal> completionOnTag(final ContentAssistInvocationContext context, final String tag,
			final boolean nothingAfterOnTheLine, boolean tagFollowedByWhitespace) {
		// retrieve templates proposals
		final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		final LuaDocumentorTemplateCompletionProcessor processor = new LuaDocumentorTemplateCompletionProcessor(
				(ScriptContentAssistInvocationContext) context);
		Collections.addAll(proposals, processor.computeCompletionProposals(context.getViewer(), context.getInvocationOffset()));

		// retrieve tags
		final Set<String> tags = new HashSet<String>();
		Collections.addAll(tags, LuaDocumentorTags.getTags());

		// add simple tags proposals matching with the given tag
		for (String jsdocTag : tags) {
			if (CharOperation.prefixEquals(tag, jsdocTag)) {

				// if there is nothing after, template are more relevant
				int relevance = nothingAfterOnTheLine ? 50 : 95;

				// add a space after the replacement if missing
				String replacement = jsdocTag;
				if (nothingAfterOnTheLine || !tagFollowedByWhitespace) {
					replacement += ' ';
				}

				// add tag proposal
				proposals.add(new ScriptCompletionProposal(replacement, context.getInvocationOffset() - tag.length(), tag.length(), DLTKPluginImages
						.get(DLTKPluginImages.IMG_OBJS_JAVADOCTAG), jsdocTag, relevance, true));
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
