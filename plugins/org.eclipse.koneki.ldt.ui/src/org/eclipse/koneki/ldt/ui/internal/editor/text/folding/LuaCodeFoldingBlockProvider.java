/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.editor.text.folding;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockProvider;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockRequestor;
import org.eclipse.dltk.ui.text.folding.IFoldingContent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaSourceRoot;
import org.eclipse.koneki.ldt.core.internal.ast.models.file.Block;

public class LuaCodeFoldingBlockProvider extends ASTVisitor implements IFoldingBlockProvider {

	private boolean fCollapseBlocks;
	private int fBlockLinesMin;
	private IFoldingBlockRequestor requestor;

	/**
	 * @see org.eclipse.dltk.ui.text.folding.IFoldingBlockProvider#initializePreferences(org.eclipse.jface.preference.IPreferenceStore)
	 */
	@Override
	public void initializePreferences(IPreferenceStore preferenceStore) {
		// we use "fold methods" preference to store whether blocks should be folded or not
		fCollapseBlocks = preferenceStore.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_METHODS);
		fBlockLinesMin = preferenceStore.getInt(PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT);
	}

	/**
	 * @see org.eclipse.dltk.ui.text.folding.IFoldingBlockProvider#setRequestor(org.eclipse.dltk.ui.text.folding.IFoldingBlockRequestor)
	 */
	@Override
	public void setRequestor(IFoldingBlockRequestor requestor) {
		this.requestor = requestor;
	}

	/**
	 * @see org.eclipse.dltk.ui.text.folding.IFoldingBlockProvider#getMinimalLineCount()
	 */
	@Override
	public int getMinimalLineCount() {
		return fBlockLinesMin;
	}

	/**
	 * @see org.eclipse.dltk.ui.text.folding.IFoldingBlockProvider#computeFoldableBlocks(org.eclipse.dltk.ui.text.folding.IFoldingContent)
	 */
	@Override
	public void computeFoldableBlocks(IFoldingContent content) {
		if (content.getModelElement() instanceof ISourceModule) {
			IModuleDeclaration declaration = SourceParserUtil.parse((ISourceModule) content.getModelElement(), null);
			for (LuaASTNode node : ((LuaSourceRoot) declaration).getInternalContent().getContent().getContent()) {
				if (node instanceof Block) {
					requestor.acceptBlock(node.sourceStart(), node.sourceEnd(), LuaFoldingBlockKind.COMMENT, null, fCollapseBlocks);
				}
			}
		}
	}
}
