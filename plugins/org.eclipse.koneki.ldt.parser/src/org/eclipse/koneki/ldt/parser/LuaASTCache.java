package org.eclipse.koneki.ldt.parser;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.ast.parser.IASTCache;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemCollector;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;

/**
 * Enable framework to cache last AST built, so far they are {@link LuaSourceRoot}.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
@Deprecated
public class LuaASTCache implements IASTCache {
	/** Where AST are stored, the key of the map is it {@link IPath} */
	private Map<IPath, LuaCacheUnit> cache = new HashMap<IPath, LuaCacheUnit>();

	/**
	 * Just as useful as {@link ASTCacheEntry} but adding parsed source code., which will be useful to compute {@link ISourceModule} consistency.
	 * 
	 * @see ASTCacheEntry
	 */
	private class LuaCacheUnit extends ASTCacheEntry {
		public String source;
	}

	/**
	 * Enable to retrieve last AST. Basically {@link ISourceModule} in cache is <strong> not </strong> consistent when not from same source as given
	 * {@link ISourceModule}. In this case <strong>null</strong> is returned.
	 * 
	 * @param module
	 *            AST description
	 * @see ISourceModule
	 * @see IASTCache#restoreModule(org.eclipse.dltk.core.ISourceModule)
	 */
	@Override
	public ASTCacheEntry restoreModule(ISourceModule module) {
		LuaCacheUnit unit = cache.get(module.getPath());
		try {
			// If cached source differs from given source
			if (unit == null || !unit.source.equals(module.getSource())) {
				// AST is not pertinent, return null is asking for given source parsing
				return null;
			}
		} catch (ModelException e) {
			// If no source is given, it is not possible to guarantee AST consistency
			Activator.logWarning(Messages.LuaASTCacheNoSourceProvided, e);
			return null;
		}
		ASTCacheEntry entry = new ASTCacheEntry();
		entry.module = unit.module;
		entry.problems = unit.problems;
		return entry;
	}

	/**
	 * Stores AST in cache with {@link IProblem} when defined
	 * 
	 * @param module
	 *            Description of file to parse
	 * @param moduleDeclaration
	 *            AST from given file
	 * @param problems
	 *            Problem which occurred during AST generation,<strong>null</strong> is allowed when no problem was encountered.
	 * @see IASTCache#storeModule(org.eclipse.dltk.core.ISourceModule, org.eclipse.dltk.ast.parser.IModuleDeclaration,
	 *      org.eclipse.dltk.compiler.problem.ProblemCollector)
	 */
	@Override
	public void storeModule(ISourceModule module, IModuleDeclaration moduleDeclaration, ProblemCollector problems) {
		LuaCacheUnit unit = new LuaCacheUnit();
		unit.module = moduleDeclaration;
		unit.problems = problems;
		try {
			// If no source is given, AST is not cached
			// As it will not possible to provide it ensuring its consistency
			unit.source = module.getSource();
			cache.put(module.getPath(), unit);
		} catch (ModelException e) {
			Activator.logWarning(Messages.LuaASTCacheNoSourceProvided, e);
		}
	}

}
