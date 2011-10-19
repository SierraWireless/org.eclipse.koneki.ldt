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
package org.eclipse.koneki.ldt.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.DefaultProblemFactory;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.eclipse.koneki.ldt.parser.internal.NodeFactory;
import org.eclipse.koneki.ldt.parser.internal.error.LuaParseError;

/**
 * The Class LuaSourceParser provides a DLTK AST for Lua source code, when an
 * error occur during parsing it provide the previous version of AST.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class LuaSourceParser extends AbstractSourceParser {

	public static Object mutex = new Object();

	/**
	 * Sources cache, allow to keep previous version of source per file in mind.
	 * When syntax errors occurs it's then possible to use previous version of
	 * source, in order to obtain a consistent AST.
	 */
	private static Map<String, String> _cache = null;
	static {
		_cache = Collections.synchronizedMap(new HashMap<String, String>());
	}

	/**
	 * @since 2.0
	 */
	@Override
	public IModuleDeclaration parse(IModuleSource input,
			IProblemReporter reporter) {
		char[] source = input.getContentsAsCharArray();
		String fileName = input.getFileName();
		return parse(fileName.toCharArray(), source, reporter);
	}

	/**
	 * Provides DLTK compliant AST from Metalua analysis
	 * 
	 * @return {@link ModuleDeclaration}, in case of syntax errors, the previous
	 *         valid AST is given
	 * @see org.eclipse.dltk.ast.parser.ISourceParser#parse(char[], char[],
	 *      org.eclipse.dltk.compiler.problem.IProblemReporter)
	 */
	public ModuleDeclaration parse(char[] file, char[] source,
			IProblemReporter reporter) {

		// Analyze code
		ModuleDeclaration ast;
		String code = new String(source);
		NodeFactory factory = null;
		String fileName = new String(file);
		factory = new NodeFactory(code);

		// Search for problem
		if (factory.errorDetected()) {

			// Report it
			IProblem problem = buildProblem(file, factory.analyser());
			reporter.reportProblem(problem);

			// Fetch previous stable source from cache
			if (_cache.containsKey(fileName)) {
				factory = new NodeFactory(_cache.get(fileName));
				ast = factory.getRoot();
			} else {
				// When there is no source code cached, start from scratch
				ast = new ModuleDeclaration(source.length);
			}

		} else {
			// Cache current AST in order to use it in case of error
			_cache.put(fileName, code);
			ast = factory.getRoot();
		}
		return ast;
	}

	/**
	 * Parses Lua error string and founds its position: offset, line, column
	 */
	private IProblem buildProblem(final char[] fileName,
			final LuaParseError analyzer) {
		int col = analyzer.getErrorColumn();
		int line = analyzer.getErrorLine();
		int id = 1;
		String[] args = {};

		// Consider all problems as errors
		int severity = ProblemSeverities.Error;

		// Retrieve Lua error string
		String error = analyzer.getErrorString();

		// Convert file name
		DefaultProblemFactory factory = new DefaultProblemFactory();
		return factory.createProblem(new String(fileName), id, args,
				new String[] { error }, severity, -1, -1, line, col);
	}

}
