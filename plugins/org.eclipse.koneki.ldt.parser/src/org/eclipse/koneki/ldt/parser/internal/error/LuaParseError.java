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

public abstract class LuaParseError {
    private String _errorString;
    private Integer _errorLine;
    private Integer _errorOffset;
    private Integer _errorCol;

    public LuaParseError(String errorMessage) {
	_errorString = errorMessage;
    }

    public String getErrorString() {
	return _errorString;
    }

    public Integer getErrorLine() {
	return _errorLine;
    }

    public Integer getErrorOffset() {
	return _errorOffset;
    }

    public Integer getErrorColumn() {
	return _errorCol;
    }

    protected abstract void initPositions();

    protected void setErrorLine(Integer errorLine) {
	_errorLine = errorLine;
    }

    protected void setErrorOffset(Integer errorOffset) {
	_errorOffset = errorOffset;
    }

    protected void setErrorColumn(Integer errorCol) {
	_errorCol = errorCol;
    }
}
