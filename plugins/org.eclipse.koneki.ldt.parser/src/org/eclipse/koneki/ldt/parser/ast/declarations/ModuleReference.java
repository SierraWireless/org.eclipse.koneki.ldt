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
package org.eclipse.koneki.ldt.parser.ast.declarations;

/**
 * 
 */
public class ModuleReference extends VariableDeclaration {

	private String moduleNameReference;

	public ModuleReference(String name, int nameStart, int nameEnd, int declStart, int declEnd, String moduleName) {
		super(name, nameStart, nameEnd, declStart, declEnd);
		moduleNameReference = moduleName;
	}

	/**
	 * @return the module name reference
	 */
	public String getModuleNameReference() {
		return moduleNameReference;
	}
}
