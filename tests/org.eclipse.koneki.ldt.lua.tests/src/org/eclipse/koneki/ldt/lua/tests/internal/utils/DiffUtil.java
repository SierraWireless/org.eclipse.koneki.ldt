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
package org.eclipse.koneki.ldt.lua.tests.internal.utils;

import java.util.LinkedList;

import diff.match.patch.diff_match_patch.Diff;

public final class DiffUtil {

	private DiffUtil() {
	}

	/**
	 * Convert a Diff list into a pretty text report.
	 * 
	 * @param diffs
	 *            LinkedList of Diff objects.
	 * @return HTML representation.
	 */
	// CHECKSTYLE:OFF
	public static final String diff_pretty_diff(LinkedList<Diff> diffs) {
		StringBuilder html = new StringBuilder();
		for (Diff aDiff : diffs) {
			String text = aDiff.text;
			switch (aDiff.operation) {
			case INSERT:
				html.append("[+++[").append(text).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case DELETE:
				html.append("[---[").append(text).append("]]"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case EQUAL:
				html.append(text);
				break;
			}
		}
		return html.toString();
	}
	// CHECKSTYLE:ON
}
