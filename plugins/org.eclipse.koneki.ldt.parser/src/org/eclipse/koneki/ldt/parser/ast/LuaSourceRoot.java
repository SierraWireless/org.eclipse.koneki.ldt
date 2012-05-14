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
 * $Id: LuaSourceRoot.java 2190 2009-07-29 15:56:04Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.koneki.ldt.parser.api.external.LuaASTNode;
import org.eclipse.koneki.ldt.parser.api.external.LuaFileAPI;

/**
 * The Root AST Node of a lua source file.
 */
public class LuaSourceRoot extends ModuleDeclaration {

	private DefaultProblem problem = null;
	private LuaFile luaFile;
	private boolean error;

	public LuaSourceRoot(final int sourceLength) {
		super(sourceLength);
		luaFile = new LuaFile();
		addStatement(luaFile);
	}

	public LuaSourceRoot(final int length, final boolean rebuild) {
		super(length, rebuild);
		luaFile = new LuaFile();
		addStatement(luaFile);
	}

	public LuaFileAPI getFileapi() {
		return luaFile.getApi();
	}

	public LuaInternalContent getInternalContent() {
		return luaFile.getInternalContent();
	}

	public void setProblem(final int line, final int column, final int offset, final String message) {
		final IProblemIdentifier id = DefaultProblemIdentifier.decode(offset);
		problem = new DefaultProblem("", message, id, new String[0], ProblemSeverity.ERROR, offset, -1, line, column); //$NON-NLS-1$
		setError(true);
	}

	public DefaultProblem getProblem() {
		return problem;
	}

	public boolean hasError() {
		return error;
	}

	public void setError(final boolean status) {
		error = status;
	}

	public void setLuaFileApi(final LuaFileAPI api) {
		luaFile.setApi(api);
	}

	public void setInternalContent(final LuaInternalContent content) {
		luaFile.setInternalContent(content);
	}

	@Override
	public int hashCode() {
		// we do this only to avoid findbug errors.
		// findbugs detects that ASTNode override equals but not hashcode
		// but equals is override by super.equals ...
		return super.hashCode();
	}

	/********************************************************
	 * this is a complete representation of a lua file <br/>
	 * External API + Local AST
	 */
	private static class LuaFile extends LuaASTNode {

		// this is the API of the current Lua file.
		private LuaFileAPI fileAPI;

		// this is the internal representation of code
		private LuaInternalContent internalContent;

		public LuaFile() {
			internalContent = new LuaInternalContent();
			fileAPI = new LuaFileAPI();
		}

		public LuaInternalContent getInternalContent() {
			return internalContent;
		}

		public void setInternalContent(LuaInternalContent internalContent) {
			this.internalContent = internalContent;
		}

		public void setApi(final LuaFileAPI file) {
			fileAPI = file;
		}

		public LuaFileAPI getApi() {
			return fileAPI;
		}

		/**
		 * @see org.eclipse.dltk.ast.ASTNode#traverse(org.eclipse.dltk.ast.ASTVisitor)
		 */
		@Override
		public void traverse(ASTVisitor visitor) throws Exception {
			if (visitor.visit(this)) {
				if (getApi() != null) {
					fileAPI.traverse(visitor);
					internalContent.traverse(visitor);
				}
				visitor.endvisit(this);
			}

		}
	}
}
