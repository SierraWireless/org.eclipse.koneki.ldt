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
package org.eclipse.koneki.ldt.wizards.pages;

import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.swt.widgets.Composite;

/**
 * A restriction of {@link ProjectWizardFirstPage}, remove useless UI elements:
 * <ul>
 * <li>interpreters</li>
 * <li>hosts</li>
 * </ul>
 */
public class LuaProjectSettingsPage extends ProjectWizardFirstPage {

	public LuaProjectSettingsPage() {
		setTitle(Messages.LuaProjecSettingsPageLabel);
	}

	/**
	 * Interpreters are not supported yet. Related UI options and functionality should be disabled.
	 * 
	 * @see ProjectWizardFirstPage#supportInterpreter()
	 * @return false
	 */
	@Override
	protected boolean supportInterpreter() {
		return false;
	}

	/**
	 * Provide specific {@link LocationGroup} in order to avoid UI section dealing with Host, as no host is currently used.
	 * 
	 * @see ProjectWizardFirstPage#createLocationGroup()
	 */
	@Override
	protected LocationGroup createLocationGroup() {
		return new LuaLocationGroup();
	}

	/**
	 * Just disable <i>Host</i> section defined in {@link LuaLocationGroup#fEnvironment}
	 * 
	 * @see LuaLocationGroup#fEnvironment
	 */
	public class LuaLocationGroup extends LocationGroup {
		@Override
		protected void createEnvironmentControls(final Composite group, final int numColumns) {
		}
	}
}