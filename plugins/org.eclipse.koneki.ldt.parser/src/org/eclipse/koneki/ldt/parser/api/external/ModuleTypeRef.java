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

public class ModuleTypeRef extends LazyTypeRef {
	private String moduleName;

	public ModuleTypeRef(String moduleName, int returnPosition) {
		super();
		this.moduleName = moduleName;
		this.returnPosition = returnPosition;
	}

	private int returnPosition;

	public String getModuleName() {
		return moduleName;
	}

	public int getReturnPosition() {
		return returnPosition;
	}
}