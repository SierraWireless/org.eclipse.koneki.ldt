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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.koneki.ldt.ui.internal.editor.templates.SimpleLuaSourceViewerConfiguration;
import org.eclipse.koneki.ldt.ui.internal.editor.text.ILuaPartitions;
import org.eclipse.koneki.ldt.ui.internal.editor.text.LuaDocumentorScanner;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

@SuppressWarnings("restriction")
public class LuaDocumentorScannerTestCase extends AbstractScannerTestCase {

	public LuaDocumentorScannerTestCase(String testName, File inputFile, File referenceFile) {
		super(testName, inputFile, referenceFile);
	}

	@Override
	protected ITokenScanner createScanner() {
		IPreferenceStore preferenceStore = EditorsPlugin.getDefault().getPreferenceStore();
		SimpleLuaSourceViewerConfiguration configuration = new SimpleLuaSourceViewerConfiguration(new DLTKColorManager(true), preferenceStore, null,
				ILuaPartitions.LUA_PARTITIONING, false);
		return new LuaDocumentorScanner(configuration) {
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
		return super.isIgnoredToken(token) || token.getData().toString().equals("DLTK_doc"); //$NON-NLS-1$
	}

}
