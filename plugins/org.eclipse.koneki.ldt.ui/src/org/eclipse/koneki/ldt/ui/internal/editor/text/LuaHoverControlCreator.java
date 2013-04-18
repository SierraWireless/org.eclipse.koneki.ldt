/*******************************************************************************
 * Copyright (c) 2013 Sierra Wireless and others. All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sierra Wireless - initial API and implementation
 *******************************************************************************/

package org.eclipse.koneki.ldt.ui.internal.editor.text;

import org.eclipse.dltk.internal.ui.text.IInformationControlExtension4;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;

/**
 * This class is a copy of org.eclipse.dltk.internal.ui.text.hover.DocumentationHover.HoverControlCreator. This class is duplicated only in order to
 * increase by 30px the width of lua documentation tooltip, 30px correspond at the maximum margin you can have in a lua documentation html render with
 * the according CSS. This fix is to avoid to have too small tooltip that truncate the lua documentation content.
 */
@SuppressWarnings("restriction")
public final class LuaHoverControlCreator extends AbstractReusableInformationControlCreator {

	private final IInformationControlCreator fInformationPresenterControlCreator;

	private final boolean fAdditionalInfoAffordance;

	/**
	 * @param informationPresenterControlCreator
	 *            control creator for enriched hover
	 */
	public LuaHoverControlCreator(IInformationControlCreator informationPresenterControlCreator) {
		this(informationPresenterControlCreator, false);
	}

	/**
	 * @param informationPresenterControlCreator
	 *            control creator for enriched hover
	 * @param additionalInfoAffordance
	 *            <code>true</code> to use the additional info affordance, <code>false</code> to use the hover affordance
	 */
	public LuaHoverControlCreator(IInformationControlCreator informationPresenterControlCreator, boolean additionalInfoAffordance) {
		fInformationPresenterControlCreator = informationPresenterControlCreator;
		fAdditionalInfoAffordance = additionalInfoAffordance;
	}

	@Override
	public IInformationControl doCreateInformationControl(Shell parent) {
		String tooltipAffordanceString = fAdditionalInfoAffordance ? DLTKUIPlugin.getAdditionalInfoAffordanceString() : EditorsUI
				.getTooltipAffordanceString();
		if (BrowserInformationControl.isAvailable(parent)) {
			String font = PreferenceConstants.APPEARANCE_DOCUMENTATION_FONT;
			BrowserInformationControl iControl = new BrowserInformationControl(parent, font, tooltipAffordanceString) {

				@Override
				public IInformationControlCreator getInformationPresenterControlCreator() {
					return fInformationPresenterControlCreator;
				}

				@Override
				public Point computeSizeHint() {
					Point point = super.computeSizeHint();
					// adding 30px the width of lua documentation tooltip
					return new Point(point.x + 30, point.y);
				}

			};
			return iControl;
		} else {
			return new DefaultInformationControl(parent, tooltipAffordanceString);
		}
	}

	@Override
	public boolean canReuse(IInformationControl control) {
		if (!super.canReuse(control))
			return false;

		if (control instanceof IInformationControlExtension4) {
			String tooltipAffordanceString = fAdditionalInfoAffordance ? DLTKUIPlugin.getAdditionalInfoAffordanceString() : EditorsUI
					.getTooltipAffordanceString();
			((IInformationControlExtension4) control).setStatusText(tooltipAffordanceString);
		}

		return true;
	}
}