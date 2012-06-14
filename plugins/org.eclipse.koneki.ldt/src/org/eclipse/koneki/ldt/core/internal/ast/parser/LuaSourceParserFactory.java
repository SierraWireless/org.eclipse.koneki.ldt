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

package org.eclipse.koneki.ldt.core.internal.ast.parser;

import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;

/**
 * Provides source parser
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class LuaSourceParserFactory implements ISourceParserFactory {

	public LuaSourceParserFactory() {
	}

	@Override
	public ISourceParser createSourceParser() {
		return new LuaSourceParser();
	}

}
