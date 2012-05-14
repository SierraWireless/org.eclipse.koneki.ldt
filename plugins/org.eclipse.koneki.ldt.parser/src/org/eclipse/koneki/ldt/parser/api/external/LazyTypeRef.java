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
package org.eclipse.koneki.ldt.parser.api.external;

/**
 * TODO Comment this class
 */
public class LazyTypeRef extends TypeRef {

	/**
	 * @see org.eclipse.koneki.ldt.parser.api.external.TypeRef#toReadableString()
	 */
	@Override
	public String toReadableString() {
		return "unresolved"; //$NON-NLS-1$
	}

}
