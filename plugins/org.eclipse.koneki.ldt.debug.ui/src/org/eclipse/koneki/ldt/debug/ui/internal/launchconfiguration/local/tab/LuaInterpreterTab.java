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
package org.eclipse.koneki.ldt.debug.ui.internal.launchconfiguration.local.tab;

import java.text.MessageFormat;

import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.debug.ui.launchConfigurations.IMainLaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.launchConfigurations.InterpreterTab;
import org.eclipse.dltk.internal.debug.ui.interpreters.IInterpreterComboBlockContext;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpreterDescriptor;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.launching.ScriptRuntime.DefaultInterpreterEntry;

public class LuaInterpreterTab extends InterpreterTab {

	public LuaInterpreterTab(final IMainLaunchConfigurationTab main) {
		super(main);
	}

	@Override
	protected LuaInterpreterTabComboBlock createInterpreterBlock(final IInterpreterComboBlockContext context) {
		return new LuaInterpreterTabComboBlock(context);
	}

	@Override
	protected InterpreterDescriptor getDefaultInterpreterDescriptor() {
		return new InterpreterDescriptor() {

			@Override
			public String getDescription() {
				String interpreterName = getInterpreter().getName();
				if (interpreterName == null) {
					interpreterName = Messages.LuaInterpreterTabUndefinedInterpreterName;
				}
				return MessageFormat.format(Messages.LuaInterpreterTabInterpreterName, interpreterName);
			}

			@Override
			public IInterpreterInstall getInterpreter() {
				final IEnvironment environment = EnvironmentManager.getEnvironment(getScriptProject());
				final String id = environment != null ? environment.getId() : EnvironmentManager.getLocalEnvironment().getId();
				return ScriptRuntime.getDefaultInterpreterInstall(new DefaultInterpreterEntry(getNature(), id));
			}
		};
	}
}
