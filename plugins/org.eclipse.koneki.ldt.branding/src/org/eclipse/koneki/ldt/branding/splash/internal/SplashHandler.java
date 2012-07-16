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

package org.eclipse.koneki.ldt.branding.splash.internal;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.koneki.ldt.branding.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.splash.BasicSplashHandler;

/**
 * Our custom Splash Handler that shows a ProgressBar and the version number
 */
public class SplashHandler extends BasicSplashHandler {

	private static final int DEFAULT_FOREGROUND_COLOR = 0xD2D7FF;
	private static final Rectangle DEFAULT_MESSAGE_RECT = new Rectangle(1, 1, 10, 100);
	private static final Rectangle DEFAULT_PROGRESS_RECT = new Rectangle(1, 12, 10, 100);

	private Font font;

	public void init(Shell splash) {
		super.init(splash);

		// get product information
		String progressRectString = null;
		String messageRectString = null;
		String foregroundColorString = null;
		IProduct product = Platform.getProduct();
		if (product != null) {
			progressRectString = product.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
			messageRectString = product.getProperty(IProductConstants.STARTUP_MESSAGE_RECT);
			foregroundColorString = product.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);
		}

		// set message position
		Rectangle messageRect = StringConverter.asRectangle(messageRectString, DEFAULT_MESSAGE_RECT);
		setMessageRect(messageRect);

		// set progress position
		Rectangle progressRect = StringConverter.asRectangle(progressRectString, DEFAULT_PROGRESS_RECT);
		setProgressRect(progressRect);

		// get foreground color
		int foregroundColorInteger;
		try {
			foregroundColorInteger = Integer.parseInt(foregroundColorString, 16);
		} catch (NumberFormatException ex) {
			foregroundColorInteger = DEFAULT_FOREGROUND_COLOR;
		}
		setForeground(new RGB((foregroundColorInteger & 0xFF0000) >> 16, (foregroundColorInteger & 0xFF00) >> 8, foregroundColorInteger & 0xFF));

		String version = System.getProperty(Activator.PRODUCT_VERSION_PROPERTY);
		int lastIndexOf = version.lastIndexOf('.');
		if (lastIndexOf != -1)
			version = version.substring(0, lastIndexOf);

		final String buildId = "Version: " + version; //$NON-NLS-1$
		final Point position = new Point(280, 150);

		font = new Font(Display.getDefault(), new FontData("Helvetica", (Platform.getOS().equals(Platform.OS_MACOSX) ? 9 : 7), SWT.BOLD)); //$NON-NLS-1$
		getContent().addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(getForeground());
				e.gc.setFont(font);
				e.gc.drawText(buildId, position.x, position.y, true);
			}
		});
	}

	@Override
	public void dispose() {
		font.dispose();
		super.dispose();
	}
}
