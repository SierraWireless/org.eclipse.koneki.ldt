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
package org.eclipse.koneki.ldt.core.buildpath;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.koneki.ldt.Activator;

public final class LuaExecutionEnvironmentManager {
	private static final String INSTALLATION_FOLDER = "ee"; //$NON-NLS-1$

	private LuaExecutionEnvironmentManager() {

	}

	private static LuaExecutionEnvironment getExecutionEnvironmentFromCompressedFile(final String filePath) throws CoreException {
		/*
		 * Extract manifest file
		 */
		ZipFile zipFile = null;
		String manifestString = null;
		try {
			zipFile = new ZipFile(filePath);
			final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			while (zipEntries.hasMoreElements()) {
				final ZipEntry zipEntry = zipEntries.nextElement();
				if ((!zipEntry.getName().contains("/")) && zipEntry.getName().endsWith(LuaExecutionEnvironmentConstants.MANIFEST_EXTENSION)) { //$NON-NLS-1$

					// check there are only one manifest.
					if (manifestString != null) {
						throwException(
								MessageFormat
										.format("Invalid Execution Environment : more than one \"{0}\" file.", LuaExecutionEnvironmentConstants.MANIFEST_EXTENSION), null, IStatus.ERROR); //$NON-NLS-1$
					}

					// read manifest
					final InputStream input = zipFile.getInputStream(zipEntry);
					manifestString = IOUtils.toString(input);
				}
			}
		} catch (IOException e) {
			throwException(MessageFormat.format("Unable to extract manifest from zip file {0}", filePath), e, IStatus.ERROR); //$NON-NLS-1$
		} finally {
			if (zipFile != null)
				try {
					zipFile.close();
				} catch (IOException e) {
					Activator.logWarning(MessageFormat.format("Unable to close zip file {0}", filePath), e); //$NON-NLS-1$
				}
		}

		// if no manifest extract
		if (manifestString == null) {
			throwException(MessageFormat.format("No manifest \"{0}\" file found", //$NON-NLS-1$
					LuaExecutionEnvironmentConstants.MANIFEST_EXTENSION), null, IStatus.ERROR);
		}

		return getLuaExecutionEnvironmentFromManifest(manifestString);
	}

