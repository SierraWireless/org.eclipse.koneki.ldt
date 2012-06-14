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
package org.eclipse.koneki.ldt.ui.internal.search;

import java.util.ArrayList;

import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.search.ModelElementOccurrencesFinder;
import org.eclipse.koneki.ldt.core.internal.ast.models.LuaASTUtils;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Item;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaSourceRoot;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Identifier;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.LuaExpression;

public class LuaModelElementOccurrencesFinder extends ModelElementOccurrencesFinder {

	private Item definition;

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
		definition = null;
		if (root instanceof LuaSourceRoot && ((LuaSourceRoot) root).hasError()) {
			return null;
		}

		LuaExpression luaExpression = LuaASTUtils.getLuaExpressionAt((LuaSourceRoot) root, offset, offset + length - 1);
		if (luaExpression instanceof Identifier) {
			definition = ((Identifier) luaExpression).getDefinition();
		}
		return null;
	}

	@Override
	public OccurrenceLocation[] getOccurrences() {
		// Clean file from occurrences if not relevant
		if (definition == null) {
			return new OccurrenceLocation[0];
		}

		// Highlight occurrences
		ArrayList<OccurrenceLocation> list = new ArrayList<OccurrenceLocation>();
		for (Identifier identifier : definition.getOccurrences()) {
			list.add(new OccurrenceLocation(identifier.sourceStart(), identifier.matchLength() + 1, definition.getName()));
		}
		return list.toArray(new OccurrenceLocation[list.size()]);
	}
}
