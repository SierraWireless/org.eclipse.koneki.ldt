/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 ******************************************************************************/
package org.eclipse.koneki.ldt.ui.propertytesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.internal.ui.scriptview.BuildPathContainer;
import org.eclipse.koneki.ldt.core.buildpath.LuaExecutionEnvironmentBuildpathUtil;

@SuppressWarnings("restriction")
public class LuaExecutionEnvironmentContainerPropertyTester extends PropertyTester {

	private static final String PROPERTY_ID = "containsLuaExecutionEnvironmentContainer"; //$NON-NLS-1$ 

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		if (PROPERTY_ID.equals(property) && (receiver instanceof BuildPathContainer)) {

			// Extract build path container path
			final BuildPathContainer container = (BuildPathContainer) receiver;
			final IPath entryPath = container.getBuildpathEntry().getPath();

			// Check if it is a valid Execution Environment path
			return LuaExecutionEnvironmentBuildpathUtil.isLuaExecutionEnvironmentContainer(entryPath);
		}
		return false;
	}

}
