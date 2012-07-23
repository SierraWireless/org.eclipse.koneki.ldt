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
package org.eclipse.koneki.ldt.ui.tests.internal;

import java.text.MessageFormat;

import org.eclipse.jface.text.rules.IToken;

public class ScannerResult {

	private IToken token;
	private int offset;
	private int lenght;

	public ScannerResult(IToken token2, int tokenOffset, int tokenLength) {
		token = token2;
		offset = tokenOffset;
		lenght = tokenLength;
	}

	public IToken getToken() {
		return token;
	}

	public int getOffset() {
		return offset;
	}

	public int getLenght() {
		return lenght;
	}

	@Override
	public String toString() {
		return MessageFormat.format("'{'{0};{1};{2}'}'\n", offset, lenght, token.getData()); //$NON-NLS-1$
	}
}
