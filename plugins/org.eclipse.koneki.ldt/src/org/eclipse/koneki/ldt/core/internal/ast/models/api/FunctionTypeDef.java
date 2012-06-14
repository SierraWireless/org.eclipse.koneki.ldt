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
package org.eclipse.koneki.ldt.core.internal.ast.models.api;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.ASTVisitor;

/**
 * Use to define a kind of type 'function'.
 */
public class FunctionTypeDef extends TypeDef {
	private String documentation;
	private ArrayList<Parameter> parameters = new ArrayList<Parameter>();
	private ArrayList<Return> returns = new ArrayList<Return>();

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public List<Return> getReturns() {
		return returns;
	}

	public void addParameter(final Parameter parameter) {
		parameters.add(parameter);
	}

	public void addReturn(final Return values) {
		returns.add(values);
	}

	/**
	 * @see org.eclipse.dltk.ast.ASTNode#traverse(org.eclipse.dltk.ast.ASTVisitor)
	 */
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			// traverse paramters
			for (Parameter param : parameters) {
				param.traverse(visitor);
			}
			visitor.endvisit(this);
		}
	}
}
