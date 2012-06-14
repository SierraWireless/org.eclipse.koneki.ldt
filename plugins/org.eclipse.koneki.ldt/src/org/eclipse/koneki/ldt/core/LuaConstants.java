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
package org.eclipse.koneki.ldt.core;

public final class LuaConstants {
	public static final String DEFAULT_MAIN_FILE = "main.lua"; //$NON-NLS-1$
	public static final String MAIN_FILE_CONTENT = "local function main()\n\nend\nmain()\n"; //$NON-NLS-1$
	public static final String SOURCE_FOLDER = "src"; //$NON-NLS-1$

	public static final String COMMENT_STRING = "--"; //$NON-NLS-1$
	public static final String SELF_PARAMETER = "self"; //$NON-NLS-1$

	private LuaConstants() {
	}
}
