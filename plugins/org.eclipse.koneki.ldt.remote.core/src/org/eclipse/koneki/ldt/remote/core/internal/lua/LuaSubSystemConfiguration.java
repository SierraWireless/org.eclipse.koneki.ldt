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
package org.eclipse.koneki.ldt.remote.core.internal.lua;

import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.ISubSystemConfiguration;
import org.eclipse.rse.core.subsystems.SubSystemConfiguration;

public class LuaSubSystemConfiguration extends SubSystemConfiguration implements ISubSystemConfiguration {

	@Override
	public ISubSystem createSubSystemInternal(IHost host) {
		return new LuaSubSystem(host, getConnectorService(host));
	}

	/**
	 * @see org.eclipse.rse.core.subsystems.SubSystemConfiguration#getConnectorService(org.eclipse.rse.core.model.IHost)
	 */
	@Override
	public IConnectorService getConnectorService(IHost host) {
		IConnectorService[] connectorServices = host.getConnectorServices();
		if (connectorServices.length > 0) {
			return connectorServices[0];
		}
		return null;
	}

	/**
	 * @see org.eclipse.rse.core.subsystems.SubSystemConfiguration#supportsProperties()
	 */
	@Override
	public boolean supportsProperties() {
		return true;
	}

	/**
	 * @see org.eclipse.rse.core.subsystems.SubSystemConfiguration#supportsFilters()
	 */
	@Override
	public boolean supportsFilters() {
		return false;
	}
}