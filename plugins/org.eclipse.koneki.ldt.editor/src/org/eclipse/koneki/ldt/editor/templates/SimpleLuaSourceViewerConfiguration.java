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
package org.eclipse.koneki.ldt.editor.templates;

import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.koneki.ldt.editor.internal.text.LuaSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

public class SimpleLuaSourceViewerConfiguration extends LuaSourceViewerConfiguration {

	private boolean fConfigureFormatter;

	public SimpleLuaSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning, boolean configureFormatter) {
		super(colorManager, preferenceStore, editor, partitioning);
		fConfigureFormatter = configureFormatter;
	}
	/*
	 * public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) { return null; }
	 * 
	 * public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) { return null; }
	 * 
	 * public IAnnotationHover getOverviewRulerAnnotationHover(ISourceViewer sourceViewer) { return null; }
	 * 
	 * public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer, String contentType) { return null; }
	 * 
	 * public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) { return null; }
	 * 
	 * public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) { return null; }
	 * 
	 * public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) { if (fConfigureFormatter) return
	 * super.getContentFormatter(sourceViewer); else return null; }
	 * 
	 * public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) { return null; }
	 * 
	 * public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) { return null; }
	 * 
	 * public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer, boolean doCodeResolve) { return null; }
	 * 
	 * public IInformationPresenter getHierarchyPresenter(ISourceViewer sourceViewer, boolean doCodeResolve) { return null; }
	 * 
	 * public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) { return null; }
	 */
}
