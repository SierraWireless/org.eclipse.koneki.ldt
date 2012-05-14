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
package org.eclipse.koneki.ldt.editor.internal.navigation;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.koneki.ldt.parser.LuaDLTKModelUtils;
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
						return Activator.getImage("/img/module.gif"); //$NON-NLS-1$
					} else if (LuaDLTKModelUtils.isGlobalTable(member)) {
						return Activator.getImage("/img/global_table.gif"); //$NON-NLS-1$
					} else if (LuaDLTKModelUtils.isLocalTable(member)) {
						return Activator.getImage("/img/local_table.gif"); //$NON-NLS-1$
					}
				} else if (LuaDLTKModelUtils.isModuleFunction(member)) {
					return Activator.getImage("/img/module_function.gif"); //$NON-NLS-1$
				}
			}
		} catch (ModelException e) {
			Activator.logError(Messages.LuaCompletionProvidersFlags, e);
		}
		// DLTK default behavior
		return null;
	}
}
