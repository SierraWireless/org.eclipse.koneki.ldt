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
package org.eclipse.koneki.ldt.parser.ast.visitor;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;

/**
 * Visits AST in order to find minimal node under a source code position.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public final class ScopeVisitor extends ASTVisitor {
	private final int offset;
	private ASTNode scope;

	/**
	 * Provides desired scope location
	 * 
	 * @param position
	 *            Position in source code of node defining pertinent scope
	 */
	public ScopeVisitor(final int position) {
		this.offset = position;
	}

	@Override
	public boolean visitGeneral(final ASTNode node) {
		// Do not process after given position
		if (node.sourceStart() > offset) {
			return false;
		} else if (node.sourceStart() <= offset) {
			// Node is interesting
			if (scope == null) {
				// Keep it
				scope = node;
			} else {
				// Compare to current one
				final int challenger = node.sourceEnd() - node.sourceStart();
				final int current = scope.sourceEnd() - scope.sourceStart();
				final boolean closerToOffset = Math.abs(offset - node.sourceEnd()) < Math.abs(offset - scope.sourceEnd());
				// Keep the most accurate, which has shortest length
				if (challenger < current && closerToOffset) {
					scope = node;
				}
			}

		}
		return true;
	}

	/**
	 * Node at given position
	 * 
	 * @return node near to given position, with shortest length
	 */
	public ASTNode getScope() {
		return scope;
	}
}