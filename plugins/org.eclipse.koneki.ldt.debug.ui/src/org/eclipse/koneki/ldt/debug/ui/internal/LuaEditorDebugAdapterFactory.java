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
package org.eclipse.koneki.ldt.debug.ui.internal;

import org.eclipse.dltk.debug.ui.ScriptEditorDebugAdapterFactory;
import org.eclipse.dltk.debug.ui.breakpoints.ScriptToggleBreakpointAdapter;

public class LuaEditorDebugAdapterFactory extends ScriptEditorDebugAdapterFactory {

	/**
	 * @see org.eclipse.dltk.debug.ui.ScriptEditorDebugAdapterFactory#getBreakpointAdapter()
	 */
	@Override
	protected ScriptToggleBreakpointAdapter getBreakpointAdapter() {
		return new LuaToggleBreakpointAdapter();
	}

}
