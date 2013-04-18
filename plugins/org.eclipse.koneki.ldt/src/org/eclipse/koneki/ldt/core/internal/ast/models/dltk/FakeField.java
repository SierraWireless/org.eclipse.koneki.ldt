/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
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
import org.eclipse.dltk.internal.core.SourceField;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaASTNode;

@SuppressWarnings("restriction")
public class FakeField extends SourceField implements ISourceRange, IFakeElement {
	private final int offset;
	private final int length;
	private final int flags;
	private final boolean hasFlags;
	private final LuaASTNode luaASTNode;

	public FakeField(ISourceModule parent, String name, int offset, int length, LuaASTNode luaASTNode) {
		super((ModelElement) parent, name);
		this.offset = offset;
		this.length = length;
		this.flags = 0;
		this.hasFlags = false;
		this.luaASTNode = luaASTNode;
	}

	public FakeField(ISourceModule parent, String name, int offset, int length, int flags, LuaASTNode luaASTNode) {
		super((ModelElement) parent, name);
		this.offset = offset;
		this.length = length;
		this.flags = flags;
		this.hasFlags = true;
		this.luaASTNode = luaASTNode;
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
		return null; //$NON-NLS-1$
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
		return luaASTNode;
	}
}