	private static LuaExecutionEnvironment getInstalledExecutionEnvironmentFromDir(final File executionEnvironmentDirectory) throws CoreException {
		// check if the directory exist
		if (!executionEnvironmentDirectory.exists() || !executionEnvironmentDirectory.isDirectory())
			return null;

		// list manifest files
		String manifestString = null;
		File[] manifests = executionEnvironmentDirectory.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(LuaExecutionEnvironmentConstants.MANIFEST_EXTENSION);
			}
		});

		// check number of manifest file
		if (manifests == null || manifests.length != 1) {
			final String message = MessageFormat.format("0 or more than 1 \"{0}\" file in given file.", //$NON-NLS-1$
					LuaExecutionEnvironmentConstants.MANIFEST_EXTENSION);
			throwException(message, null, IStatus.ERROR);
		}

		// try to read it
		InputStream manifestInputStream = null;
		try {
			manifestInputStream = new FileInputStream(manifests[0]);
			manifestString = IOUtils.toString(manifestInputStream);
		} catch (IOException e) {
			throwException("Unable to read manifest file.", e, IStatus.ERROR); //$NON-NLS-1$
		} finally {
			if (manifestInputStream != null)
				try {
					manifestInputStream.close();
				} catch (IOException e) {
					Activator.logWarning(MessageFormat.format("Unable to close file {0}", manifests[0]), e); //$NON-NLS-1$
				}

		}

		// extract execution environment from manifest
		return getLuaExecutionEnvironmentFromManifest(manifestString);
	}

	private static LuaExecutionEnvironment getLuaExecutionEnvironmentFromManifest(String manifestString) throws CoreException {
		/*
		 * Match available package name
		 */
		Pattern namePattern = Pattern.compile("(?:^|\\s+)package\\s*=\\s*(?:[\"']|\\[*)([\\w.]+)"); //$NON-NLS-1$
		String name = null;
		Matcher matcher = namePattern.matcher(manifestString);
		if (matcher.find() && matcher.groupCount() > 0) {
			name = matcher.group(1);
		}

		/*
		 * Match available version
		 */
		String version = null;
		namePattern = Pattern.compile("(?:^|\\s+)version\\s*=\\s*(?:[\"']|\\[*)([\\w.]+)"); //$NON-NLS-1$
		matcher = namePattern.matcher(manifestString);
		if (matcher.find() && matcher.groupCount() > 0) {
			version = matcher.group(1);
		}

		// Create object representing a valid Execution Environment
		if (name == null || version == null) {
			throwException("Manifest from given file has no package name or version.", null, IStatus.ERROR); //$NON-NLS-1$
		}

		// get install path
		final IPath pathToEE = getInstallDirectory().append(name + '-' + version);

		return new LuaExecutionEnvironment(name, version, pathToEE);
	}

	public static void removeLuaExecutionEnvironment(final LuaExecutionEnvironment ee) throws CoreException {
		if (ee == null)
			throwException("No Execution Environment provided.", null, IStatus.ERROR); //$NON-NLS-1$
		final IPath pathToEE = ee.getPath();
		if (pathToEE == null)
			throwException("The install path should not be null", null, IStatus.ERROR); //$NON-NLS-1$
		final File eeInstallationDir = pathToEE.toFile();

		if (eeInstallationDir.exists()) {
			try {
				FileUtils.deleteDirectory(eeInstallationDir);
				refreshDLTKModel(ee);
			} catch (final IOException e) {
				throwException(MessageFormat.format("Unable to delete install directory : {0}", pathToEE.toOSString()), e, IStatus.ERROR); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Will deploy files from a valid Execution Environment file in installation directory. File will be considered as installed when its name will be
	 * appended in {@link LuaExecutionEnvironmentConstants#PREF_EXECUTION_ENVIRONMENTS_LIST}
	 * 
	 * @param zipPath
	 *            Path to file to deploy
	 * @return {@link LuaExecutionEnvironmentException} when deployment succeeded.
	 * @throws CoreException
	 */
	public static LuaExecutionEnvironment installLuaExecutionEnvironment(final String zipPath) throws CoreException {
		/*
		 * Ensure there are no folder named like the one we are about to create
		 */
		LuaExecutionEnvironment ee = null;
		ee = getExecutionEnvironmentFromCompressedFile(zipPath);

		if (ee == null)
			throwException(MessageFormat.format("Unable to extract execution environment information from {0}.", zipPath), null, IStatus.ERROR); //$NON-NLS-1$

		// check if it is already installed
		if (getInstalledExecutionEnvironments().contains(ee)) {
			throwException("Execution environment is already installed.", null, IStatus.ERROR); //$NON-NLS-1$
		}

		// prepare/clean the directory where the Execution environment will be installed
		final IPath eePath = ee.getPath();
		if (eePath == null)
			throwException("The install path should not be null.", null, IStatus.ERROR); //$NON-NLS-1$

		final File installDirectory = eePath.toFile();
		// clean install directory if it exists
		if (installDirectory.exists()) {
			if (installDirectory.isFile()) {
				if (!installDirectory.delete())
					throwException(MessageFormat.format("Unable to clean installation directory : {0}", eePath.toOSString()), null, IStatus.ERROR); //$NON-NLS-1$
			} else {
				try {
					FileUtils.deleteDirectory(installDirectory);
				} catch (IOException e) {
					throwException(MessageFormat.format("Unable to clean installation directory : {0}", eePath.toOSString()), e, IStatus.ERROR); //$NON-NLS-1$
				}
			}
		}

		// in all case create the install directory
		if (!installDirectory.mkdirs()) {
			throwException(MessageFormat.format("Unable to create installation directory : {0}", eePath.toOSString()), null, IStatus.ERROR); //$NON-NLS-1$
		}

		// Extract Execution environment from zip
		// Loop over content
		ZipInputStream zipStream = null;
		FileInputStream input = null;
		try {
			// Open given file
			input = new FileInputStream(zipPath);
			zipStream = new ZipInputStream(new BufferedInputStream(input));

			for (ZipEntry entry = zipStream.getNextEntry(); entry != null; entry = zipStream.getNextEntry()) {
				/*
				 * Flush current file on disk
				 */
				final File outputFile = new File(installDirectory, entry.getName());
				// Define output file
				if (entry.isDirectory()) {
					// Create sub directory if needed
					if (!outputFile.mkdir()) {
						throwException(MessageFormat.format("Unable to create install directory {0}", outputFile.toString()), null, IStatus.ERROR); //$NON-NLS-1$
					}
				} else {
					// copy file
					FileOutputStream fileOutputStream = null;
					try {
						fileOutputStream = new FileOutputStream(outputFile);
						// Inflate file
						IOUtils.copy(zipStream, fileOutputStream);

						// Flush on disk
						fileOutputStream.flush();
					} finally {
						if (fileOutputStream != null) {
							try {
								fileOutputStream.close();
							} catch (IOException e) {
								Activator.logWarning(MessageFormat.format("Unable to close file {0}", outputFile), e); //$NON-NLS-1$
							}

						}
					}
				}
			}
		} catch (IOException e) {
			throwException(MessageFormat.format("Unable to extract zip file : {0}", zipPath), e, IStatus.ERROR); //$NON-NLS-1$
		} finally {
			/*
			 * Make sure all streams are closed
			 */
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Activator.logWarning(MessageFormat.format("Unable to close file {0}", zipPath), e); //$NON-NLS-1$
				}
			}
			if (zipStream != null) {
				try {
					zipStream.close();
				} catch (IOException e) {
					Activator.logWarning(MessageFormat.format("Unable to close file {0}", zipPath), e); //$NON-NLS-1$
				}
			}
		}

		// try to get installed Execution Environment to be sure, it is well installed
		getInstalledExecutionEnvironment(ee.getID(), ee.getVersion());

		refreshDLTKModel(ee);
		return ee;
	}

	private static IPath getInstallDirectory() {
		return Activator.getDefault().getStateLocation().append(INSTALLATION_FOLDER);
	}

	public static List<LuaExecutionEnvironment> getInstalledExecutionEnvironments() {
		// list of execution environment installed
		final ArrayList<LuaExecutionEnvironment> result = new ArrayList<LuaExecutionEnvironment>();

		// search in the install directory
		IPath installDirectoryPath = getInstallDirectory();
		File installDirectory = installDirectoryPath.toFile();
		if (installDirectory.exists() && installDirectory.isDirectory()) {
			File[] content = installDirectory.listFiles();
			for (File executionEnvironmentDirectory : content) {
				if (executionEnvironmentDirectory.exists() && executionEnvironmentDirectory.isDirectory()) {
					LuaExecutionEnvironment executionEnvironment;
					try {
						executionEnvironment = getInstalledExecutionEnvironmentFromDir(executionEnvironmentDirectory);
						if (executionEnvironment != null)
							result.add(executionEnvironment);
					} catch (CoreException e) {
						Activator.logWarning(MessageFormat.format("Unable to extract execution environment from {0}", executionEnvironmentDirectory), //$NON-NLS-1$
								e);
					}
				}
			}
		}

		return result;
	}

	private static IPath getLuaExecutionEnvironmentPath(final String name, final String version) {
		return getInstallDirectory().append(name + '-' + version);
	}

	public static LuaExecutionEnvironment getInstalledExecutionEnvironment(String name, String version) throws CoreException {
		IPath luaExecutionEnvironmentPath = getLuaExecutionEnvironmentPath(name, version);
		return getInstalledExecutionEnvironmentFromDir(luaExecutionEnvironmentPath.toFile());
	}

	private static void refreshDLTKModel(LuaExecutionEnvironment ee) {
		try {
			// get path for this execution environment
			IPath containerPath = LuaExecutionEnvironmentBuildpathUtil.getLuaExecutionEnvironmentContainerPath(ee);

			// find all project which references it
			IScriptProject[] scriptProjects = DLTKCore.create(ResourcesPlugin.getWorkspace().getRoot()).getScriptProjects();
			ArrayList<IScriptProject> affectedProjects = new ArrayList<IScriptProject>();
			for (int i = 0; i < scriptProjects.length; i++) {
				IScriptProject scriptProject = scriptProjects[i];
				IBuildpathEntry[] entries = scriptProject.getRawBuildpath();
				for (int j = 0; j < entries.length; j++) {
					IBuildpathEntry entry = entries[j];
					if (entry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER) {
						if (containerPath.equals(entry.getPath())) {
							affectedProjects.add(scriptProject);
							break;
						}
					}
				}
			}

			// update affected projects
			int length = affectedProjects.size();
			if (length == 0)
				return;
			IScriptProject[] projects = new IScriptProject[length];
			affectedProjects.toArray(projects);
			IBuildpathContainer[] containers = new IBuildpathContainer[length];
			if (ee != null) {
				LuaExecutionEnvironmentBuildpathContainer container = new LuaExecutionEnvironmentBuildpathContainer(ee.getID(), ee.getVersion(),
						containerPath);
				for (int i = 0; i < length; i++) {
					containers[i] = container;
				}
			}
			DLTKCore.setBuildpathContainer(containerPath, projects, containers, null);
		} catch (ModelException e) {
			Activator.logError("Unable to refresh Model after execution environment change", e); //$NON-NLS-1$
		}
	}

	private static void throwException(String message, Throwable t, int severity) throws CoreException {
		throw new CoreException(new Status(severity, Activator.PLUGIN_ID, message, t));
	}
}
