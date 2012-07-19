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
package org.eclipse.koneki.ldt.ui.tests.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.editor.text.ILuaPartitions;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaDocumentorCommentAutoEditStrategy;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaTextTools;

/**
 * Tests for {@link LuaDocumentorCommentAutoEditStrategy}
 */
public class LuaDocumentorCommentAutoEditStrategyTest extends TestCase {

	private static final String ENTER = "\n"; //$NON-NLS-1$

	protected IDocument createDocument(String code) {
		final IDocument document = new Document(code);
		IDocumentSetupParticipant participant = new IDocumentSetupParticipant() {
			@Override
			public void setup(IDocument document) {
				LuaTextTools tools = Activator.getDefault().getTextTools();
				tools.setupDocumentPartitioner(document, ILuaPartitions.LUA_PARTITIONING);
			}
		};
		participant.setup(document);
		return document;
	}

	protected DocumentCommand createCommand(String text, int offset) {
		return createCommand(text, offset, 0);
	}

	protected DocumentCommand createCommand(String text, int offset, int length) {
		final DocumentCommand cmd = new DocumentCommand() {
		};
		cmd.offset = offset;
		cmd.length = length;
		cmd.text = text;
		cmd.doit = true;
		cmd.caretOffset = offset;
		cmd.shiftsCaret = true;
		return cmd;
	}

	protected void execute(IDocument document, DocumentCommand cmd) {
		final IAutoEditStrategy strategy = new LuaDocumentorCommentAutoEditStrategy();
		strategy.customizeDocumentCommand(document, cmd);
		if (!cmd.doit)
			return;
		try {
			// access "execute(IDocument)" method via reflection since it has package visibility
			Method execute = DocumentCommand.class.getDeclaredMethod("execute", IDocument.class); //$NON-NLS-1$
			if (!execute.isAccessible())
				execute.setAccessible(true);
			execute.invoke(cmd, document);
		} catch (SecurityException e) {
			Assert.fail(e.toString());
		} catch (NoSuchMethodException e) {
			Assert.fail(e.toString());
		} catch (IllegalArgumentException e) {
			Assert.fail(e.toString());
		} catch (IllegalAccessException e) {
			Assert.fail(e.toString());
		} catch (InvocationTargetException e) {
			Assert.fail(e.toString());
		}
	}

	public void testSimplestComment() throws BadLocationException {
		StringBuffer code = new StringBuffer();
		code.append("--"); //$NON-NLS-1$
		final IDocument document = createDocument(code.toString());
		execute(document, createCommand(ENTER, getEndOfLineOffset(document, 0)));
		StringBuffer expected = new StringBuffer();
		expected.append("--" + ENTER); //$NON-NLS-1$
		expected.append("--"); //$NON-NLS-1$
		assertEquals(expected.toString(), document.get());
	}

	public void testSimplestDocComment() throws BadLocationException {
		StringBuffer code = new StringBuffer();
		code.append("---"); //$NON-NLS-1$
		final IDocument document = createDocument(code.toString());
		execute(document, createCommand(ENTER, getEndOfLineOffset(document, 0)));
		StringBuffer expected = new StringBuffer();
		expected.append("---" + ENTER); //$NON-NLS-1$
		expected.append("--"); //$NON-NLS-1$
		assertEquals(expected.toString(), document.get());
	}

	public void testSimplestIndentedDocComment() throws BadLocationException {
		StringBuffer code = new StringBuffer();
		code.append("   ---"); //$NON-NLS-1$
		final IDocument document = createDocument(code.toString());
		execute(document, createCommand(ENTER, getEndOfLineOffset(document, 0)));
		StringBuffer expected = new StringBuffer();
		expected.append("   ---" + ENTER); //$NON-NLS-1$
		expected.append("   --"); //$NON-NLS-1$
		assertEquals(expected.toString(), document.get());
	}

	public void testIndentedDocComment() throws BadLocationException {
		StringBuffer code = new StringBuffer();
		code.append("    ---" + ENTER); //$NON-NLS-1$
		code.append("    -- line 1"); //$NON-NLS-1$
		final IDocument document = createDocument(code.toString());
		execute(document, createCommand(ENTER, getEndOfLineOffset(document, 1)));
		execute(document, createCommand("line 2", getEndOfLineOffset(document, 2))); //$NON-NLS-1$
		StringBuffer expected = new StringBuffer();
		expected.append("    ---" + ENTER); //$NON-NLS-1$
		expected.append("    -- line 1" + ENTER); //$NON-NLS-1$
		expected.append("    -- line 2"); //$NON-NLS-1$
		assertEquals(expected.toString(), document.get());
	}

	public void testDocComment() throws BadLocationException {
		StringBuffer code = new StringBuffer();
		code.append("---" + ENTER); //$NON-NLS-1$
		code.append("--  my comment"); //$NON-NLS-1$
		final IDocument document = createDocument(code.toString());
		execute(document, createCommand(ENTER, getEndOfLineOffset(document, 1)));
		StringBuffer expected = new StringBuffer();
		expected.append("---" + ENTER); //$NON-NLS-1$
		expected.append("--  my comment" + ENTER); //$NON-NLS-1$
		expected.append("--  "); //$NON-NLS-1$
		assertEquals(expected.toString(), document.get());
	}

	public void testCodeWithoutComment() throws BadLocationException {
		StringBuffer code = new StringBuffer();
		code.append("local v = 123"); //$NON-NLS-1$
		final IDocument document = createDocument(code.toString());
		execute(document, createCommand(ENTER, getEndOfLineOffset(document, 0)));
		StringBuffer expected = new StringBuffer();
		expected.append("local v = 123" + ENTER); //$NON-NLS-1$
		assertEquals(expected.toString(), document.get());
	}

	public void testSplittingComment() throws BadLocationException {
		StringBuffer code = new StringBuffer();
		code.append("  ---" + ENTER); //$NON-NLS-1$
		code.append("  -- my --comment"); //$NON-NLS-1$
		final IDocument document = createDocument(code.toString());
		execute(document, createCommand(ENTER, getEndOfLineOffset(document, 1) - "--comment".length())); //$NON-NLS-1$
		StringBuffer expected = new StringBuffer();
		expected.append("  ---" + ENTER); //$NON-NLS-1$
		expected.append("  -- my " + ENTER); //$NON-NLS-1$
		expected.append("  ----comment"); //$NON-NLS-1$
		assertEquals(expected.toString(), document.get());
	}

	private static int getEndOfLineOffset(IDocument d, int line) throws BadLocationException {
		IRegion line1 = d.getLineInformation(line);
		return line1.getOffset() + line1.getLength();
	}

}
