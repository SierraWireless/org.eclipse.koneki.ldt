/*******************************************************************************
 * Copyright (c) 2013 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.core.internal.ast.models.dltk;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceMethod;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;

@SuppressWarnings("restriction")
public class FakeMethod extends SourceMethod implements ISourceRange, IFakeElement {
	private final int offset;
	private final int length;
	private final int flags;
	private final boolean hasFlags;
	private final String[] parameterNames;
	private final LuaASTNode luaASTNode;

	public FakeMethod(ISourceModule parent, String name, int offset, String[] parameterNames, int length, LuaASTNode luaASTnode) {
		super((ModelElement) parent, name);
		this.offset = offset;
		this.length = length;
		this.flags = 0;
		this.hasFlags = false;
		this.parameterNames = parameterNames;
		this.luaASTNode = luaASTnode;
	}

	public FakeMethod(ISourceModule parent, String name, int offset, int length, String[] parameterNames, int flags, LuaASTNode luaASTnode) {
		super((ModelElement) parent, name);
		this.offset = offset;
		this.length = length;
		this.flags = flags;
		this.hasFlags = true;
		this.parameterNames = parameterNames;
		this.luaASTNode = luaASTnode;
	}

	public String[] getParameterNames() throws ModelException {
		return this.parameterNames;
	}

	public ISourceRange getNameRange() throws ModelException {
		return this;
	}

	public ISourceRange getSourceRange() throws ModelException {
		return this;
	}

	public boolean exists() {
		return true;
	}

	public int getFlags() throws ModelException {
		return hasFlags ? flags : super.getFlags();
	}

	public int getLength() {
		return length;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public String getType() throws ModelException {
		return null;
	}

	/**
	 * @see org.eclipse.dltk.internal.core.SourceField#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// TODO we probably need to override it
		return super.equals(o);
	}

	/**
	 * @see org.eclipse.dltk.internal.core.ModelElement#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO we probably need to override it
		return super.hashCode();
	}

	/**
	 * @see org.eclipse.koneki.ldt.core.internal.ast.models.dltk.IFakeElement#getLuaASTNode()
	 */
	@Override
	public LuaASTNode getLuaASTNode() {
		return this.luaASTNode;
	}
}