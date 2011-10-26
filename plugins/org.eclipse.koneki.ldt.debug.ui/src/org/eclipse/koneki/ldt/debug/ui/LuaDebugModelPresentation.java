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
package org.eclipse.koneki.ldt.debug.ui;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptVariable;
import org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation;
import org.eclipse.dltk.internal.debug.core.model.ScriptVariableWrapper;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.koneki.ldt.debug.core.LuaDebugConstant;
import org.eclipse.koneki.ldt.debug.core.UnreachableStackFrame;
import org.eclipse.koneki.ldt.editor.LuaEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

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

	/**
	 * @see org.eclipse.dltk.debug.ui.ScriptDebugModelPresentation#getStackFrameText(org.eclipse.dltk.debug.core.model.IScriptStackFrame)
	 */
	@Override
	protected String getStackFrameText(IScriptStackFrame stackFrame) {
		UnreachableStackFrame uStackFrame = UnreachableStackFrame.checkReachable(stackFrame);
		// TODO: use LDT's AST to try to find function name when Lua's debug API fails
		// see LuaSelectionEngine.select
		String text = super.getStackFrameText(stackFrame);
		if (uStackFrame != null) {
			if (uStackFrame.getReason().equals(UnreachableStackFrame.CCODE_SCHEME)) {
				text = text + " [" + Messages.LuaDebugModelPresentation_ccode + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			} else if (uStackFrame.getReason().equals(UnreachableStackFrame.TAIL_RETURN_SCHEME)) {
				text = "[" + Messages.LuaDebugModelPresentation_tail_return + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			} else if (uStackFrame.getReason().equals(UnreachableStackFrame.UNKNOWN_SCHEME)) {
				text = text + " [" + Messages.LuaDebugModelPresentation_unknown + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return text;
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
		} catch (DebugException e) {
		}
		return super.getVariableImage(variable);
	}
}
