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
package org.eclipse.koneki.ldt.debug.ui.internal.interpreters;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterEnvironmentVariablesBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterLibraryBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.IAddInterpreterDialogRequestor;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersMessages;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.ui.dialogs.StatusInfo;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class AddLuaInterpreterDialog extends AddScriptInterpreterDialog {

	public AddLuaInterpreterDialog(IAddInterpreterDialogRequestor requestor, Shell shell, IInterpreterInstallType[] interpreterInstallTypes,
			IInterpreterInstall editedInterpreter) {
		super(requestor, shell, interpreterInstallTypes, editedInterpreter);
	}

	@Override
	protected AbstractInterpreterLibraryBlock createLibraryBlock(AddScriptInterpreterDialog dialog) {
		return new LuaInterpreterLibraryBlock(this);
	}

	@Override
	protected AbstractInterpreterEnvironmentVariablesBlock createEnvironmentVariablesBlock() {
		return new LuaInterpreterEnvironmentVariablesBlock(this);
	}

	@Override
	protected Composite createEnvironmentVariablesBlockParent(Composite parent, int numColumns) {
		return super.createEnvironmentVariablesBlockParent(parent, numColumns);
	}

	@Override
	protected IStatus validateInterpreterLocation() {
		// just validate the field is not empty
		final IStatus s;
		final Path location = new Path(getInterpreterPath());
		if (location.isEmpty()) {
			s = new StatusInfo(IStatus.INFO, InterpretersMessages.addInterpreterDialog_enterLocation);
		} else {
			s = new StatusInfo();
		}
		return s;
	}

	protected void createDialogBlocks(Composite parent, int numColumns) {
		// removing library block by stubing it
		fLibraryBlock = new LibraryBlockStub();

		// create environment variable block instead
		fEnvironmentVariablesBlock = createEnvironmentVariablesBlock();
		if (fEnvironmentVariablesBlock != null) {
			Composite envParent = createEnvironmentVariablesBlockParent(parent, numColumns);

			fEnvironmentVariablesBlock.createControlsIn(envParent);
		}

	}

	/**
	 * Stub to avoid library block
	 */
	private class LibraryBlockStub extends AbstractInterpreterLibraryBlock {

		protected LibraryBlockStub() {
			super(null);
		}

		@Override
		protected IBaseLabelProvider getLabelProvider() {
			return null;
		}

		@Override
		public void initializeFrom(IInterpreterInstall interpreter, IInterpreterInstallType type) {
		}

		@Override
		public void restoreDefaultLibraries() {
		}

		@Override
		protected boolean isDefaultLocations() {
			return true;
		}

	}

}
