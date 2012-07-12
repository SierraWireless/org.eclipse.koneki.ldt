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

import org.eclipse.dltk.ui.text.folding.IFoldingContent;
import org.eclipse.dltk.ui.text.folding.PartitioningFoldingBlockProvider;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.editor.text.ILuaPartitions;

public class LuaCommentFoldingBlockProvider extends PartitioningFoldingBlockProvider {

	public LuaCommentFoldingBlockProvider() {
		super(Activator.getDefault().getTextTools());
	}

	public void computeFoldableBlocks(IFoldingContent content) {
		if (isFoldingComments()) {
			computeBlocksForPartitionType(content, ILuaPartitions.LUA_COMMENT, LuaFoldingBlockKind.COMMENT, isCollapseComments());
			computeBlocksForPartitionType(content, ILuaPartitions.LUA_MULTI_LINE_COMMENT, LuaFoldingBlockKind.COMMENT, isCollapseComments());
		}
	}
}
