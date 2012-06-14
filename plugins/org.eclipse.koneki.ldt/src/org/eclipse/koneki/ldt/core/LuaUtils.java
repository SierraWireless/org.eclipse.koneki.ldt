/*******************************************************************************
 * Copyright (c) 2011-2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.koneki.ldt.core.internal.Activator;
import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironmentBuildpathUtil;

/**
 * Utility class for Lua
 */
public final class LuaUtils {

	private LuaUtils() {
	}

	/**
	 * @return name of the module (without package qualifier) (no support of init.lua)
	 */
	public static String getModuleName(ISourceModule module) {
		String moduleName = module.getElementName();
		if (moduleName.endsWith(".lua")) { //$NON-NLS-1$
			moduleName = moduleName.replaceFirst("\\.lua$", ""); //$NON-NLS-1$//$NON-NLS-2$
		}
		return moduleName;
	}

	/**
	 * @return name of the module (without package qualifier) (no support of init.lua)
	 */
	public static String getModuleName(final IModuleSource module) {
		final IModelElement modelElement = module.getModelElement();
		if (modelElement instanceof ISourceModule) {
			return getModuleName((ISourceModule) modelElement);
		} else {
			return module.getFileName();
		}
	}

	/**
	 * @return full name of a module with dot syntax (support init.lua case)<br>
	 * 
	 *         e.g. : socket.core
	 */
	public static String getModuleFullName(final IModuleSource module) {
		final IModelElement modelElement = module.getModelElement();
		if (modelElement instanceof ISourceModule) {
			return getModuleFullName((ISourceModule) modelElement);
		} else {
			return module.getFileName();
		}
	}

	/**
	 * @return full name of a module with dot syntax (support init.lua case) <br>
	 * 
	 *         e.g. : socket.core
	 */
	public static String getModuleFullName(final ISourceModule module) {
		String moduleName = getModuleName(module);

		// get prefix
		String prefix = null;
		if (module.getParent() instanceof IScriptFolder) {
			prefix = getFolderFullName((IScriptFolder) module.getParent());
		}

		if (prefix != null)
			if ("init".equalsIgnoreCase(moduleName))//$NON-NLS-1$
				return prefix;
			else
				return prefix + "." + moduleName; //$NON-NLS-1$
		else
			return moduleName;
	}

	/*
	 * @return the source folder full name with module dot syntax
	 */
	private static String getFolderFullName(final IScriptFolder folder) {
		if (!folder.isRootFolder()) {
			// get folder name
			final String folderName = folder.getElementName().replace("/", "."); //$NON-NLS-1$//$NON-NLS-2$

			// get prefix
			final IModelElement parent = folder.getParent();
			String prefix = null;
			if (parent instanceof IScriptFolder) {
				prefix = getFolderFullName((IScriptFolder) parent) + "."; //$NON-NLS-1$
			}

			if (prefix != null)
				return prefix + "." + folderName; //$NON-NLS-1$
			else
				return folderName;
		}
		return null;
	}

	/**
	 * @return the {@link IModuleSource} from full name with module dot syntax
	 */
	public static IModuleSource getModuleSource(final String name, final IScriptProject project) {
		if (project == null && name == null || name.isEmpty())
			return null;

		// search in all source path.
		IProjectFragment[] allProjectFragments;
		try {
			allProjectFragments = project.getAllProjectFragments();
			for (final IProjectFragment projectFragment : allProjectFragments) {
				final IModuleSource moduleSource = getModuleSource(name, projectFragment);
				if (moduleSource != null)
					return moduleSource;
			}
		} catch (final ModelException e) {
			Activator.logError(MessageFormat.format("Unable to find module: {0}.", name), e); //$NON-NLS-1$
			return null;
		}
		return null;
	}

	/*
	 * @return the {@link IModuleSource} from full name with module dot syntax
	 */
	private static IModuleSource getModuleSource(final String name, final IParent parent) throws ModelException {
		final IModelElement[] children = parent.getChildren();
		for (final IModelElement child : children) {
			if (child instanceof IModuleSource) {
				if (name.equals(getModuleFullName((IModuleSource) child))) {
					return (IModuleSource) child;
				}
			} else if (child instanceof IParent) {
				final IModuleSource moduleSource = getModuleSource(name, (IParent) child);
				if (moduleSource != null)
					return moduleSource;
			}

		}
		return null;
	}

	/**
	 * @return the {@link ISourceModule} from full name with module dot syntax
	 */
	public static ISourceModule getSourceModule(final String name, final IScriptProject project) {
		final IModuleSource moduleSource = getModuleSource(name, project);
		if (moduleSource instanceof ISourceModule) {
			return (ISourceModule) moduleSource;
		}
		return null;
	}

