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
package org.eclipse.koneki.ldt.core.internal.ast.models;

import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

public final class LuaDLTKModelUtils {
	private LuaDLTKModelUtils() {

	}

	public static boolean isAncestor(IModelElement element, IModelElement ancestor) {
		return ancestor != null && element != null && (ancestor.equals(element.getParent()) || isAncestor(element.getParent(), ancestor));
	}

	private static boolean isModule(int flags) {
		return (flags & Flags.AccModule) != 0;
	}

	public static boolean isModule(IMember member) throws ModelException {
		return member instanceof IType && isModule(member.getFlags());
	}

	public static boolean isModuleFunction(IMember member) throws ModelException {
		return member instanceof IMethod && isModule(member.getFlags());
	}

	public static boolean isGlobalTable(IMember member) throws ModelException {
		return member instanceof IType && Flags.isPublic(member.getFlags());
	}

	public static boolean isLocalTable(IMember member) throws ModelException {
		return member instanceof IType && Flags.isPrivate(member.getFlags());
	}

}
