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

package org.eclipse.koneki.ldt.branding;

import java.util.Iterator;

import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.koneki.ldt.branding"; //$NON-NLS-1$

	public static final String PRODUCT_VERSION_PROPERTY = "product.version"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// get version of Product installable unit !
		String version = "NA"; //$NON-NLS-1$
		System.setProperty(PRODUCT_VERSION_PROPERTY, version);

		ProvisioningUI provisioningUI = ProvisioningUI.getDefaultUI();
		if (null == provisioningUI) {
			return;
		}

		ProvisioningSession provisioningSession = provisioningUI.getSession();
		if (null == provisioningSession) {
			return;
		}

		String profileId = provisioningUI.getProfileId();
		IQueryable<IInstallableUnit> queryable = ((IProfileRegistry) provisioningSession.getProvisioningAgent().getService(
				IProfileRegistry.SERVICE_NAME)).getProfile(profileId);
		if (null == queryable) {
			return;
		}

		String pId = "org.eclipse.koneki.ldt.product-product"; //$NON-NLS-1$

		if (null != queryable) {
			IQueryResult<IInstallableUnit> iqr = queryable.query(QueryUtil.createIUQuery(pId), null);
			if (null != iqr) {
				Iterator<IInstallableUnit> ius = iqr.iterator();
				if (ius.hasNext()) {
					IInstallableUnit iu = ius.next();
					Version v = iu.getVersion();

					if (null != v) {
						version = v.toString();
					}
				}
			}
		}

		// set the version as system properties
		System.setProperty(PRODUCT_VERSION_PROPERTY, version);

		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
