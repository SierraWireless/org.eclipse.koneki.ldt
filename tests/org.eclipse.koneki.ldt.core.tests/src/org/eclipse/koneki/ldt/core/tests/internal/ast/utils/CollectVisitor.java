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
package org.eclipse.koneki.ldt.core.tests.internal.ast.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;

/**
 * TODO Comment this class
 */
public class CollectVisitor extends ASTVisitor {

	private Map<Class<?>, List<Object>> collection = new HashMap<Class<?>, List<Object>>();

	/**
	 * @see org.eclipse.dltk.ast.ASTVisitor#visitGeneral(org.eclipse.dltk.ast.ASTNode)
	 */
	@Override
	public boolean visitGeneral(ASTNode node) throws Exception {
		List<Object> list = collection.get(node.getClass());
		if (list == null) {
			list = new ArrayList<Object>();
			collection.put(node.getClass(), list);
		}
		list.add(node);
		return super.visitGeneral(node);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] getASTNode(Class<T> clazz) {
		List<Object> list = collection.get(clazz);
		T[] result;
		if (list != null && !list.isEmpty()) {
			result = (T[]) Array.newInstance(clazz, list.size());
			for (int i = 0; i < result.length; i++) {
				result[i] = (T) list.get(i);
			}
		} else {
			result = (T[]) Array.newInstance(clazz, 0);
		}

		return result;
	}
}
