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
package org.eclipse.koneki.ldt.ui.internal.editor.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class LuaDocMultLineCommentRule extends LuaMultLineCommentRule {

	public LuaDocMultLineCommentRule(IToken docToken) {
		super(docToken);
	}

	@Override
	protected IToken doEvaluateContent(ICharacterScanner scanner) {
		int c = scanner.read();
		readCount++;
		// if content don't start by a - this is not a multline comment doc
		if (c != '-') {
			return Token.UNDEFINED;
		}
		return super.doEvaluateContent(scanner);
	}
}