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

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-06-18 16:46:07 +0200 (jeu., 18 juin 2009) $
 * $Author: kkinfoo $
 * $Id: Number.java 1887 2009-06-18 14:46:07Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.expressions;

import org.eclipse.dltk.ast.expressions.FloatNumericLiteral;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;
import org.eclipse.koneki.ldt.parser.internal.IndexedNode;

// TODO: Auto-generated Javadoc
/**
 * The Class Number.
 */
public class Number extends FloatNumericLiteral implements
	LuaExpressionConstants, IndexedNode {

    private long id;

    /**
     * Instantiates a new number.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     * @param value
     *            the value
     */
    public Number(int start, int end, double value) {
	super(start, end, value);
    }

    public long getID() {
	return id;
    }

    public void printNode(CorePrinter output) {
	output.formatPrintLn(getValue());
    }

    public void setID(long id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.dltk.ast.expressions.Literal#toString()
     */
    public java.lang.String toString() {
	return getValue();
    }
}
