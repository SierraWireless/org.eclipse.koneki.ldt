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

/**
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 * 
 */
public class LuaParseErrorAnalyzer extends LuaParseError {

    public LuaParseErrorAnalyzer(String errorMessage) {
	super(errorMessage);
	initPositions();
    }

    private Integer extractIntFromErrorString(final String startTag,
	    final char endTag) {
	return extractIntFromErrorString(startTag, endTag, 0);

    }

    private Integer extractIntFromErrorString(final String startTag,
	    final char endTag, final int shift) {
	String errorMessage = shift > 0 ? getErrorString().substring(shift)
		: getErrorString();

	int offsetStart = errorMessage.indexOf(startTag) + startTag.length();
	int offsetEnd = errorMessage.indexOf(endTag);
	String offset = errorMessage.substring(offsetStart, offsetEnd);
	return Integer.parseInt(offset);

    }

    @Override
    protected void initPositions() {
	// Error column
	String tag = " column ";
	int shift = getErrorString().indexOf(tag);
	setErrorColumn(extractIntFromErrorString(tag, ',', shift));

	// Error line
	setErrorLine(extractIntFromErrorString(" line ", ','));

	// Offset
	shift = getErrorString().indexOf('>');
	setErrorOffset(extractIntFromErrorString(" char ", ':', shift));
    }
}
