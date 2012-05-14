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

package org.eclipse.koneki.ldt.core;

import java.awt.Toolkit;

import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.koneki.ldt.Activator;

/**
 * The Class LuaLanguageToolkit gather some Eclipse editor conventions.
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class LuaLanguageToolkit extends AbstractLanguageToolkit {

	/** Current instance of editor. */
	private static IDLTKLanguageToolkit toolkit = null;

	/**
	 * Getter on current instance of ToolKit.
	 * 
	 * @return current instance of {@linkplain Toolkit}
	 */
	public static IDLTKLanguageToolkit getDefault() {
		if (toolkit == null) {
			toolkit = new LuaLanguageToolkit();
		}
		return toolkit;
	}

	/**
	 * Gives a content-type for the language
	 * 
	 * @see org.eclipse.dltk.core.IDLTKLanguageToolkit#getLanguageContentType()
	 * @return String
	 */
	@Override
	public String getLanguageContentType() {
		return Activator.PLUGIN_ID + ".content-type"; //$NON-NLS-1$
	}

	/**
	 * Just gives current language name.
	 * 
	 * @return String
	 * @see org.eclipse.dltk.core.IDLTKLanguageToolkit#getLanguageName()
	 */
	@Override
	public String getLanguageName() {
		return "Lua"; //$NON-NLS-1$
	}

	/**
	 * Nature of current editor
	 * 
	 * @see org.eclipse.dltk.core.IDLTKLanguageToolkit#getNatureId()
	 */
	@Override
	public String getNatureId() {
		return LuaNature.ID;
	}

	/**
	 * @see org.eclipse.dltk.core.AbstractLanguageToolkit#languageSupportZIPBuildpath()
	 */
	@Override
	public boolean languageSupportZIPBuildpath() {
		return true;
	}

	public String getPreferenceQualifier() {
		return Activator.PLUGIN_ID;
	}
}
