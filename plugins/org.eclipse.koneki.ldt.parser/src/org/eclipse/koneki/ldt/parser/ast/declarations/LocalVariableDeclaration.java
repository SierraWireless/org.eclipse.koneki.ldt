/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.parser.ast.declarations;

import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;

/**
 * Declaration of a local variable detected by outline and code assistance
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class LocalVariableDeclaration extends FieldDeclaration {

	/**
	 * Initialize a local variable declaration node
	 * 
	 * @param name
	 *            name of the expression this variable is named after
	 * @param nameStart
	 *            start offset of name expression
	 * @param nameEnd
	 *            end offset of name expression
	 * @param start
	 *            start offset of variable body
	 * @param end
	 *            end offset of variable body
	 */
	public LocalVariableDeclaration(String name, int nameStart, int nameEnd,
			int declStart, int declEnd) {
		super(name, nameStart, nameEnd, declStart, declEnd);
	}

	/**
	 * Initialize a local variable declaration node
	 * 
	 * @param name
	 *            reference to the expression which is the name of the variable
	 * @param start
	 *            start offset of variable body
	 * @param end
	 *            end offset of variable body
	 */
	public LocalVariableDeclaration(SimpleReference name, int declStart,
			int declEnd) {
		this(name.getName(), name.sourceStart(), name.sourceEnd(), declStart,
				declEnd);
	}

	public int getKind() {
		return Declaration.D_DECLARATOR;
	}
}
