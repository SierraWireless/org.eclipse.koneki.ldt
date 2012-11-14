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
package org.eclipse.koneki.ldt.ui.internal.editor.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.dltk.ui.text.IScriptCorrectionContext;
import org.eclipse.dltk.ui.text.IScriptCorrectionProcessor;

public class LuaScriptCorrectionProcessor implements IScriptCorrectionProcessor {

	public LuaScriptCorrectionProcessor() {
	}

	@Override
	public boolean canFix(IScriptAnnotation annotation) {
		return false;
	}

	@Override
	public boolean canFix(IMarker marker) {
		return false;
	}

	@Override
	public void computeQuickAssistProposals(IScriptAnnotation annotation, IScriptCorrectionContext context) {

	}

	@Override
	public void computeQuickAssistProposals(IMarker marker, IScriptCorrectionContext context) {
	}
}
