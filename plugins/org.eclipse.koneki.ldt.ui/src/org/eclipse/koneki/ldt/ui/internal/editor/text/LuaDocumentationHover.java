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
package org.eclipse.koneki.ldt.ui.internal.editor.text;

import java.io.IOException;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.internal.ui.text.HTMLPrinter;
import org.eclipse.dltk.internal.ui.text.hover.DocumentationHover;
import org.eclipse.dltk.internal.ui.text.hover.ScriptHoverMessages;
import org.eclipse.dltk.ui.ScriptElementImageProvider;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.documentation.IDocumentationResponse;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationTitleAdapter;
import org.eclipse.dltk.ui.documentation.ScriptDocumentationAccess;
import org.eclipse.dltk.ui.documentation.TextDocumentationResponse;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.koneki.ldt.ui.internal.LuaDocumentationHelper;
import org.eclipse.ui.IEditorPart;

@SuppressWarnings("restriction")
public class LuaDocumentationHover extends DocumentationHover {

	private static final long LABEL_FLAGS = ScriptElementLabels.ALL_FULLY_QUALIFIED | ScriptElementLabels.M_APP_RETURNTYPE
			| ScriptElementLabels.F_APP_TYPE_SIGNATURE | ScriptElementLabels.M_PARAMETER_TYPES | ScriptElementLabels.M_PARAMETER_NAMES
			| ScriptElementLabels.M_EXCEPTIONS | ScriptElementLabels.F_PRE_TYPE_SIGNATURE | ScriptElementLabels.M_PRE_TYPE_PARAMETERS
			| ScriptElementLabels.T_TYPE_PARAMETERS | ScriptElementLabels.USE_RESOLVED;
	private static final long LOCAL_VARIABLE_FLAGS = LABEL_FLAGS & ~ScriptElementLabels.F_FULLY_QUALIFIED | ScriptElementLabels.F_POST_QUALIFIED;

	private static final IScriptDocumentationTitleAdapter TITLE_ADAPTER = new IScriptDocumentationTitleAdapter() {

		private ScriptElementImageProvider fImageProvider = new ScriptElementImageProvider();

		public String getTitle(Object element) {
			if (element instanceof IModelElement) {
				IModelElement member = (IModelElement) element;
				long flags = member.getElementType() == IModelElement.LOCAL_VARIABLE ? LOCAL_VARIABLE_FLAGS : LABEL_FLAGS;
				String label = ScriptElementLabels.getDefault().getElementLabel(member, flags);
				return label;
			} else {
				return null;
			}
		}

		public ImageDescriptor getImage(Object element) {
			if (element instanceof IModelElement) {
				final IModelElement modelElement = (IModelElement) element;
				if (fImageProvider == null) {
					fImageProvider = new ScriptElementImageProvider();
				}
				return fImageProvider.getScriptImageDescriptor(modelElement, ScriptElementImageProvider.OVERLAY_ICONS
						| ScriptElementImageProvider.SMALL_ICONS);
			}
			return null;
		}
	};

	public LuaDocumentationHover(IEditorPart editor, IPreferenceStore store) {
		super();
		setEditor(editor);
		setPreferenceStore(store);
	}

	@Override
	protected String getHoverInfo(String nature, Object[] result) {
		String htmlContent = null;

		int nResults = result.length;
		if (nResults == 0)
			return null;

		boolean hasContents = false;
		if (nResults > 0) {

			Object element = result[0];
			IDocumentationResponse response = ScriptDocumentationAccess.getDocumentation(nature, element, TITLE_ADAPTER);
			// Provide hint why there's no doc
			if (response == null) {
				response = new TextDocumentationResponse(element, TITLE_ADAPTER.getTitle(element), TITLE_ADAPTER.getImage(element),
						ScriptHoverMessages.ScriptdocHover_noAttachedInformation);
			}
			try {

				htmlContent = HTMLPrinter.read(response.getReader());
				hasContents = true;
			} catch (IOException e) {
				return null;
			}

		}
		if (!hasContents)
			return null;

		if (!htmlContent.isEmpty()) {
			return LuaDocumentationHelper.generatePage(htmlContent);
		}
		return null;
	}

}