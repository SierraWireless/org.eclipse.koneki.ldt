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
package org.eclipse.koneki.ldt.ui.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.internal.ui.scriptview.BuildPathContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.koneki.ldt.core.buildpath.LuaExecutionEnvironment;
import org.eclipse.koneki.ldt.core.buildpath.LuaExecutionEnvironmentBuildpathUtil;
import org.eclipse.koneki.ldt.core.buildpath.LuaExecutionEnvironmentConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

@SuppressWarnings("restriction")
public class LuaExecutionEnvironmentBuildpathContainerDocumentationCommandHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// Compute path to Execution Environment
		final IPath path = getExecutionEnvironmentContainer(event);
		if (path == null)
			return null;
		try {
			final LuaExecutionEnvironment ee = LuaExecutionEnvironmentBuildpathUtil.getExecutionEnvironment(path);
			// Deduce documentation path from Execution Environment
			if (ee != null && ee.getDocumentationPath().length > 0) {
				final IPath docPath = ee.getDocumentationPath()[0].append(LuaExecutionEnvironmentConstants.EE_FILE_DOCS_INDEX);
				final URL firstUrl = docPath.toFile().toURI().toURL();
				PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(firstUrl);
				return null;
			}
		} catch (final MalformedURLException e) {
			throw new ExecutionException(Messages.LuaExecutionEnvironmentBuildpathContainerDocumentationCommandHandlerUnableToDisplay, e);
		} catch (final PartInitException e) {
			throw new ExecutionException(Messages.LuaExecutionEnvironmentBuildpathContainerDocumentationCommandHandlerUnableToDisplay, e);
		} catch (final CoreException e) {
			throw new ExecutionException(Messages.LuaExecutionEnvironmentBuildpathContainerDocumentationCommandHandlerUnableToDisplay, e);
		}
		throw new ExecutionException(Messages.LuaExecutionEnvironmentBuildpathContainerDocumentationCommandHandlerUnableToFindExecutionEnvironment);
	}

	/**
	 * Extracts Execution Environment path from selection
	 */
	private IPath getExecutionEnvironmentContainer(final ExecutionEvent event) {
		final ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		if (currentSelection instanceof IStructuredSelection) {
			final Object firstElement = ((IStructuredSelection) currentSelection).getFirstElement();
			if (firstElement instanceof BuildPathContainer) {
				final BuildPathContainer bpc = ((BuildPathContainer) firstElement);
				final IBuildpathEntry entry = bpc.getBuildpathEntry();
				return entry.getPath();
			}
		}
		return null;
	}
}
