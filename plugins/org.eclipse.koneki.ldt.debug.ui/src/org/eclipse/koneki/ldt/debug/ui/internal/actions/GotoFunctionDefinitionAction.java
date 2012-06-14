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
package org.eclipse.koneki.ldt.debug.ui.internal.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.dltk.debug.core.model.IScriptValue;
import org.eclipse.dltk.debug.core.model.IScriptVariable;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.koneki.ldt.debug.core.internal.LuaFunctionType;
import org.eclipse.koneki.ldt.debug.core.internal.LuaFunctionType.FunctionData;
import org.eclipse.koneki.ldt.debug.ui.internal.Activator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;

public class GotoFunctionDefinitionAction implements IObjectActionDelegate {

	// Ressources needed for lookup, updated by selectionChanged and setActivePart
	private IScriptValue value;
	private IWorkbenchPart part;

	public GotoFunctionDefinitionAction() {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		// source lookup
		FunctionData data = ((LuaFunctionType) value.getType()).getData(value);
		ISourceLocator locator = value.getDebugTarget().getLaunch().getSourceLocator();
		ISourceLookupResult result = DebugUITools.lookupSource(data.getPath(), locator);

		if (result.getSourceElement() == null) {
			String title = Messages.GotoFunctionDefinitionAction_name;
			String message = Messages.GotoFunctionDefinitionAction_open_error_msg;
			String cause = Messages.GotoFunctionDefinitionAction_lookup_failed_msg;
			ErrorDialog.openError(DLTKDebugUIPlugin.getActiveWorkbenchShell(), title, message, new Status(IStatus.ERROR, Activator.PLUGIN_ID, cause));
			return;
		}

		try {
			// open editor
			ScriptDebugModelPresentation presentation = DLTKDebugUIPlugin.getDefault().getModelPresentation(value.getModelIdentifier());

			IEditorInput input = presentation.getEditorInput(result.getSourceElement());
			String editorId = presentation.getEditorId(input, result.getSourceElement());

			ITextEditor editor = (ITextEditor) part.getSite().getPage().openEditor(input, editorId);

			// reveal definition line
			IDocument doc = editor.getDocumentProvider().getDocument(input);
			editor.selectAndReveal(doc.getLineOffset(data.getLine() - 1), 0);
		} catch (PartInitException e) {
			Activator.logError("Failed to open editor", e); //$NON-NLS-1$
		} catch (BadLocationException e) {
			Activator.logError("Failed to set definition line", e); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		boolean enabled = false;
		try {
			if (selection instanceof IStructuredSelection) {
				IScriptVariable var = (IScriptVariable) ((IStructuredSelection) selection).getFirstElement();
				if (var != null) {
					value = (IScriptValue) var.getValue();
					if (value.getType() instanceof LuaFunctionType) {
						enabled = true;
					}
				}
			}
		} catch (DebugException e) {
			enabled = false;
		}
		action.setEnabled(enabled);
	}

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

}
