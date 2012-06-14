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
import org.eclipse.dltk.ui.templates.IScriptTemplateContext;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.eclipse.koneki.ldt.core.LuaUtils;

/**
 * Specific template variables for LUA
 */

public final class LuaScriptTemplateVariables {

	private LuaScriptTemplateVariables() {
	}

	/**
	 * Module name variable
	 */
	public static class SourceModuleTemplateVariableResolver extends TemplateVariableResolver {
		public static final String NAME = "module_short_name"; //$NON-NLS-1$

		public SourceModuleTemplateVariableResolver() {
			super(NAME, Messages.LuaScriptTemplateVariables_SourceModuleDescription);
		}

		protected String resolve(TemplateContext context) {
			ISourceModule module = getSourceModule(context);
			return (module == null) ? null : LuaUtils.getModuleName(module);
		}

		protected boolean isUnambiguous(TemplateContext context) {
			return resolve(context) != null;
		}
	}

	/**
	 * Full module name variable
	 */
	public static class FullSourceModuleTemplateVariableResolver extends TemplateVariableResolver {
		public static final String NAME = "module_name"; //$NON-NLS-1$

		public FullSourceModuleTemplateVariableResolver() {
			super(NAME, Messages.LuaScriptTemplateVariables_FullSourceModuleDescription);
		}

		protected String resolve(TemplateContext context) {
			ISourceModule module = getSourceModule(context);
			return (module == null) ? null : LuaUtils.getModuleFullName(module);
		}

		protected boolean isUnambiguous(TemplateContext context) {
			return resolve(context) != null;
		}
	}

	private static ISourceModule getSourceModule(TemplateContext context) {
		return ((IScriptTemplateContext) context).getSourceModule();
	}
}
