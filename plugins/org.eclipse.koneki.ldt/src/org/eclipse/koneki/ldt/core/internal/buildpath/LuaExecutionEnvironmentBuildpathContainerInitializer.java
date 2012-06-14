package org.eclipse.koneki.ldt.core.internal.buildpath;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.BuildpathContainerInitializer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IScriptProject;

public class LuaExecutionEnvironmentBuildpathContainerInitializer extends BuildpathContainerInitializer {

	@Override
	public void initialize(IPath containerPath, IScriptProject project) throws CoreException {

		// Extract name from path
		final String eeID = LuaExecutionEnvironmentBuildpathUtil.getEEID(containerPath);

		// Extract version from path
		final String eeVersion = LuaExecutionEnvironmentBuildpathUtil.getEEVersion(containerPath);

		// Set actual build path container
		final LuaExecutionEnvironmentBuildpathContainer container = new LuaExecutionEnvironmentBuildpathContainer(eeID, eeVersion, containerPath);
		DLTKCore.setBuildpathContainer(containerPath, new IScriptProject[] { project }, new IBuildpathContainer[] { container }, null);
	}

	@Override
	public boolean canUpdateBuildpathContainer(IPath containerPath, IScriptProject project) {
		return LuaExecutionEnvironmentBuildpathUtil.isLuaExecutionEnvironmentContainer(containerPath);
	}

	@Override
	public Object getComparisonID(IPath containerPath, IScriptProject project) {
		return containerPath;
	}
}
