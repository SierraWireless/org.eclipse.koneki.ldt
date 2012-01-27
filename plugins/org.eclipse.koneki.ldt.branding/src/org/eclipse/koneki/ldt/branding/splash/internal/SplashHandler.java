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

	private Font font;

	public void init(Shell splash) {
		super.init(splash);

		String progressRectString = null;
		String messageRectString = null;
		String foregroundColorString = null;
		IProduct product = Platform.getProduct();
		if (product != null) {
			progressRectString = product.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
			messageRectString = product.getProperty(IProductConstants.STARTUP_MESSAGE_RECT);
			foregroundColorString = product.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);
		}
		Rectangle progressRect = StringConverter.asRectangle(progressRectString, new Rectangle(10, 150, 100, 15));
		setProgressRect(progressRect);

		Rectangle messageRect = StringConverter.asRectangle(messageRectString, new Rectangle(10, 35, 300, 15));
		setMessageRect(messageRect);

		int foregroundColorInteger;
		try {
			foregroundColorInteger = Integer.parseInt(foregroundColorString, 16);
		} catch (NumberFormatException ex) {
			foregroundColorInteger = 0xD2D7FF; // off white
		}

		setForeground(new RGB((foregroundColorInteger & 0xFF0000) >> 16, (foregroundColorInteger & 0xFF00) >> 8, foregroundColorInteger & 0xFF));

		String version = (String) Platform.getBundle("org.eclipse.koneki.ldt").getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION); //$NON-NLS-1$
		// version = version.substring(0, 3);
		//final String buildId = System.getProperty("eclipse.buildId", "Version : " + version); //$NON-NLS-1$ //$NON-NLS-2$

		final String buildId = "Version: " + version;
		String buildIdLocString = product.getProperty("buildIdLocation"); //$NON-NLS-1$
		final Point buildIdPoint = StringConverter.asPoint(buildIdLocString, new Point(280, 150));

		font = new Font(Display.getDefault(), new FontData("Helvetica", (Platform.getOS().equals(Platform.OS_MACOSX) ? 9 : 7), SWT.BOLD));
		getContent().addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(getForeground());
				e.gc.setFont(font);
				// e.gc.setAntialias(SWT.ON);
				e.gc.drawText(buildId, buildIdPoint.x, buildIdPoint.y, true);
			}
		});
	}

	@Override
	public void dispose() {
		font.dispose();
		super.dispose();
	}
}
