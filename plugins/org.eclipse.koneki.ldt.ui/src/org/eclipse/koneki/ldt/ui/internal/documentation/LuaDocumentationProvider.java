/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.documentation;

import java.io.Reader;
import java.io.StringReader;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.ui.documentation.IDocumentationResponse;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProviderExtension;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProviderExtension2;
import org.eclipse.dltk.ui.documentation.TextDocumentationResponse;
import org.eclipse.koneki.ldt.Activator;
import org.eclipse.koneki.ldt.parser.LuaASTUtils;
import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;

/**
 * In charge to find the documentation for a given "lua element" Use to feed Luadoc view and tooltip in LuaEditor
 */
public class LuaDocumentationProvider implements IScriptDocumentationProvider, IScriptDocumentationProviderExtension,
		IScriptDocumentationProviderExtension2 {

	/**
	 * @see org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider#getInfo(org.eclipse.dltk.core.IMember, boolean, boolean)
	 */
	@Override
	public Reader getInfo(IMember element, boolean lookIntoParents, boolean lookIntoExternal) {
		try {
			String memberDocumentation = getMemberDocumentation(element);
			if (memberDocumentation != null)
				return new StringReader(memberDocumentation);
		} catch (ModelException e) {
			Activator.logWarning("unable to get documentation for :" + element, e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * @see org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider#getInfo(java.lang.String)
	 */
	@Override
	public Reader getInfo(String content) {
		return null;
	}

	/**
	 * @see org.eclipse.dltk.ui.documentation.IScriptDocumentationProviderExtension#describeKeyword(java.lang.String,
	 *      org.eclipse.dltk.core.IModelElement)
	 */
	@Override
	public IDocumentationResponse describeKeyword(String keyword, IModelElement context) {
		return null;
	}

	/**
	 * @see org.eclipse.dltk.ui.documentation.IScriptDocumentationProviderExtension2#getDocumentationFor(java.lang.Object)
	 */
	@Override
	public IDocumentationResponse getDocumentationFor(Object element) {
		try {
			// Support Documentation for ISourceModule and IMember
			if (element instanceof IMember) {
				String memberDocumentation = getMemberDocumentation((IMember) element);

				if (memberDocumentation != null)
					return new TextDocumentationResponse(element, memberDocumentation);
			} else if (element instanceof ISourceModule) {
				String moduleDocumentation = getModuleDocumentation((ISourceModule) element);
				if (moduleDocumentation != null)
					return new TextDocumentationResponse(element, moduleDocumentation);
			}
		} catch (ModelException e) {
			Activator.logWarning("unable to get documentation for :" + element, e); //$NON-NLS-1$
		}
		return null;
	}

	private String getMemberDocumentation(IMember member) throws ModelException {
		ISourceModule sourceModule = member.getSourceModule();
		if (sourceModule != null) {
			if (LuaASTUtils.isModule(member)) {
				return getModuleDocumentation(sourceModule);
			} else {
				// get identifier
				String elementName = member.getElementName();
				// get documentation for this identifier
				ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule);
				if (moduleDeclaration instanceof LuaSourceRoot) {
					return ((LuaSourceRoot) moduleDeclaration).getMemberDocumentation(elementName);
				}
			}
		}
		return null;
	}

	private String getModuleDocumentation(ISourceModule module) {
		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(module);
		if (moduleDeclaration instanceof LuaSourceRoot) {
			return ((LuaSourceRoot) moduleDeclaration).getGlobalDocumentation();
		}
		return null;
	}

}
