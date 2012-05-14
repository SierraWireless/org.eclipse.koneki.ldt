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

import org.eclipse.dltk.ast.ASTNode;

/**
 * root class of all node for LDT
 */
// CHECKSTYLE:OFF
public abstract class LuaASTNode extends ASTNode {
	// CHECKSTYLE:ON

	@Override
	public int hashCode() {
		// we do this only to avoid findbug errors.
		// findbugs detects that ASTNode override equals but not hashcode
		// but equals is override by super.equals ...
		return super.hashCode();
	}
}
