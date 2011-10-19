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


package org.eclipse.koneki.ldt.ui.interpreter.preferences;

import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.ScriptInterpreterPreferencePage;
import org.eclipse.koneki.ldt.ui.interpreter.preferences.LuaInterpretersBlock;


public class LuaInterpreterPreferencePage extends
		ScriptInterpreterPreferencePage {
	
	public static final String PAGE_ID =
			"org.eclipse.koneki.ldt.interpreter.preferences";

	@Override
	public InterpretersBlock createInterpretersBlock() {
		return new LuaInterpretersBlock();
	}

}
