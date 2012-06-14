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
package org.eclipse.koneki.ldt.core.internal.ast.models.api;

/**
 * Reference of a type defines in this module
 */
public class PrimitiveTypeRef extends TypeRef {
	private String typeName;

	public PrimitiveTypeRef(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @see org.eclipse.koneki.ldt.core.internal.ast.models.api.TypeRef#toReadableString()
	 */
	@Override
	public String toReadableString() {
		return "#" + typeName; //$NON-NLS-1$
	}

}
