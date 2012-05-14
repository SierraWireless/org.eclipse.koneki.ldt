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
package org.eclipse.koneki.ldt.core.buildpath;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.internal.core.BuildpathEntry;
import org.eclipse.koneki.ldt.Activator;

@SuppressWarnings("restriction")
public class LuaExecutionEnvironmentBuildpathContainer implements IBuildpathContainer {

	private final IPath path;
	private String description;

	public LuaExecutionEnvironmentBuildpathContainer(String eeID, String eeVersion, IPath path) {
		this.path = path;
	}

	@Override
	public IBuildpathEntry[] getBuildpathEntries() {
		try {
			final List<IPath> eeBuildPathes = LuaExecutionEnvironmentBuildpathUtil.getExecutionEnvironmentBuildPath(path);
			final ArrayList<IBuildpathEntry> arrayList = new ArrayList<IBuildpathEntry>(eeBuildPathes.size());
			if (!eeBuildPathes.isEmpty()) {
				for (final IPath buildPath : eeBuildPathes) {
					final IBuildpathEntry libEntry = DLTKCore.newLibraryEntry(buildPath, IAccessRule.EMPTY_RULES, new IBuildpathAttribute[0],
							BuildpathEntry.INCLUDE_ALL, BuildpathEntry.EXCLUDE_NONE, false, true);
					arrayList.add(libEntry);
				}
				return arrayList.toArray(new IBuildpathEntry[arrayList.size()]);
			}
		} catch (final CoreException e) {
			Activator.log(e.getStatus());
		}
		return new IBuildpathEntry[0];
	}

	@Override
	public String getDescription() {
		// Provide available description
		if (description != null) {
			return description;
		}
		/*
		 * Retrieve name and version from Execution Environment
		 */
		String id = null;
		String version = null;
		boolean isFromManifest = false;
		try {
			final LuaExecutionEnvironment ee = LuaExecutionEnvironmentBuildpathUtil.getExecutionEnvironment(path);
			if ((ee != null) && (ee.getID() != null)) {
				id = ee.getID();
				version = ee.getVersion();
				isFromManifest = true;
			}
		} catch (final CoreException e) {
			Activator.log(e.getStatus());
		}

		/*
		 * In case of failure we can still extract name and version from given path
		 */
		if (id == null && version == null && path != null && (path.segmentCount() > 2)) {
			final int length = path.segmentCount();
			id = path.segment(length - 2);
			version = path.segment(length - 1);
		}

		/*
		 * Compute description
		 */
		if (id != null && version != null) {
			final StringBuffer sb = new StringBuffer();
			// Appending ID with capital first letter
			if (id.length() > 0) {
				sb.append(id.substring(0, 1).toUpperCase());
				if (id.length() > 1) {
					sb.append(id.substring(1));
				}
				sb.append(' ');
			}
			sb.append(version);
			String result = sb.toString();
			if (isFromManifest) {
				// Execution Environment is valid and description stored for future calls
				description = result;
			} else {
				// A problem occurred while seeking for Execution Environment,
				// description may need to be refreshed at next call.
				result = Messages.bind(Messages.LuaExecutionEnvironmentBuildpathContainerEENotFound, result);
			}
			return result;
		}
		// No data to compute description is available
		return Messages.LuaExecutionEnvironmentBuildpathContainerNoDescriptionAvailable;
	}

	@Override
	public int getKind() {
		// Not called at project creation nor project load.
		// Defined just in case ...
		return IBuildpathContainer.K_DEFAULT_SYSTEM;
	}

	@Override
	public IPath getPath() {
		return path;
	}

}
