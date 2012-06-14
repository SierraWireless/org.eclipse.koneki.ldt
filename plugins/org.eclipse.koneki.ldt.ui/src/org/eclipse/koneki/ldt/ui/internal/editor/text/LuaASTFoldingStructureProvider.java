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
package org.eclipse.koneki.ldt.ui.internal.editor.text;

import org.eclipse.core.runtime.ILog;
import org.eclipse.dltk.ui.text.folding.AbstractASTFoldingStructureProvider;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.ui.internal.Activator;

public class LuaASTFoldingStructureProvider extends AbstractASTFoldingStructureProvider {

	@Override
	protected String getCommentPartition() {
		return ILuaPartitions.LUA_COMMENT;
	}

	@Override
	protected ILog getLog() {
		return Activator.getDefault().getLog();
	}

	@Override
	protected String getNatureId() {
		return LuaNature.ID;
	}

	@Override
	protected String getPartition() {
		return ILuaPartitions.LUA_PARTITIONING;
	}

	@Override
	protected IPartitionTokenScanner getPartitionScanner() {
		return Activator.getDefault().getTextTools().createPartitionScanner();
	}

	@Override
	protected String[] getPartitionTypes() {
		return ILuaPartitions.LUA_PARTITION_TYPES;
	}

}
