/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.buildpath;

import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

public class LuaExecutionEnvironmentLabelProvider implements ILabelProvider, IStyledLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getImage(Object element) {
		return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_LIBRARY);
	}

	@Override
	public String getText(Object element) {
		if (element != null) {
			return element.toString();
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(java.lang.Object)
	 */
	@Override
	public StyledString getStyledText(Object element) {

		// we styled only execution environment;
		if (!(element instanceof LuaExecutionEnvironment))
			return null;

		// custom style for embedded execution environment
		final String text = getText(element);
		if (((LuaExecutionEnvironment) element).isEmbedded())
			return new StyledString(text + Messages.LuaExecutionEnvironmentLabelProvider_embedded_string, new Styler() {

				@Override
				public void applyStyles(TextStyle textStyle) {
					if (textStyle instanceof StyleRange) {
						((StyleRange) textStyle).start = text.length();
						Font italic = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
						textStyle.font = italic;
						((StyleRange) textStyle).fontStyle = SWT.BOLD;
					}
					textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND);

				}
			});

		// else default styled
		return new StyledString(text);
	}
}
