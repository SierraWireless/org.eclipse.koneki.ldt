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

public interface LuaExecutionEnvironmentConstants {
	public final String PREF_EXECUTION_ENVIRONMENTS_LIST = "executionEnvironments"; //$NON-NLS-1$
	public final String EXECUTION_ENVIRONMENTS_LIST_SEPARATOR = ";"; //$NON-NLS-1$
	public final String PREFERENCE_PAGE_ID = "org.eclipse.koneki.ldt.ui.executionenvironmentpreferencepage"; //$NON-NLS-1$
	public final String FILE_EXTENSION = "*.zip"; //$NON-NLS-1$
	public final String MANIFEST_EXTENSION = ".rockspec"; //$NON-NLS-1$
	public final String CONTAINER_PATH_START = "org.eclipse.koneki.ldt.ExecutionEnvironmentContainer"; //$NON-NLS-1$
	public final String EE_FILE_API_ARCHIVE = "api.zip"; //$NON-NLS-1$
	public final String EE_FILE_DOCS_FOLDER = "docs/"; //$NON-NLS-1$
	public final String EE_FILE_DOCS_INDEX = "index.html"; //$NON-NLS-1$
}
