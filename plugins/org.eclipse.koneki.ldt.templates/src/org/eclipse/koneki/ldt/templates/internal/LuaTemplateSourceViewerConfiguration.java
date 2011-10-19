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
package org.eclipse.koneki.ldt.templates.internal;

import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.koneki.ldt.editor.internal.text.LuaSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

public class LuaTemplateSourceViewerConfiguration extends
	LuaSourceViewerConfiguration {

    private boolean fConfigureFormatter;

    public LuaTemplateSourceViewerConfiguration(IColorManager colorManager,
	    IPreferenceStore preferenceStore, ITextEditor editor,
	    String partitioning, boolean configureFormatter) {
	super(colorManager, preferenceStore, editor, partitioning);
	fConfigureFormatter = configureFormatter;
    }

    public IAutoEditStrategy[] getAutoEditStrategies(
	    ISourceViewer sourceViewer, String contentType) {
	return null;
    }

    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
	return null;
    }

    public IAnnotationHover getOverviewRulerAnnotationHover(
	    ISourceViewer sourceViewer) {
	return null;
    }

    public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer,
	    String contentType) {
	return null;
    }

    public ITextHover getTextHover(ISourceViewer sourceViewer,
	    String contentType, int stateMask) {
	return null;
    }

    public ITextHover getTextHover(ISourceViewer sourceViewer,
	    String contentType) {
	return null;
    }

    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
	if (fConfigureFormatter)
	    return super.getContentFormatter(sourceViewer);
	else
	    return null;
    }

    public IInformationControlCreator getInformationControlCreator(
	    ISourceViewer sourceViewer) {
	return null;
    }

    public IInformationPresenter getInformationPresenter(
	    ISourceViewer sourceViewer) {
	return null;
    }

    public IInformationPresenter getOutlinePresenter(
	    ISourceViewer sourceViewer, boolean doCodeResolve) {
	return null;
    }

    public IInformationPresenter getHierarchyPresenter(
	    ISourceViewer sourceViewer, boolean doCodeResolve) {
	return null;
    }

    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
	return null;
    }
}
