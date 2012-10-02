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
package org.eclipse.koneki.ldt.remote.core.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public final class NetworkUtil {

	private NetworkUtil() {
	}

	public static String findBindedAddress(String hostName, IProgressMonitor monitor) {
		try {
			SubMonitor subMonitor = SubMonitor.convert(monitor, 3);
			// get the inet address for this hostname
			InetAddress inetaddr = InetAddress.getByName(hostName);
			subMonitor.worked(2);

			// find in a network interface which will allow to access to this address.
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface netInterface = interfaces.nextElement();
				// search only on running network
				if (netInterface.isUp()) {
					// test if the address is accessible for this network interface
					if (inetaddr.isReachable(netInterface, 0, 2000)) {
						// we found the interface, now seach the IP address
						Enumeration<InetAddress> inetAddresses = netInterface.getInetAddresses();
						while (inetAddresses.hasMoreElements()) {
							InetAddress nextElement = inetAddresses.nextElement();
							if (nextElement.isSiteLocalAddress())
								return nextElement.getHostAddress();
						}
					}
				}
			}
			subMonitor.worked(1);
		} catch (UnknownHostException ex) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return null;
	}
}
