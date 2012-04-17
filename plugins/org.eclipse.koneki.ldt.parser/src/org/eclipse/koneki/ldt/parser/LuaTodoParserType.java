/*******************************************************************************
 * Copyright (c) 2012 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/

package org.eclipse.koneki.ldt.parser;

import org.eclipse.dltk.compiler.task.ITodoTaskPreferences;
import org.eclipse.dltk.compiler.task.TodoTaskPreferencesOnPreferenceLookupDelegate;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.AbstractTodoTaskBuildParticipantType;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.koneki.ldt.Activator;

public class LuaTodoParserType extends AbstractTodoTaskBuildParticipantType {

	@Override
	protected ITodoTaskPreferences getPreferences(IScriptProject project) {
		return new TodoTaskPreferencesOnPreferenceLookupDelegate(Activator.PLUGIN_ID, project);
	}

	/**
	 * @see org.eclipse.dltk.core.builder.AbstractTodoTaskBuildParticipantType#getBuildParticipant(org.eclipse.dltk.compiler.task.ITodoTaskPreferences)
	 */
	@Override
	protected IBuildParticipant getBuildParticipant(ITodoTaskPreferences preferences) {
		return new TodoTaskBuildParticipant(preferences) {
			@Override
			protected int findCommentStart(char[] content, int begin, int end) {
				begin = skipSpaces(content, begin, end);
				for (int i = begin; i < end; ++i) {
					if (content[i] == '-' && i + 1 < end && content[i + 1] == '-') {
						return i + 2;
					}
				}
				return -1;
			}
		};
	}

}
