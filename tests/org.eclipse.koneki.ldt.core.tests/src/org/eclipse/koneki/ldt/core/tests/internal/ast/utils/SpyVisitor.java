/*******************************************************************************
 * Copyright (c) 2009, 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.core.tests.internal.ast.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;

/**
 * Just compute nodes types and count.
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 * 
 */
public class SpyVisitor extends ASTVisitor {

	private String _error;
	private int _nodesCount = 0;
	private Map<String, Integer> _types = new HashMap<String, Integer>();
	private List<Object> _countedObjects = new ArrayList<Object>();

	public void clear() {
		_countedObjects = new ArrayList<Object>();
		_nodesCount = 0;
		_types = new HashMap<String, Integer>();
	}

	private void countType(String typeName) {
		int count = _types.containsKey(typeName) ? _types.get(typeName) : 0;
		_types.put(typeName, Integer.valueOf(count + 1));
	}

	/**
	 * Provide error message in error case.
	 * 
	 * @return String Error message when available, empty string else way.
	 */
	public String getErrorMessage() {
		return _error == null ? "" : _error; //$NON-NLS-1$
	}

	/**
	 * Reports if visitor has meet the requested object type.
	 * 
	 * @param String
	 *            nodeType Type of node sought.
	 * @return boolean True if encountered, false else way.
	 */
	public boolean hasVisitedType(String nodeType) {
		return _types.containsKey(nodeType);
	}

	/**
	 * Count of visited nodes
	 * 
	 * @return int
	 */
	public int nodesCount() {
		return _nodesCount;
	}

	public boolean visitGeneral(ASTNode node) throws Exception {

		try {
			if (!_countedObjects.contains(node)) {
				// Keep node's details in mind
				countType(node.getClass().getName());
				_countedObjects.add(node);
				_nodesCount++;
			}
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			_error = e.getMessage();
			throw e;
		}
		return true;
	}

	public Set<String> types() {
		return _types.keySet();
	}

	public int typeCount(String type) {
		try {
			return _types.get(type).intValue();
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			return 0;
		}
	}
}
