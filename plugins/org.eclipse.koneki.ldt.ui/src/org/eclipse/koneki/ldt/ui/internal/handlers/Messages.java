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
package org.eclipse.koneki.ldt.ui.internal.handlers;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {
	// CHECKSTYLE NLS: OFF
	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.ui.internal.handlers.messages"; //$NON-NLS-1$
	public static String LuaExecutionEnvironmentBuildpathContainerDocumentationCommandHandlerUnableToDisplay;
	public static String LuaExecutionEnvironmentBuildpathContainerDocumentationCommandHandlerUnableToFindExecutionEnvironment;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	// CHECKSTYLE NLS: ON
	private Messages() {
	}
}
