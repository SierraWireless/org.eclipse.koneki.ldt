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
package org.eclipse.koneki.ldt.parser.internal.error;

public class LuaParseErrorNotifier extends LuaParseError {

    public LuaParseErrorNotifier(String errorMessage) {
	super(errorMessage);
	initPositions();
    }

    @Override
    protected void initPositions() {
	setErrorColumn(1);
	setErrorLine(1);
	setErrorOffset(1);
    }

}
