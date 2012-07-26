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

public class LuaDocumentorTemplateContextType extends LuaUniversalTemplateContextType {

	public static final String CONTEXT_TYPE_ID = "LuaDocumentorTemplateContextType"; //$NON-NLS-1$

	public LuaDocumentorTemplateContextType() {
		super();
	}

	public LuaDocumentorTemplateContextType(String id) {
		super(id);
	}

	public LuaDocumentorTemplateContextType(String id, String name) {
		super(id, name);
	}

	public ScriptTemplateContext createContext(IDocument document, int offset, int length, ISourceModule sourceModule) {
		return new LuaDocumentorScriptTemplateContext(this, document, offset, length, sourceModule);
	}

	@Override
	public ScriptTemplateContext createContext(IDocument document, Position position, ISourceModule sourceModule) {
		return new LuaDocumentorScriptTemplateContext(this, document, position, sourceModule);
	}
}
