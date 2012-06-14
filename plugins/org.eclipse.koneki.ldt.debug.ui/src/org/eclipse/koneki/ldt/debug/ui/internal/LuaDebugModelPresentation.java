/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.ui.internal;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.dltk.debug.core.DLTKDebugConstants;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.core.model.IScriptVariable;
import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.dltk.internal.debug.core.model.ScriptVariableWrapper;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.koneki.ldt.debug.core.internal.LuaCoroutine;
import org.eclipse.koneki.ldt.debug.core.internal.LuaDebugConstant;
import org.eclipse.koneki.ldt.debug.core.internal.LuaModuleURIUtil;
import org.eclipse.koneki.ldt.debug.core.internal.UnreachableStackFrame;
import org.eclipse.koneki.ldt.ui.internal.editor.LuaEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class LuaDebugModelPresentation extends ScriptDebugModelPresentation {

	/**
	 * @see org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	@Override
	public String getEditorId(IEditorInput input, Object element) {
		return LuaEditor.EDITOR_ID;
	}

	/**
	 * @see org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation#getStackFrameImage(org.eclipse.dltk.debug.core.model.IScriptStackFrame)
	 */
	@Override
	protected Image getStackFrameImage(IScriptStackFrame stackFrame) {
		UnreachableStackFrame uStackFrame = UnreachableStackFrame.checkReachable(stackFrame);
		if (uStackFrame != null) {
			ImageRegistry reg = Activator.getDefault().getImageRegistry();
			if (uStackFrame.getReason().equals(UnreachableStackFrame.CCODE_SCHEME)) {
				return reg.get(ImageConstants.LUA_DEBUG_CCODE_STACK_FRAME);
			}
			return reg.get(ImageConstants.LUA_DEBUG_UNREACHABLE_STACK_FRAME);
		}
		return super.getStackFrameImage(stackFrame);
	}

	@Override
	protected String getStackFrameText(IScriptStackFrame stackFrame) {
		String sourceLine = getSourceLine(stackFrame);
		String sourceLocation = getSourceLocation(stackFrame);
		return MessageFormat.format("{0}  [ {1} ]", sourceLine, sourceLocation); //$NON-NLS-1$
	}

	/**
	 * @param stackFrame
	 * @return
	 */
	private String getSourceLocation(IScriptStackFrame stackFrame) {
		URI sourceURI = stackFrame.getSourceURI();

		// get location
		String location;
		if (DLTKDebugConstants.FILE_SCHEME.equalsIgnoreCase(sourceURI.getScheme())) {
			IPath path;
			try {
				// TODO not the good way to found the relative path
				path = getStackFrameRelativePath(stackFrame);
			} catch (CoreException e) {
				Activator.logWarning("unable to extract relative path", e); //$NON-NLS-1$
				path = new Path(sourceURI.getPath());
			}
			location = path.toString();
		} else if (LuaModuleURIUtil.isModuleURI(sourceURI)) {
			location = LuaModuleURIUtil.getModuleName(sourceURI);
		} else if (UnreachableStackFrame.CCODE_SCHEME.equalsIgnoreCase(sourceURI.getScheme())) {
			location = Messages.LuaDebugModelPresentation_ccode;
		} else if (UnreachableStackFrame.TAIL_RETURN_SCHEME.equalsIgnoreCase(sourceURI.getScheme())) {
			location = Messages.LuaDebugModelPresentation_tail_return;
		} else if (DLTKDebugConstants.UNKNOWN_SCHEME.equalsIgnoreCase(sourceURI.getScheme())) {
			location = Messages.LuaDebugModelPresentation_unknown;
		} else {
			location = Messages.LuaDebugModelPresentation_unknown;
			Activator.logWarning("unsupported uri :" + sourceURI); //$NON-NLS-1$
		}

		// get line number
		int lineNumber = -1;
		try {
			lineNumber = stackFrame.getLineNumber();
		} catch (DebugException e) {
			Activator.logWarning("could not acces to stackframe line number for  + sourceURI"); //$NON-NLS-1$
		}

		if (lineNumber > 0)
			return MessageFormat.format("{0}:{1}", location, lineNumber); //$NON-NLS-1$
		else
			return location;
	}

	private String getSourceLine(IScriptStackFrame stackFrame) {
		// get source line from debug model (so debug client)
		String sourceLine = stackFrame.getSourceLine();

		// if not found, try to found in source file.
		if (sourceLine == null || sourceLine.length() == 0) {
			final ILaunch launch = stackFrame.getLaunch();
			final ISourceLocator sourceLocator = launch.getSourceLocator();
			if (sourceLocator != null) {
				final Object object = sourceLocator.getSourceElement(stackFrame);

				if (object instanceof IFile) {
					final IDocumentProvider provider = DLTKUIPlugin.getDocumentProvider();

					final IDocument document = provider.getDocument(new FileEditorInput((IFile) object));

					if (document != null) {
						try {
							sourceLine = retrieveStackFrameLine(stackFrame, document);
						} catch (BadLocationException e) {
							Activator.logWarning("unnable to retrieve stack frame line", e); //$NON-NLS-1$
						} catch (DebugException e) {
							Activator.logWarning("unnable to retrieve stack frame line", e); //$NON-NLS-1$
						}
					}
				}
			}
		}

		// if not found, display just the stack level
		if (sourceLine == null || sourceLine.length() == 0) {
			final int level = stackFrame.getStack().size() - stackFrame.getLevel() - 1;
			sourceLine = NLS.bind("Stack frame #{0}", Integer.valueOf(level)); //$NON-NLS-1$
		}

		if (sourceLine == null || sourceLine.length() == 0)
			return ""; //$NON-NLS-1$
		else
			return sourceLine;

	}

	private static String retrieveStackFrameLine(IScriptStackFrame frame, final IDocument document) throws BadLocationException, DebugException {
		if (frame.getBeginLine() > 0 && frame.getEndLine() > 0) {
			final IRegion region = document.getLineInformation(frame.getBeginLine() - 1);
			final int start = region.getOffset() + frame.getBeginColumn();
			final int len;
			if (frame.getBeginLine() == frame.getEndLine()) {
				len = frame.getEndColumn() - frame.getBeginColumn() + 1;
			} else {
				len = region.getLength() - frame.getBeginColumn();
			}
			return document.get(start, len).trim();
		}
		final IRegion region = document.getLineInformation(frame.getLineNumber() - 1);
		return document.get(region.getOffset(), region.getLength()).trim();
	}

	/**
	 * @see org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation#getVariableName(org.eclipse.debug.core.model.IVariable)
	 */
	@Override
	public String getVariableName(IVariable variable) throws DebugException {
		// FIXME this is the only possible way to change a context name in DLTK
		if (variable instanceof ScriptVariableWrapper
				&& variable.getName().equals(org.eclipse.dltk.internal.debug.core.model.Messages.ScriptStackFrame_classVariables)) {
			return Messages.LuaDebugModelPresentation_upvalues;
		}
		return super.getVariableName(variable);
	}

	/**
	 * @see org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation#getVariableImage(org.eclipse.dltk.debug.core.model.IScriptVariable)
	 */
	@Override
	protected Image getVariableImage(IScriptVariable variable) {
		try {
			if (variable.getReferenceTypeName().equals(LuaDebugConstant.TYPE_SPECIAL)) {
				return Activator.getDefault().getImageRegistry().get(ImageConstants.LUA_DEBUG_SPECIAL_VAR);
			}
			// CHECKSTYLE:OFF
		} catch (DebugException e) {
			// do nothing and so return variable image
			// CHECKSTYLE:ON
		}
		return super.getVariableImage(variable);
	}

	/**
	 * @see org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation#getThreadText(org.eclipse.dltk.debug.core.model.IScriptThread)
	 */
	@Override
	protected String getThreadText(IScriptThread thread) {
		if (thread instanceof LuaCoroutine) {
			try {
				return NLS.bind(Messages.LuaDebugModelPresentation_pause_coroutine, thread.getName());
			} catch (DebugException e) {
				Activator.logError("Cannot get name of coroutine", e); //$NON-NLS-1$
			}
		}
		return NLS.bind(Messages.LuaDebugModelPresentation_running_coroutine, (thread.isSuspended() ? SUSPENDED_LABEL : RUNNING_LABEL));
	}
}