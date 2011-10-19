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
 * @date $Date: 2009-07-29 17:56:04 +0200 (mer., 29 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: LuaModuleDeclaration.java 2190 2009-07-29 15:56:04Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;

// TODO: Auto-generated Javadoc
/**
 * The Class LuaModuleDeclaration.
 */
public class LuaModuleDeclaration extends ModuleDeclaration {

    /**
     * Instantiates a new lua module declaration.
     * 
     * @param sourceLength
     *            the source length
     */
    public LuaModuleDeclaration(int sourceLength) {
	super(sourceLength);
    }

    /**
     * Instantiates a new lua module declaration.
     * 
     * @param length
     *            the length
     * @param rebuild
     *            the rebuild
     */
    public LuaModuleDeclaration(int length, boolean rebuild) {
	super(length, rebuild);
    }

    @Override
    public boolean equals(Object o) {
	return o instanceof LuaModuleDeclaration;
    }
}
