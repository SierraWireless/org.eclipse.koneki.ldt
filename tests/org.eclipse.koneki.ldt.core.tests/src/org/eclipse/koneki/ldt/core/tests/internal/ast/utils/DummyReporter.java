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
 * @date $Date: 2009-07-23 12:07:30 +0200 (jeu., 23 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: DummyReporter.java 2161 2009-07-23 10:07:30Z kkinfoo $
 */
package org.eclipse.koneki.ldt.core.tests.internal.ast.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

/**
 * The Class DummyReporter. Is an empty reporter used for testing.
 */
public class DummyReporter implements IProblemReporter {

	private List<IProblem> problems = new ArrayList<IProblem>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.compiler.problem.IProblemReporter#reportProblem(org.eclipse.dltk.compiler.problem.IProblem)
	 */
	@Override
	public void reportProblem(IProblem problem) {
		problems.add(problem);
	}

	public List<IProblem> getProblems() {
		return problems;
	}
}
