/*******************************************************************************
 * Copyright (c) 2009, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/

package org.eclipse.koneki.ldt.editor.internal.text;

import org.eclipse.dltk.ui.text.DLTKColorConstants;

public interface ILuaColorConstants {
	public static final String LUA_STRING = DLTKColorConstants.DLTK_STRING;
	public static final String LUA_SINGLE_LINE_COMMENT = DLTKColorConstants.DLTK_SINGLE_LINE_COMMENT;
	public static final String LUA_MULTI_LINE_COMMENT = DLTKColorConstants.DLTK_MULTI_LINE_COMMENT;
	public static final String LUA_NUMBER = DLTKColorConstants.DLTK_NUMBER;
	public static final String LUA_KEYWORD = DLTKColorConstants.DLTK_KEYWORD;
	public static final String LUA_DEFAULT = DLTKColorConstants.DLTK_DEFAULT;

	public static final String LUA_LOCAL_VARIABLE = "variable.local"; //$NON-NLS-1$
	public static final String LUA_GLOBAL_VARIABLE = "variable.global"; //$NON-NLS-1$

	public static final String COMMENT_TASK_TAGS = DLTKColorConstants.TASK_TAG;
}
