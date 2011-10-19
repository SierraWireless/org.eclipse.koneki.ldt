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

import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.core.Flags;

/**
 * Represent a Lua Module Declaration
 */
public class LuaModuleDeclaration extends TypeDeclaration {

	public LuaModuleDeclaration(int nameStart, int nameEnd, int start, int end) {
		super("module", nameStart, nameEnd, start, end);
		setModifier(Flags.AccModule);
	}
}
