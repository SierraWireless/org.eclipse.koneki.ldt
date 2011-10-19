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

import java.util.ArrayList;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;
import org.eclipse.koneki.ldt.internal.parser.IOccurrenceHolder;

/**
 * Declaration of a table detected by outline and code assistance
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class TableDeclaration extends TypeDeclaration implements IOccurrenceHolder, INavigableNode {

	private ArrayList<ASTNode> occurrences;
	private ASTNode parentNode;
	private boolean isModuleRepresentation;

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
	public TableDeclaration(String name, int nameStart, int nameEnd, int start, int end, boolean isModule) {
		super(name, nameStart, nameEnd, start, end);
		occurrences = new ArrayList<ASTNode>();
		isModuleRepresentation = isModule;
	}

	public boolean isModuleRepresentation() {
		return isModuleRepresentation;
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
		this(name.getName(), name.sourceStart(), name.sourceEnd(), start, end, false);
	}

	@Override
	public void addOccurrence(ASTNode node) {
		occurrences.add(node);
	}

	@Override
	public ASTNode[] getOccurrences() {
		return occurrences.toArray(new ASTNode[occurrences.size()]);
	}

	/**
	 * @see org.eclipse.koneki.ldt.internal.parser.INavigableNode#getParent()
	 */
	@Override
	public ASTNode getParent() {
		return parentNode;
	}

	/**
	 * @see org.eclipse.koneki.ldt.internal.parser.INavigableNode#setParent(org.eclipse.dltk.ast.ASTNode)
	 */
	@Override
	public void setParent(ASTNode parent) {
		parentNode = parent;
	}
}
