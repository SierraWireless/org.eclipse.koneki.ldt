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
package org.eclipse.koneki.ldt.ui.internal.editor;

public final class LuaDocumentorTags {

	public static final String MODULE = "@module"; //$NON-NLS-1$
	public static final String FUNCTION = "@function"; //$NON-NLS-1$
	public static final String PARAM = "@param"; //$NON-NLS-1$
	public static final String FIELD = "@field"; //$NON-NLS-1$
	public static final String TYPE = "@type"; //$NON-NLS-1$
	public static final String RETURN = "@return"; //$NON-NLS-1$
	public static final String USAGE = "@usage"; //$NON-NLS-1$

	private LuaDocumentorTags() {
		// private constructor
	}

	public static String[] getTags() {
		return new String[] { MODULE, FUNCTION, PARAM, FIELD, TYPE, RETURN, USAGE };
	}

}
