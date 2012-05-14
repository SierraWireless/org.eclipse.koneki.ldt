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
 * Reference a type which is defined in another module
 */
public class ExternalTypeRef extends TypeRef {
	private final String moduleName;
	private final String typeName;

	public ExternalTypeRef(final String module, final String type) {
		moduleName = module;
		typeName = type;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getTypeName() {
		return typeName;
	}

	/**
	 * @see org.eclipse.koneki.ldt.parser.api.external.TypeRef#toReadableString()
	 */
	@Override
	public String toReadableString() {
		return moduleName + "#" + typeName; //$NON-NLS-1$
	}
}
