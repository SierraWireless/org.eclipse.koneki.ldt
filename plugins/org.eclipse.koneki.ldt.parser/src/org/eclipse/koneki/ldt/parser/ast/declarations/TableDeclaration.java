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

import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;

/**
 * Declaration of a table detected by outline and code assistance
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class TableDeclaration extends TypeDeclaration {

	/**
	 * Initialize a table declaration node
	 * 
	 * @param name
	 *            name of the expression which this table is assigned to
	 * @param nameStart
	 *            offset of start of table's start expression
	 * @param nameEnd
	 *            offset of end of table's start expression
	 * @param start
	 *            start offset of table body
	 * @param end
	 *            end offset of table body
	 */
	public TableDeclaration(String name, int nameStart, int nameEnd, int start,
			int end) {
		super(name, nameStart, nameEnd, start, end);
	}

	/**
	 * Initialize a table declaration node
	 * 
	 * @param name
	 *            reference to the expression which this table is assigned to
	 * @param start
	 *            start offset of table body
	 * @param end
	 *            end offset of table body
	 */
	public TableDeclaration(SimpleReference name, int start, int end) {
		this(name.getName(), name.sourceStart(), name.sourceEnd(), start, end);
	}
}
