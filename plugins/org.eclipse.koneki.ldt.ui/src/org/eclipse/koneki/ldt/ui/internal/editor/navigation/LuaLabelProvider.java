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
package org.eclipse.koneki.ldt.ui.internal.editor.navigation;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.koneki.ldt.core.internal.ast.models.LuaDLTKModelUtils;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.ImageConstants;
import org.eclipse.swt.graphics.Image;

public class LuaLabelProvider extends LabelProvider {

	@Override
	public String getText(final Object element) {
		return null;
	}

	@Override
	public Image getImage(final Object element) {
		final IMember member = element instanceof IMember ? (IMember) element : null;
		if (member == null)
			return null;
		try {
			// Special icon for private type
			if (member.exists()) {
				if (member instanceof IType) {
					if (LuaDLTKModelUtils.isModule(member)) {
						return Activator.getDefault().getImageRegistry().get(ImageConstants.MODULE_OBJ16);
					} else if (LuaDLTKModelUtils.isGlobalTable(member)) {
						return Activator.getDefault().getImageRegistry().get(ImageConstants.GLOBAL_TABLE_OBJ16);
					} else if (LuaDLTKModelUtils.isLocalTable(member)) {
						return Activator.getDefault().getImageRegistry().get(ImageConstants.LOCAL_TABLE_OBJ16);
					}
				} else if (LuaDLTKModelUtils.isModuleFunction(member)) {
					return Activator.getDefault().getImageRegistry().get(ImageConstants.MODULE_FUNCTION_OBJ16);
				}
			}
		} catch (ModelException e) {
			Activator.logError(Messages.LuaCompletionProvidersFlags, e);
		}
		// DLTK default behavior
		return null;
	}
}
