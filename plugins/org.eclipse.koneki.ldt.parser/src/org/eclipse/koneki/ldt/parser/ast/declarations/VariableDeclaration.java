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
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;
import org.eclipse.koneki.ldt.internal.parser.IOccurrenceHolder;

/**
 * Declaration of a local variable detected by outline and code assistance
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class VariableDeclaration extends Declaration implements IOccurrenceHolder, INavigableNode {
	private ArrayList<ASTNode> occurrences;
	private ASTNode parentNode;

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
	public VariableDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd) {
		this(name, declStart, declEnd);
		setNameStart(nameStart);
		setNameEnd(nameEnd);
	}

	public VariableDeclaration(String name, int nameStart, int nameEnd) {
		super(nameStart, nameEnd);
		setName(name);
		setNameStart(nameStart);
		setNameEnd(nameEnd);
		occurrences = new ArrayList<ASTNode>();
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
	public VariableDeclaration(SimpleReference name, int declStart, int declEnd) {
		this(name.getName(), name.sourceStart(), name.sourceEnd(), declStart, declEnd);
	}

	@Override
	public int getKind() {
		return Declaration.D_VAR_DECL;
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
