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

package org.eclipse.koneki.ldt.parser.ast.expressions;

import org.eclipse.dltk.ast.expressions.NilLiteral;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;
import org.eclipse.koneki.ldt.parser.internal.IndexedNode;

public class Nil extends NilLiteral implements LuaExpressionConstants, IndexedNode {

    private long id;

    public Nil(int start, int end) {
	super(start, end);
    }

    @Override
    public int getKind() {
	return NIL_LITTERAL;
    }

    public long getID() {
	return id;
    }

    public void setID(long id) {
	this.id = id;
    }

}
