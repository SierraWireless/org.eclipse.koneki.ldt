/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local.tab;

import org.eclipse.dltk.debug.ui.launchConfigurations.ScriptArgumentsTab;
import org.eclipse.dltk.internal.debug.ui.launcher.WorkingDirectoryBlock;

public class LuaArgumentsTab extends ScriptArgumentsTab {
	@Override
	protected WorkingDirectoryBlock createWorkingDirBlock() {
		return new LuaArgumentsTabWorkingDirectoryBlock();
	}
}
