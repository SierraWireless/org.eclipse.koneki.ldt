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

import java.util.ArrayList;
import java.util.List;

/**
 * A ReturnValues mean a list of value return by a function
 */
public class Return {
	private ArrayList<TypeRef> types = new ArrayList<TypeRef>();

	public List<TypeRef> getTypes() {
		return types;
	}

	public void addType(final TypeRef type) {
		types.add(type);
	}
}
