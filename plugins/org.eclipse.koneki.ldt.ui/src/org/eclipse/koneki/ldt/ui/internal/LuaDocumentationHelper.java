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
package org.eclipse.koneki.ldt.ui.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;
import org.osgi.framework.Bundle;

public final class LuaDocumentationHelper {

	public static final String BACKGROUND_COLOR_ID = "org.eclipse.koneki.ldt.ui.docbackground"; //$NON-NLS-1$
	public static final String FOREGROUND_COLOR_ID = "org.eclipse.koneki.ldt.ui.docforeground"; //$NON-NLS-1$

	private static final String CSS_FILE_PATH = "/resources/css/lua_documentation.css"; //$NON-NLS-1$

	private static String styleSheet;

	private LuaDocumentationHelper() {
	}

	private static ColorRegistry getColorRegistry() {
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();
		return currentTheme.getColorRegistry();
	}

	public static Color getForegroundColor() {
		return getColorRegistry().get(FOREGROUND_COLOR_ID);
	}

	public static Color getBackgroundColor() {
		return getColorRegistry().get(BACKGROUND_COLOR_ID);
	}

	public static String getStyleSheet() {
		if (styleSheet == null) {
			styleSheet = initStyleSheet();
		}

		// Retrieve font from preference
		FontData fontData = JFaceResources.getFontRegistry().getFontData(PreferenceConstants.APPEARANCE_DOCUMENTATION_FONT)[0];
		String styleSheetWithPreference = org.eclipse.dltk.ui.text.completion.HTMLPrinter.convertTopLevelFont(styleSheet, fontData);

		// Retrieve colors from theme:
		RGB bgColor = getBackgroundColor().getRGB();
		RGB fgColor = getForegroundColor().getRGB();

		styleSheetWithPreference = "body {color:" + toHtmlColor(fgColor) + "; background-color:" + toHtmlColor(bgColor) + ";}" + styleSheetWithPreference; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return styleSheetWithPreference;
	}

	protected static String initStyleSheet() {
		Bundle bundle = Activator.getDefault().getBundle();
		URL styleSheetURL = bundle.getEntry(CSS_FILE_PATH);
		if (styleSheetURL == null) {
			String errorMessage = MessageFormat.format("No css found on the path: {1}", CSS_FILE_PATH); //$NON-NLS-1$
			Activator.logError(errorMessage, new NullPointerException());
		}
		try {
			styleSheetURL = FileLocator.toFileURL(styleSheetURL);
			File cssFile = FileUtils.toFile(styleSheetURL);
			return FileUtils.readFileToString(cssFile);

		} catch (IOException ex) {
			Activator.logError("Unable to open CSS file for luadoc view", ex); //$NON-NLS-1$
		}
		return null;
	}

	protected static String toHtmlColor(RGB color) {
		StringBuffer buffer = new StringBuffer();

		buffer.append('#');
		appendHex(color.red, buffer);
		appendHex(color.green, buffer);
		appendHex(color.blue, buffer);

		return buffer.toString();
	}

	protected static void appendHex(int color, StringBuffer buffer) {
		String string = Integer.toHexString(color).toUpperCase();
		if (string.length() == 1) {
			buffer.append("0"); //$NON-NLS-1$
		}
		buffer.append(string);
	}

	public static String generatePage(String htmlContent) {
		return generatePage(getStyleSheet(), htmlContent);
	}

	public static String generatePage(String cssStyle, String htmlContent) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head><style CHARSET=\"ISO-8859-1\" TYPE=\"text/css\">"); //$NON-NLS-1$
		buffer.append(cssStyle);
		buffer.append("</style></head><body>"); //$NON-NLS-1$
		buffer.append(htmlContent);
		buffer.append("</body></html>"); //$NON-NLS-1$
		return buffer.toString();
	}

}
