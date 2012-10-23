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
package org.eclipse.koneki.ldt.lua.tests.internal.formatter;

import org.eclipse.core.runtime.Path;
import org.eclipse.koneki.ldt.lua.tests.internal.utils.AbstractLuaTestSuite;

public class MixedFormatterTestSuite extends AbstractLuaTestSuite {

	public MixedFormatterTestSuite(boolean ignore) {
		super("Formatter mixed", "tests/formatter", "lua", ignore); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @see org.eclipse.koneki.ldt.parser.lua.tests.LDTLuaAbstractTestSuite#getReferenceFolderPath()
	 */
	@Override
	protected String getReferenceFolderPath() {
		return new Path("reference").append("mixed").toString(); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @see org.eclipse.koneki.ldt.parser.lua.tests.LDTLuaAbstractTestSuite#getTestModuleName()
	 */
	@Override
	protected String getTestModuleName() {
		return "test_mixed"; //$NON-NLS-1$
	}

}
