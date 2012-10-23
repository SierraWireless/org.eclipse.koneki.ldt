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
package org.eclipse.koneki.ldt.lua.tests.internal.ast.models.api;

import org.eclipse.koneki.ldt.lua.tests.internal.utils.AbstractLuaTestSuite;

public class APIModelToHTMLTestSuite extends AbstractLuaTestSuite {

	public APIModelToHTMLTestSuite(boolean ignore) {
		super("apimodeltohtml", "tests/apimodel", "html", ignore); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected String getTestModuleName() {
		return "testhtml"; //$NON-NLS-1$
	}

	protected String getInputFolderPath() {
		return "model"; //$NON-NLS-1$
	}

	protected String getReferenceFolderPath() {
		return "html"; //$NON-NLS-1$
	}

}