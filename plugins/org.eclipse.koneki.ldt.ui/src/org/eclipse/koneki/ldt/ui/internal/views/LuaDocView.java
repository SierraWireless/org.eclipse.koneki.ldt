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
package org.eclipse.koneki.ldt.ui.internal.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.text.HTMLPrinter;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.documentation.ScriptDocumentationAccess;
import org.eclipse.dltk.ui.infoviews.AbstractDocumentationView;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.koneki.ldt.core.internal.LuaLanguageToolkit;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

/**
 * The view which show the documentation of a selected ModelElement
 */
// TODO avoid to access to internal class (open a bug ?)
@SuppressWarnings("restriction")
public class LuaDocView extends AbstractDocumentationView {

	/** Flags used to render a label in the text widget. */
	private static final long LABEL_FLAGS = ScriptElementLabels.ALL_FULLY_QUALIFIED | ScriptElementLabels.M_APP_RETURNTYPE
			| ScriptElementLabels.F_APP_TYPE_SIGNATURE | ScriptElementLabels.M_PARAMETER_TYPES | ScriptElementLabels.M_PARAMETER_NAMES
			| ScriptElementLabels.M_EXCEPTIONS | ScriptElementLabels.T_TYPE_PARAMETERS; // FIXME DUPLICATE CODE from parent class due to private
																						// method declaration ...

	private static String fgStyleSheet; // FIXME DUPLICATE CODE from parent class due to private method declaration ...

	private RGB fBackgroundColorRGB; // FIXME DUPLICATE CODE from parent class due to private method declaration ...

	public LuaDocView() {
	}

	// FIXME DUPLICATE CODE from parent class due to private method declaration ...
	private static void initStyleSheet() {
		Bundle bundle = Platform.getBundle(DLTKUIPlugin.getPluginId());
		URL styleSheetURL = bundle.getEntry("/DocumentationViewStyleSheet.css"); //$NON-NLS-1$
		if (styleSheetURL == null)
			return;
		try {
			styleSheetURL = FileLocator.toFileURL(styleSheetURL);
			InputStream openStream = styleSheetURL.openStream();
			InputStreamReader inputStreamReader = new InputStreamReader(openStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			StringBuffer buffer = new StringBuffer(200);
			String line = reader.readLine();
			while (line != null) {
				buffer.append(line);
				buffer.append('\n');
				line = reader.readLine();
			}
			reader.close();
			FontData fontData = JFaceResources.getFontRegistry().getFontData(PreferenceConstants.APPEARANCE_DOCUMENTATION_FONT)[0];
			fgStyleSheet = org.eclipse.dltk.ui.text.completion.HTMLPrinter.convertTopLevelFont(buffer.toString(), fontData);
		} catch (IOException ex) {
			DLTKUIPlugin.log(ex);
		}
	}

	// //FIXME workarround due to private method declaration ...
	@Override
	protected void internalCreatePartControl(Composite parent) {
		super.internalCreatePartControl(parent);
		initStyleSheet();
	}

	@Override
	protected void setBackground(Color color) {
		fBackgroundColorRGB = color.getRGB();
		super.setBackground(color);
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected String getNature() {
		return LuaLanguageToolkit.getDefault().getNatureId();
	}

	/**
	 * @see org.eclipse.dltk.ui.infoviews.AbstractDocumentationView#computeInput(java.lang.Object)
	 */
	@Override
	protected Object computeInput(Object input) {
		if (getControl() != null) {
			if (input instanceof IModelElement) {
				final IModelElement model = (IModelElement) input;
				// get the html documentation
				String scriptdocHtml = getScriptdocHtml(model);
				if (scriptdocHtml == null)
					// generate default documentation if there no documentation attached
					scriptdocHtml = getDefaultDocumentation(model);
				return scriptdocHtml;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * @return the lua doc in HTML format for a given model Element or null if no documentation is attached
	 */
	private String getScriptdocHtml(IModelElement modelelement) {
		StringBuffer buffer = new StringBuffer();
		// HTMLPrinter.addSmallHeader(buffer, getInfoText(member));
		Reader reader = ScriptDocumentationAccess.getHTMLContentReader(getNature(), modelelement, true, true);
		if (reader != null) {
			HTMLPrinter.addParagraph(buffer, reader);
		}
		return addPrologeEpilog(buffer);
	}

	/**
	 * generate default documentation for a IModelElement
	 */
	private String getDefaultDocumentation(IModelElement modelElement) {
		if (modelElement instanceof ISourceModule) {
			return getDefaultDocumentation((ISourceModule) modelElement);
		} else if (modelElement instanceof IMember) {
			final StringBuffer buffer = new StringBuffer();
			HTMLPrinter.addParagraph(buffer, getInfoText(modelElement));
			HTMLPrinter.addParagraph(buffer, "<br><em>Note: This element has no attached documentation.</em>"); //$NON-NLS-1$
			return addPrologeEpilog(buffer);
		} else {
			return null;
		}
	}

	/**
	 * generate default documentation for a source module
	 */
	private String getDefaultDocumentation(ISourceModule sourcemodule) {
		final StringBuffer buffer = new StringBuffer();
		try {
			IModelElement[] children = sourcemodule.getChildren();
			HTMLPrinter.startBulletList(buffer);
			for (int i = 0; i < children.length; i++) {
				final IModelElement curr = children[i];
				if (curr instanceof IMember) {
					final IMember member = (IMember) curr;
					HTMLPrinter.addBullet(buffer, getInfoText(member));
				}
			}
			HTMLPrinter.endBulletList(buffer);
			HTMLPrinter.addParagraph(buffer, "<em>Note: This element has no attached documentation.</em>"); //$NON-NLS-1$
		} catch (ModelException ex) {
			return null;
		}
		return addPrologeEpilog(buffer);
	}

	/**
	 * Gets the label for the given member.
	 */
	private String getInfoText(Object member) {
		if (member instanceof IModelElement) {
			return ScriptElementLabels.getDefault().getElementLabel((IModelElement) member, LABEL_FLAGS);
		} else {
			return null;
		}
	}

	/**
	 * add header and footer to the html page
	 */
	private String addPrologeEpilog(StringBuffer buffer) {
		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, fBackgroundColorRGB, fgStyleSheet);
			// HTMLPrinter.insertPageProlog(buffer, 0);
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}
		return null;
	}
}
