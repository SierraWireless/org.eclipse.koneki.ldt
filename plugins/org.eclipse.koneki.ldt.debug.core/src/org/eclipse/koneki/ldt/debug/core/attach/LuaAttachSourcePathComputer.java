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
package org.eclipse.koneki.ldt.debug.core.attach;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;
import org.eclipse.dltk.launching.sourcelookup.ScriptSourcePathComputer;
import org.eclipse.koneki.ldt.debug.core.LuaAbsoluteFileURIBuildpathSourceContainer;
import org.eclipse.koneki.ldt.debug.core.LuaDebugConstant;
import org.eclipse.koneki.ldt.debug.core.LuaModuleURIBuildpathSourceContainer;
import org.eclipse.koneki.ldt.debug.core.LuaReplacePathSourceContainer;

public class LuaAttachSourcePathComputer extends ScriptSourcePathComputer {

	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		String mappingType = LaunchConfigurationUtils.getString(configuration, LuaDebugConstant.ATTR_LUA_SOURCE_MAPPING_TYPE,
				LuaDebugConstant.LOCAL_MAPPING_TYPE);

		if (mappingType.equals(LuaDebugConstant.MODULE_MAPPING_TYPE)) {
			return new ISourceContainer[] { new LuaModuleURIBuildpathSourceContainer() };
		} else if (mappingType.equals(LuaDebugConstant.REPLACE_PATH_MAPPING_TYPE)) {
			return new ISourceContainer[] { new LuaReplacePathSourceContainer() };
		} else {
			return new ISourceContainer[] { new LuaAbsoluteFileURIBuildpathSourceContainer() };
		}

	}
}