	/**
	 * @return the {@link IModuleSource} from Absolute local file URI
	 */
	public static IModuleSource getModuleSourceFromAbsoluteURI(final URI absolutepath, final IScriptProject project) {
		if (project == null || absolutepath == null)
			return null;

		final ISourceModule sourceModule = getSourceModuleFromAbsoluteURI(absolutepath, project);
		if (sourceModule instanceof IModuleSource) {
			return (IModuleSource) sourceModule;
		}
		return null;
	}

	/**
	 * @return the {@link ISourceModule} from Absolute local file URI
	 */
	public static ISourceModule getSourceModuleFromAbsoluteURI(final URI absolutepath, final IScriptProject project) {
		if (project == null || absolutepath == null)
			return null;

		// search in all source path.
		IProjectFragment[] allProjectFragments;
		try {
			allProjectFragments = project.getAllProjectFragments();
			for (final IProjectFragment projectFragment : allProjectFragments) {
				final ISourceModule moduleSource = getSourceModuleFromAbsolutePath(absolutepath, projectFragment);
				if (moduleSource != null)
					return moduleSource;
			}
		} catch (final ModelException e) {
			Activator.logError(MessageFormat.format("Unable to find module: {0}.", absolutepath), e); //$NON-NLS-1$
			return null;
		}
		return null;
	}

	/*
	 * @return the {@link ISourceModule} from Absolute local file URI and a parent
	 */
	private static ISourceModule getSourceModuleFromAbsolutePath(final URI absolutepath, final IParent parent) throws ModelException {
		final IModelElement[] children = parent.getChildren();
		for (final IModelElement child : children) {
			if (child instanceof ISourceModule) {
				if (URIUtil.sameURI(absolutepath, getModuleAbsolutePath((ISourceModule) child))) {
					return (ISourceModule) child;
				}
			} else if (child instanceof IParent) {
				final ISourceModule moduleSource = getSourceModuleFromAbsolutePath(absolutepath, (IParent) child);
				if (moduleSource != null)
					return moduleSource;
			}

		}
		return null;
	}

