/*******************************************************************************
 * Copyright (c) 2009, 2012 Sierra Wireless and others.
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
import org.eclipse.dltk.ui.templates.ScriptTemplateContextType;
import org.eclipse.jface.text.IDocument;

/**
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 * 
 */
public class LuaUniversalTemplateContextType extends ScriptTemplateContextType {

	public static final String CONTEXT_TYPE_ID = "LuaUniversalTemplateContextType"; //$NON-NLS-1$

	public LuaUniversalTemplateContextType() {
		super();
		setupLuaResolvers();
	}

	public LuaUniversalTemplateContextType(String id, String name) {
		super(id, name);
	}

	public LuaUniversalTemplateContextType(String id) {
		super(id);
	}

	private void setupLuaResolvers() {
		addResolver(new LuaScriptTemplateVariables.SourceModuleTemplateVariableResolver());
		addResolver(new LuaScriptTemplateVariables.FullSourceModuleTemplateVariableResolver());
	}

	@Override
	public ScriptTemplateContext createContext(IDocument document, int completionPosition, int length, ISourceModule sourceModule) {
		return new LuaTemplateContext(this, document, completionPosition, length, sourceModule);
	}
}
