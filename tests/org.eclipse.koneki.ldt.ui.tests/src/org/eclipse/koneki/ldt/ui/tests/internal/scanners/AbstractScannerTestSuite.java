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
package org.eclipse.koneki.ldt.ui.tests.internal.scanners;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.koneki.ldt.ui.tests.internal.Activator;
import org.osgi.framework.Bundle;

/**
 * Launch a bench of TestCase for each file to test in the given folder.
 */
public abstract class AbstractScannerTestSuite extends TestSuite {

	private List<String> blacklist;

	/**
	 * @param name
	 *            Name of the suite
	 * @param folderPath
	 *            The project relative path of the folder containing inputs, references and test files
	 * @param referenceFileExtension
	 *            Extension of the reference file (e.g. "lua" or "serialized")
	 * @param ignore
	 */
	public AbstractScannerTestSuite(final String name, final String folderPath, final String referenceFileExtension, boolean ignore) {
		super();
		setName(name);

		try {
			// Retrieve folder
			final Bundle bundle = Activator.getDefault().getBundle();
			final URL ressource = bundle.getResource(folderPath);
			final Path folderAbsolutePath = new Path(FileLocator.toFileURL(ressource).getPath());

			// check test suite folder
			checkFolder(folderAbsolutePath, "This is not a directory and cannot contain test suite files and folders."); //$NON-NLS-1$

			// check input folder
			final File inputFolder = checkFolder(folderAbsolutePath.append(getInputFolderPath()),
					"This is not a directory and cannot contain test lua input files."); //$NON-NLS-1$

			// check reference folder
			final File referenceFolder = checkFolder(folderAbsolutePath.append(getReferenceFolderPath()),
					"This is not a directory and cannot contain test reference files."); //$NON-NLS-1$

			// Retrieve files
			for (final File inputFile : getRecursiveFileList(inputFolder)) {

				// Compute relative file path
				final IPath inputFilePath = new Path(inputFile.getCanonicalPath());
				final IPath relativeToFolderPath = inputFilePath.makeRelativeTo(new Path(inputFolder.getCanonicalPath()));

				// Build reference file path
				IPath referenceFilePath = new Path(referenceFolder.getCanonicalPath()).append(relativeToFolderPath);
				referenceFilePath = referenceFilePath.removeFileExtension();
				referenceFilePath = referenceFilePath.addFileExtension(referenceFileExtension);
				final File referenceFile = new File(referenceFilePath.toOSString());

				// Compute testName
				final String testName = MessageFormat.format("{0}#{1}", getName(), relativeToFolderPath.toOSString()); //$NON-NLS-1$

				// Append test case and ignore blacklisted files
				if (!(ignore && getTestBlacklisted().contains(relativeToFolderPath.toPortableString()))) {
					addTest(createTestCase(testName, inputFile, referenceFile));
				}
			}
		} catch (final IOException e) {
			final String message = MessageFormat.format("Unable to locate {0}.", folderPath); //$NON-NLS-1$
			raiseRuntimeException(message, e);
		}
	}

	private List<String> getTestBlacklisted() {
		if (blacklist == null) {
			blacklist = createTestBlacklist();
		}
		return blacklist;
	}

	protected List<String> createTestBlacklist() {
		return Collections.<String> emptyList();
	}

	private File checkFolder(final IPath folderAbosultePath, final String errorMessage) {
		final File folder = new File(folderAbosultePath.toOSString());
		if (!folder.isDirectory()) {
			String message = MessageFormat.format("{0}: {1}", errorMessage, folderAbosultePath); //$NON-NLS-1$
			raiseRuntimeException(message, null);
		}
		return folder;
	}

	private List<File> getRecursiveFileList(final File file) {
		return getRecursiveFileList(file, new ArrayList<File>());
	}

	private List<File> getRecursiveFileList(final File file, List<File> list) {

		// Loop over directory
		if (file.isDirectory()) {
			for (final File subfile : file.listFiles()) {
				getRecursiveFileList(subfile, list);
			}
			return list;
		}

		// Regular file
		list.add(file);
		return list;
	}

	/**
	 * @return Input file root folder
	 */
	protected String getInputFolderPath() {
		return "input"; //$NON-NLS-1$
	}

	/**
	 * @return References file root folder
	 */
	protected String getReferenceFolderPath() {
		return "reference"; //$NON-NLS-1$
	}

	protected final void raiseRuntimeException(final String message, final Throwable t) {
		throw new RuntimeException(message, t);
	}

	protected abstract TestCase createTestCase(final String testName, final File inputFile, final File referenceFile);
}
