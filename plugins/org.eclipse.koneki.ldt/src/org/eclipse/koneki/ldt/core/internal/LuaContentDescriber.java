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

package org.eclipse.koneki.ldt.core.internal;

import java.util.regex.Pattern;

import org.eclipse.dltk.core.ScriptContentDescriber;

/**
 * The Class LuaContentDescriber gives patterns for Lua files headers. Most of the time those headers allow to choose an interpreter for runnable
 * scripts
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class LuaContentDescriber extends ScriptContentDescriber {

	/** Accepted patterns for headers. */
	private static final Pattern[] HEADER_PATTERNS = { Pattern.compile("^#!.*lua.*", Pattern.MULTILINE) };//$NON-NLS-1$

	/**
	 * Instantiates a new Lua content describer.
	 */
	public LuaContentDescriber() {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.core.ScriptContentDescriber#getHeaderPatterns()
	 */
	@Override
	protected Pattern[] getHeaderPatterns() {
		return HEADER_PATTERNS;
	}
}
