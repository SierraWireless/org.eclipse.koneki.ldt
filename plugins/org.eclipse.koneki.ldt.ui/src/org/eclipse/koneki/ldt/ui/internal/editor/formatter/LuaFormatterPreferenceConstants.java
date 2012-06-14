/*******************************************************************************
 * Copyright (c) 2011, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.editor.formatter;

import org.eclipse.dltk.ui.CodeFormatterConstants;

/**
 * Constants used for setting and retrieving values from preferences
 */
public interface LuaFormatterPreferenceConstants {
	public static final String FORMATTER_ID = "formatterId"; //$NON-NLS-1$
	/**
	 * Selected index from separator selection combo, can be:
	 * <ul>
	 * <li>space</li>
	 * <li>tab</li>
	 * <li>mixed</li>
	 * </ul>
	 */
	public static final String FORMATTER_TAB_CHAR = CodeFormatterConstants.FORMATTER_TAB_CHAR;
	/** Count of character a tabulation represent */
	public static final String FORMATTER_TAB_SIZE = CodeFormatterConstants.FORMATTER_TAB_SIZE;
	/** Count of character needed for indenting code */
	public static final String FORMATTER_INDENTATION_SIZE = CodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
	public static final String FORMATTER_INDENT_TABLE_VALUES = "formatter.indentation.indentTables"; //$NON-NLS-1$
	public static final String FORMATTER_PROFILES = "formatter.profiles"; //$NON-NLS-1$
	public static final String FORMATTER_ACTIVE_PROFILE = "formatter.profiles.active"; //$NON-NLS-1$
}
