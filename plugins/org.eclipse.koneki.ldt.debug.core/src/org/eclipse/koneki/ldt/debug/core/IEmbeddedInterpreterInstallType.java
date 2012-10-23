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
package org.eclipse.koneki.ldt.debug.core;

import org.eclipse.dltk.launching.IInterpreterInstallType;

/**
 * Define that the InterpreterInstall of this type doesn't need a external location as the executable is bundled in a or several eclipse plug-in
 */
public interface IEmbeddedInterpreterInstallType extends IInterpreterInstallType {

}
