/*******************************************************************************
 * Copyright (c) 2013 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.support.lua51.internal.interpreter;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua.JNLuaDebugginEngineCommandLineRenderer;
import org.eclipse.koneki.ldt.support.lua51.internal.Activator;
import org.osgi.framework.Bundle;

public class JNLua51DebugginEngineCommandLineRenderer extends JNLuaDebugginEngineCommandLineRenderer {

	private static final String COMMONS_CODEC_BUNDLE_VERSION = "1.3.0"; //$NON-NLS-1$
	private static final String COMMONS_CODEC_BUNDLE_ID = "org.apache.commons.codec"; //$NON-NLS-1$

	@Override
	protected List<String> getClassPath() {
		List<String> classPath = super.getClassPath();
		// we need to add apache commons codecs to the classpath
		// because it is used by the JavaTransportLayer of our debugger.
		classPath.add(getAppacheCommonsCodecClassPath());
		return classPath;
	}

	private String getAppacheCommonsCodecClassPath() {
		// get class of jnlua bundle in 0.9.1 version
		Bundle[] bundles = Platform.getBundles(COMMONS_CODEC_BUNDLE_ID, COMMONS_CODEC_BUNDLE_VERSION);

		// bundle must be present as we have a strong dependencies on it.
		if (bundles == null || bundles.length == 0)
			throw new RuntimeException(MessageFormat.format("Unable to resolve {0} bundle in version {1}.", COMMONS_CODEC_BUNDLE_ID, //$NON-NLS-1$
					COMMONS_CODEC_BUNDLE_VERSION));

		URL entry = bundles[0].getResource("/"); //$NON-NLS-1$

		URL resolvedEntry;
		try {
			resolvedEntry = FileLocator.toFileURL(entry);
		} catch (IOException e) {
			throw new RuntimeException(MessageFormat.format("Unable to resolve class path for {0} bundle.", COMMONS_CODEC_BUNDLE_ID), e); //$NON-NLS-1$
		}

		return resolvedEntry.getFile();
	}

	/**
	 * @see org.eclipse.koneki.ldt.support.lua51.internal.interpreter.JNLua51InterpreterCommandLineRenderer#getClassToRun()
	 */
	@Override
	protected String getClassToRun() {
		return JNLua51DebugLauncher.class.getCanonicalName();
	}

	/**
	 * @see org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua.JNLuaInterpreterCommandLineRenderer#getJNLuaBundleVersion()
	 */
	@Override
	protected String getJNLuaBundleVersion() {
		return JNLua51InterpreterCommandLineRenderer.JNLUA_BUNDLE_VERSION;
	}

	/**
	 * @see org.eclipse.koneki.ldt.debug.core.internal.interpreter.jnlua.JNLuaInterpreterCommandLineRenderer#getLauncherClassBundle()
	 */
	@Override
	protected Bundle getLauncherClassBundle() {
		return Activator.getDefault().getBundle();
	}
}
