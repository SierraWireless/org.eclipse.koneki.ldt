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
import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.koneki.ldt.internal.parser.INavigableNode;
import org.eclipse.koneki.ldt.internal.parser.IOccurrenceHolder;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;
import org.eclipse.koneki.ldt.parser.ast.statements.Chunk;

/**
 * Declaration of a function detected by outline and code assistance
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class FunctionDeclaration extends MethodDeclaration implements IOccurrenceHolder, INavigableNode {
	private ArrayList<ASTNode> occurrences;
	private ASTNode parentNode;

	/**
	 * Initialize a function declaration node
	 * 
	 * @param name
	 *            name of the expression which this function is assigned to
	 * @param nameStart
	 *            offset of start of table's start expression
	 * @param nameEnd
	 *            offset of end of table's start expression
	 * @param start
	 *            start offset of function body
	 * @param end
	 *            end offset of function body
	 */
	public FunctionDeclaration(String name, int nameStart, int nameEnd, int start, int end) {
		super(name, nameStart, nameEnd, start, end);
		occurrences = new ArrayList<ASTNode>();
	}

	/**
	 * Initialize a function declaration node
	 * 
	 * @param name
	 *            reference to the expression which this function is assigned to
	 * @param start
	 *            start offset of function body
	 * @param end
	 *            end offset of function body
	 */

	public FunctionDeclaration(SimpleReference name, int start, int end) {
		this(name.getName(), name.sourceStart(), name.sourceEnd(), start, end);
	}

	/**
	 * Defines declared function Arguments
	 * 
	 * By default, parameters of {@link MethodDeclaration} are empty. This method is for casting declared function's parameters into {@link Argument}
	 * s.
	 * 
	 * @param params
	 *            {@link Chunk} containing parameters as {@link Identifier}s. Invalid {@link ASTNode}s will be shown as <code>...</code>.
	 */
	public void acceptArguments(Chunk params) {
		for (Object o : params.getChilds()) {
			Argument arg;
			if (o instanceof Identifier) {
				Identifier id = (Identifier) o;
				arg = new Argument(id, id.sourceStart(), id.sourceEnd(), null, this.getModifiers());
			} else {
				Statement statement = (Statement) o;
				SimpleReference dots = new SimpleReference(statement.sourceStart(), statement.sourceEnd(), "..."); //$NON-NLS-1$
				arg = new Argument(dots, dots.sourceStart(), null, this.getModifiers());
			}
			addArgument(arg);
		}
	}

	@Override
	public void addOccurrence(ASTNode node) {
		occurrences.add(node);
	}

	@Override
	public ASTNode[] getOccurrences() {
		return occurrences.toArray(new ASTNode[occurrences.size()]);
	}

	@Override
	public ASTNode getParent() {
		return parentNode;
	}

	@Override
	public void setParent(ASTNode parent) {
		parentNode = parent;
	}

	public boolean isGlobal() {
		return isPublic();
	}

	public boolean isLocal() {
		return isPrivate();
	}
}
