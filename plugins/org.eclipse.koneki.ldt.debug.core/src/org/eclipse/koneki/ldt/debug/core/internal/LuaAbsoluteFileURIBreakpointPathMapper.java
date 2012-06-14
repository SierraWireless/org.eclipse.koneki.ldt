/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.core.internal;

import java.net.URI;

import org.eclipse.dltk.internal.debug.core.model.IScriptBreakpointPathMapperExtension;

/**
 * A breakpoint path mapper which must return an absolute File local URI
 */
public class LuaAbsoluteFileURIBreakpointPathMapper implements IScriptBreakpointPathMapperExtension {

	public LuaAbsoluteFileURIBreakpointPathMapper() {
	}

	public URI map(URI uri) {
		return uri;
	}

	/**
	 * @see org.eclipse.dltk.internal.debug.core.model.IScriptBreakpointPathMapperExtension#clearCache()
	 */
	@Override
	public void clearCache() {
	}
}
