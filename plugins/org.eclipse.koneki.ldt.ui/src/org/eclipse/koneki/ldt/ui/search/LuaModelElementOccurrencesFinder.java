/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 ******************************************************************************/
package org.eclipse.koneki.ldt.ui.search;

import java.util.ArrayList;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.search.ModelElementOccurrencesFinder;
import org.eclipse.koneki.ldt.internal.parser.IOccurrenceHolder;
import org.eclipse.koneki.ldt.parser.LuaSelectionEngine;
import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;

public class LuaModelElementOccurrencesFinder extends ModelElementOccurrencesFinder {

	private Declaration declaration;

	/**
	 * Browses {@link IModuleDeclaration} in order to find a {@link Declaration} or an {@link Identifier} able to provides references to a
	 * {@link Declaration}.
	 * 
	 * @return null If {@link Declaration} or references are found. A {@link String} else way to express problem that occurred .
	 * @see org.eclipse.dltk.ui.search.ModelElementOccurrencesFinder#initialize(org.eclipse.dltk.core.ISourceModule,
	 *      org.eclipse.dltk.ast.parser.IModuleDeclaration, int, int)
	 */
	@Override
	public String initialize(final ISourceModule module, final IModuleDeclaration root, final int offset, final int length) {
		declaration = null;
		// Unable to find right positions when file contains errors
		if (root instanceof LuaSourceRoot && ((LuaSourceRoot) root).hasError()) {
			return null;
		}
		ASTNode node = LuaSelectionEngine.findNodeAt((ModuleDeclaration) root, offset, offset + length - 1);
		if (node instanceof Identifier) {
			Identifier id = (Identifier) node;
			if (id.hasDeclaration()) {
				declaration = id.getDeclaration();
				return null;
			}
		} else if (node instanceof Declaration) {
			declaration = (Declaration) node;
			return null;
		}
		return null;
	}

	@Override
	public OccurrenceLocation[] getOccurrences() {
		// Clean file from occurrences if not relevant
		if (declaration == null || !(declaration instanceof IOccurrenceHolder)) {
			return new OccurrenceLocation[0];
		}
		// Highlight declaration itself
		ArrayList<OccurrenceLocation> list = new ArrayList<OccurrenceLocation>();
		list.add(new OccurrenceLocation(declaration.getNameStart(), declaration.getNameEnd() - declaration.getNameStart(), declaration.getName()));

		// Highlight occurrences
		IOccurrenceHolder holder = (IOccurrenceHolder) declaration;
		for (ASTNode node : holder.getOccurrences()) {
			list.add(new OccurrenceLocation(node.sourceStart(), node.matchLength(), node.toString()));
		}
		return list.toArray(new OccurrenceLocation[list.size()]);
	}
}
