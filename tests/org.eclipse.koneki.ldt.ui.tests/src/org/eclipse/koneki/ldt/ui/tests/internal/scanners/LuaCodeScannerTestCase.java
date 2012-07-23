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

import org.eclipse.dltk.internal.ui.text.DLTKColorManager;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaCodeScanner;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

@SuppressWarnings("restriction")
public class LuaCodeScannerTestCase extends AbstractScannerTestCase {

	public LuaCodeScannerTestCase(String testName, File inputFile, File referenceFile) {
		super(testName, inputFile, referenceFile);
	}

	@Override
	protected ITokenScanner createScanner() {
		return new LuaCodeScanner(new DLTKColorManager(true), EditorsPlugin.getDefault().getPreferenceStore()) {
			@Override
			public Token getToken(String key) {
				Token token = super.getToken(key);
				token.setData(key);
				return token;
			}
		};
	}

	@Override
	protected boolean isIgnoredToken(IToken token) {
		return super.isIgnoredToken(token) || token.getData().equals("DLTK_default"); //$NON-NLS-1$
	}
}
