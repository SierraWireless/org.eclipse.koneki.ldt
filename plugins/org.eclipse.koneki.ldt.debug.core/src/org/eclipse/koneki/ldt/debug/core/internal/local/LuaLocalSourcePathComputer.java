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
package org.eclipse.koneki.ldt.debug.core.internal.local;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.dltk.launching.sourcelookup.ScriptSourcePathComputer;
import org.eclipse.koneki.ldt.debug.core.internal.LuaAbsoluteFileURIBuildpathSourceContainer;

/**
 * This class is responsible to find the object (in most of case IResource) for a given sourcename.<br>
 * The sourceName is calculated by the ISourceLookupDirector (LuaSourceLookupDirector)<br>
 * In our case this sourceName is always a string with the URI format.
 */
public class LuaLocalSourcePathComputer extends ScriptSourcePathComputer {

	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		return new ISourceContainer[] { new LuaAbsoluteFileURIBuildpathSourceContainer() };
	}
}
