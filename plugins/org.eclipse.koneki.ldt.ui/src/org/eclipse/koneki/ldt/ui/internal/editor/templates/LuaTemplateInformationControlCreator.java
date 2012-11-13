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

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.text.hover.SourceViewerInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlCreatorExtension;
import org.eclipse.koneki.ldt.ui.internal.LuaDocumentationHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Shell;

/**
 * Copy of the final class TemplateInformationControlCreator to custom background color.
 */
@SuppressWarnings("restriction")
public class LuaTemplateInformationControlCreator implements IInformationControlCreator, IInformationControlCreatorExtension {

	private SourceViewerInformationControl fControl;

	/**
	 * The orientation to be used by this hover. Allowed values are: SWT#RIGHT_TO_LEFT or SWT#LEFT_TO_RIGHT
	 * 
	 * @since 3.2
	 */
	private int fOrientation;

	private IDLTKLanguageToolkit fToolkit;

	/**
	 * @param orientation
	 *            the orientation, allowed values are: SWT#RIGHT_TO_LEFT or SWT#LEFT_TO_RIGHT
	 */
	public LuaTemplateInformationControlCreator(int orientation, IDLTKLanguageToolkit toolkit) {
		Assert.isLegal(orientation == SWT.RIGHT_TO_LEFT || orientation == SWT.LEFT_TO_RIGHT);
		fOrientation = orientation;
		fToolkit = toolkit;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlCreator#createInformationControl(org.eclipse.swt.widgets.Shell)
	 */
	public IInformationControl createInformationControl(Shell parent) {
		fControl = new SourceViewerInformationControl(parent, SWT.TOOL | fOrientation, SWT.NONE, fToolkit);
		fControl.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				fControl = null;
			}
		});

		// Set control color
		fControl.setBackgroundColor(LuaDocumentationHelper.getBackgroundColor());
		fControl.setForegroundColor(LuaDocumentationHelper.getForegroundColor());

		return fControl;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlCreatorExtension#canReuse(org.eclipse.jface.text.IInformationControl)
	 */
	public boolean canReuse(IInformationControl control) {
		return fControl == control && fControl != null;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlCreatorExtension#canReplace(org.eclipse.jface.text.IInformationControlCreator)
	 */
	public boolean canReplace(IInformationControlCreator creator) {
		return (creator != null && getClass() == creator.getClass());
	}
}
