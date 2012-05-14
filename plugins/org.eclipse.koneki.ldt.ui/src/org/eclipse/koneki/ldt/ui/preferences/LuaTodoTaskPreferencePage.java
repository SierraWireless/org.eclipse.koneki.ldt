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

package org.eclipse.koneki.ldt.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPropertyAndPreferencePage;
import org.eclipse.dltk.ui.preferences.AbstractOptionsBlock;
import org.eclipse.dltk.ui.preferences.TodoTaskOptionsBlock;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.koneki.ldt.Activator;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class LuaTodoTaskPreferencePage extends AbstractConfigurationBlockPropertyAndPreferencePage {

	protected String getHelpId() {
		return null;
	}

	protected void setDescription() {
		setDescription(Messages.LuaTodoTaskPreferencePage_description);
	}

	protected AbstractOptionsBlock createOptionsBlock(IStatusChangeListener newStatusChangedListener, IProject project,
			IWorkbenchPreferenceContainer container) {
		return new TodoTaskOptionsBlock(newStatusChangedListener, project, container, Activator.PLUGIN_ID);
	}

	protected String getNatureId() {
		return LuaNature.ID;
	}

	protected String getProjectHelpId() {
		return null;
	}

	protected void setPreferenceStore() {
		setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(), Activator.PLUGIN_ID));
	}

	protected String getPreferencePageId() {
		return "org.eclipse.koneki.ldt.ui.todo"; //$NON-NLS-1$
	}

	protected String getPropertyPageId() {
		return "org.eclipse.koneki.ldt.ui.todo.propertyPage"; //$NON-NLS-1$
	}

}
