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
package org.eclipse.koneki.ldt.lua.tests.internal.template.engine;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.koneki.ldt.lua.tests.internal.utils.AbstractLuaTestSuite;
import org.eclipse.koneki.ldt.lua.tests.internal.utils.LuaTestCase;
import org.eclipse.koneki.ldt.lua.tests.internal.utils.LuaTestModuleRunner;

import com.cforcoding.jmd.MarkDown;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

/**
 * JDM Template Engine tests. The template engine is responsible to generate HTML. This test start from lua files, generate associed HTML
 * documentation witch is compared to an HTML reference using the JMD library instead of markdown.lua file.
 */
public class JDMTemplateEngineTestSuite extends AbstractLuaTestSuite {

	public JDMTemplateEngineTestSuite(boolean ignore) {
		super("Template Engine with JDM", "tests/templateengine/", "html", ignore); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @see org.eclipse.koneki.ldt.lua.tests.internal.utils.AbstractLuaTestSuite#getInputFolderPath()
	 */
	@Override
	protected String getInputFolderPath() {
		return new Path("input").append("markdown").toString(); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @see org.eclipse.koneki.ldt.parser.lua.tests.LDTLuaAbstractTestSuite#getReferenceFolderPath()
	 */
	@Override
	protected String getReferenceFolderPath() {
		return new Path("reference").append("markdown").toString(); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @see org.eclipse.koneki.ldt.parser.lua.tests.LDTLuaAbstractTestSuite#getTestModuleName()
	 */
	@Override
	protected String getTestModuleName() {
		return "jmdtest"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.koneki.ldt.lua.tests.internal.utils.AbstractLuaTestSuite#createTestCase(java.lang.String, java.lang.String,
	 *      org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath, java.util.List)
	 */
	@Override
	protected TestCase createTestCase(String testName, String testModuleName, IPath inputFilePath, IPath referenceFilePath,
			List<String> directoryListForLuaPath) {
		return new LuaTestCase(testName, testModuleName, inputFilePath, referenceFilePath, directoryListForLuaPath) {
			/**
			 * @see org.eclipse.koneki.ldt.lua.tests.internal.utils.LuaTestCase#createLuaRunner(java.lang.String, java.lang.String, java.lang.String,
			 *      java.util.List, java.util.List)
			 */
			@Override
			protected LuaTestModuleRunner createLuaRunner(String module, String absoluteSourcePath, String asbsoluteReferencePath,
					List<String> luapath, List<String> files) {
				return new LuaTestModuleRunner(module, absoluteSourcePath, asbsoluteReferencePath, luapath, files) {
					/**
					 * @see org.eclipse.koneki.ldt.metalua.AbstractMetaLuaModule#createLuaState()
					 */
					@Override
					protected LuaState createLuaState() {
						LuaState l = super.createLuaState();
						l.register(new NamedJavaFunction() {
							private MarkDown markdown = new MarkDown();

							@Override
							public int invoke(LuaState l) {
								String input = l.checkString(1);

								String result = markdown.transform(input);
								l.pushString(result);

								return 1;
							}

							@Override
							public String getName() {
								return "jmdmarkdown"; //$NON-NLS-1$
							}
						});
						return l;
					}
				};
			}
		};
	}

	@Override
	protected List<String> createTestBlacklist() {
		ArrayList<String> blacklist = new ArrayList<String>();

		// Bug 389887
		blacklist.add("paragraph.lua"); //$NON-NLS-1$

		// Bug 389828
		blacklist.add("code2.lua"); //$NON-NLS-1$

		// Three following are due to bug 388163
		blacklist.add("list.lua"); //$NON-NLS-1$
		blacklist.add("list3.lua"); //$NON-NLS-1$
		blacklist.add("list4.lua"); //$NON-NLS-1$

		// Bug 389991
		blacklist.add("title2.lua"); //$NON-NLS-1$

		return blacklist;
	}
}