	/**
	 * @return Absolute local file URI of a module source
	 */
	public static URI getModuleAbsolutePath(final ISourceModule module) {
		if (module instanceof IExternalSourceModule) {
			String path = EnvironmentPathUtils.getLocalPath(module.getPath()).toString();
			if (path.length() != 0 && path.charAt(0) != '/') {
				path = '/' + path;
			}
			try {
				return new URI("file", "", path, null); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (final URISyntaxException e) {
				final String message = MessageFormat.format("Unable to get file uri for external module : {0}.", module.getPath()); //$NON-NLS-1$
				Activator.logWarning(message, e);
			}
		} else {
			if (module.getResource() != null)
				return module.getResource().getLocationURI();
		}
		return null;
	}

	/**
	 * @return the list of direct project dependencies
	 * @throws ModelException
	 */
	public static List<IScriptProject> getDependencies(final IScriptProject project) throws ModelException {
		final ArrayList<IScriptProject> result = new ArrayList<IScriptProject>();
		// check in all project fragments
		final IProjectFragment[] projectFragments = project.getAllProjectFragments();
		for (int i = 0; i < projectFragments.length; i++) {
			final IProjectFragment projectFragment = projectFragments[i];
			if (isProjectDependencyFragment(project, projectFragment)) {
				final IScriptProject currentScriptProject = projectFragment.getScriptProject();
				result.add(currentScriptProject);
			}
		}
		return result;
	}

	public static boolean isProjectDependencyFragment(final IScriptProject project, final IProjectFragment projectFragment) throws ModelException {
		final IScriptProject fragmentProject = projectFragment.getScriptProject();
		if (fragmentProject != null && fragmentProject != project) {
			return (!projectFragment.isArchive() && !projectFragment.isBinary() && !projectFragment.isExternal());
		} else {
			return false;
		}
	}

	public static boolean isExecutionEnvironmentFragment(final IProjectFragment projectFragment) throws ModelException {
		final IBuildpathEntry rawBuildpathEntry = projectFragment.getRawBuildpathEntry();
		return (rawBuildpathEntry != null && LuaExecutionEnvironmentBuildpathUtil.isValidExecutionEnvironmentBuildPath(rawBuildpathEntry.getPath()));
	}

	public enum ProjectFragmentFilter {
		EXECUTION_ENVIRONMENT, DEPENDENT_PROJECT, ARCHIVE
	}

	/** Enable to perform operation in all files and directories in project fragments source directories */
	public static void visitSourceFiles(final IScriptProject project, EnumSet<ProjectFragmentFilter> filter, final IProjectSourceVisitor visitor,
			final IProgressMonitor monitor) throws CoreException {

		SubMonitor subMonitor = SubMonitor.convert(monitor, 10);

		ArrayList<IProjectFragment> filteredProjecFragment = new ArrayList<IProjectFragment>();

		// filter project fragment
		final IProjectFragment[] projectFragments = project.getAllProjectFragments();
		final SubMonitor filteredLoopMonitor = subMonitor.newChild(1).setWorkRemaining(projectFragments.length);

		for (int i = 0; i < projectFragments.length && !monitor.isCanceled(); i++) {
			final IProjectFragment projectFragment = projectFragments[i];

			if (isProjectDependencyFragment(project, projectFragment)) {
				if (filter.contains(ProjectFragmentFilter.DEPENDENT_PROJECT))
					filteredProjecFragment.add(projectFragment);
			} else if (isExecutionEnvironmentFragment(projectFragment)) {
				if (filter.contains(ProjectFragmentFilter.EXECUTION_ENVIRONMENT))
					filteredProjecFragment.add(projectFragment);
			} else if (projectFragment.isArchive()) {
				if (filter.contains(ProjectFragmentFilter.ARCHIVE))
					filteredProjecFragment.add(projectFragment);
			} else {
				filteredProjecFragment.add(projectFragment);
			}
			filteredLoopMonitor.worked(1);
		}

		// visit fragment
		final SubMonitor visitLoopMonitor = subMonitor.newChild(9).setWorkRemaining(filteredProjecFragment.size());
		for (IProjectFragment projectFragment : filteredProjecFragment) {
			if (monitor.isCanceled())
				return;
			visitSourceFiles(projectFragment, visitor, visitLoopMonitor.newChild(1));
		}
	}

	/** Enable to perform operation in all files and directories in project fragments source directories */
	// TODO make it private
	@Deprecated
	public static void visitSourceFiles(final IParent parent, final IProjectSourceVisitor visitor, final IProgressMonitor monitor)
			throws CoreException {
		visitSourceFiles(parent, visitor, monitor, Path.EMPTY);
	}

	private static void visitSourceFiles(final IParent parent, final IProjectSourceVisitor visitor, final IProgressMonitor monitor,
			final IPath currentPath) throws CoreException {

		final IModelElement[] children = parent.getChildren();

		SubMonitor subMonitor = SubMonitor.convert(monitor, children.length);

		for (int i = 0; i < children.length && !monitor.isCanceled(); i++) {
			final IModelElement modelElement = children[i];
			if (modelElement instanceof ISourceModule) {

				/*
				 * Support local module
				 */
				final IResource resource = modelElement.getResource();
				IPath absolutePath;
				String charset;
				if (resource instanceof IFile) {
					final IFile file = (IFile) resource;
					absolutePath = new Path(resource.getLocationURI().getPath());
					charset = file.getCharset();
				} else {
					absolutePath = getAbsolutePathFromModelElement(modelElement);
					charset = Charset.defaultCharset().toString();
				}
				final IPath relativeFilePath = currentPath.append(absolutePath.lastSegment());
				visitor.processFile(absolutePath, relativeFilePath, charset, subMonitor.newChild(1));
			} else if (modelElement instanceof IScriptFolder) {

				/*
				 * Support source folder
				 */
				final IScriptFolder innerSourceFolder = (IScriptFolder) modelElement;
				// Do not notify interface for Source folders
				if (!innerSourceFolder.isRootFolder()) {
					final IResource resource = innerSourceFolder.getResource();
					IPath absolutePath;
					if (resource != null) {
						absolutePath = new Path(resource.getLocationURI().getPath());
					} else {
						absolutePath = getAbsolutePathFromModelElement(modelElement);
					}

					final IPath newPath = currentPath.append(innerSourceFolder.getElementName());
					visitor.processDirectory(absolutePath, newPath, monitor);
					visitSourceFiles(innerSourceFolder, visitor, subMonitor.newChild(1), newPath);
				} else {
					// Deal with sub elements
					visitSourceFiles(innerSourceFolder, visitor, subMonitor.newChild(1));
				}
			}
		}
	}

	private static IPath getAbsolutePathFromModelElement(final IModelElement modelElement) throws CoreException {
		final IPath folderPath = modelElement.getPath();
		if (EnvironmentPathUtils.isFull(folderPath)) {
			return EnvironmentPathUtils.getLocalPath(folderPath);
		} else {
			final String message = MessageFormat.format("Unable to get absolute location for {0}.", modelElement.getElementName()); //$NON-NLS-1$
			final Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
			throw new CoreException(status);
		}
	}

	/**
	 * @return all Open Lua project in the workspace
	 */
	public static final IProject[] getLuaProjects() {
		List<IProject> luaProjects = new LinkedList<IProject>();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject iProject : projects) {
			try {
				if (iProject.isAccessible() && iProject.hasNature(LuaNature.ID)) {
					luaProjects.add(iProject);
				}
			} catch (CoreException e) {
				// must not append
				Activator.logWarning("Unexcepted error when collecting Lua project", e); //$NON-NLS-1$
			}
		}
		return luaProjects.toArray(new IProject[luaProjects.size()]);
	}

	public static boolean isLuaProject(IProject project) {
		try {
			return project.hasNature(LuaNature.ID);
		} catch (CoreException e) {
			// must not append
			Activator.logWarning("Unexcepted error when checking if project is a Lua project", e); //$NON-NLS-1$
		}
		return false;
	}
}
