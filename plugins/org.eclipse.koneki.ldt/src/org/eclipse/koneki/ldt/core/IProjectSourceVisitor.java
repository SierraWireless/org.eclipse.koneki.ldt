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
package org.eclipse.koneki.ldt.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IProjectSourceVisitor {
	public abstract void processFile(final IPath absolutePath, final IPath relativePath, final String charset, final IProgressMonitor monitor)
			throws CoreException;

	public abstract void processDirectory(final IPath absolutePath, final IPath relativePath, final IProgressMonitor monitor) throws CoreException;

}
