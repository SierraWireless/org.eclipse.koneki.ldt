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
package org.eclipse.koneki.ldt.ui.internal.editor.text;

import org.eclipse.jface.text.IDocument;

public interface ILuaPartitions {
	public static final String LUA_PARTITIONING = "__lua_partitioning"; //$NON-NLS-1$

	public static final String LUA_COMMENT = "__lua_comment"; //$NON-NLS-1$
	public static final String LUA_MULTI_LINE_COMMENT = "__lua_multi_line_comment"; //$NON-NLS-1$
	public static final String LUA_NUMBER = "__lua_number"; //$NON-NLS-1$
	public static final String LUA_STRING = "__lua_string"; //$NON-NLS-1$
	public static final String LUA_SINGLE_QUOTE_STRING = "__lua_single_quote_string"; //$NON-NLS-1$

	static final String[] LUA_PARTITION_TYPES = new String[] { IDocument.DEFAULT_CONTENT_TYPE, ILuaPartitions.LUA_COMMENT,
			ILuaPartitions.LUA_COMMENT, ILuaPartitions.LUA_STRING, ILuaPartitions.LUA_SINGLE_QUOTE_STRING, ILuaPartitions.LUA_MULTI_LINE_COMMENT,
			ILuaPartitions.LUA_NUMBER };
}
