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

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.templates.ScriptTemplateContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.templates.TemplateContextType;

public class LuaDocumentorScriptTemplateContext extends ScriptTemplateContext {

	public LuaDocumentorScriptTemplateContext(TemplateContextType type, IDocument document, int completionOffset, int completionLength,
			ISourceModule sourceModule) {
		super(type, document, completionOffset, completionLength, sourceModule);
	}

	public LuaDocumentorScriptTemplateContext(TemplateContextType type, IDocument document, Position position, ISourceModule sourceModule) {
		super(type, document, position, sourceModule);
	}

}
