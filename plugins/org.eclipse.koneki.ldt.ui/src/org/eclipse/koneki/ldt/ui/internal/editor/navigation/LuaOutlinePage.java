/*******************************************************************************
 * Copyright (c) 2012 Marc-Andre Laperle and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marc-Andre Laperle - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.editor.navigation;

import java.util.ArrayList;

import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.IScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.actions.MemberFilterActionGroup;
import org.eclipse.dltk.ui.viewsupport.AbstractModelElementFilter;
import org.eclipse.dltk.ui.viewsupport.MemberFilterAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.ui.IActionBars;

public class LuaOutlinePage extends ScriptOutlinePage {

	public LuaOutlinePage(IScriptEditor editor, IPreferenceStore store) {
		super(editor, store);
	}

	private static class LocalFunctionFilter extends AbstractModelElementFilter {

		public String getFilteringType() {
			return "LocalFunctionFilter"; //$NON-NLS-1$
		}

		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IMember) {
				IMember member = (IMember) element;
				try {
					if (member.getElementType() == IModelElement.METHOD && Flags.isPrivate(member.getFlags())) {
						return false;
					}
				} catch (ModelException e) {
					Activator.logError(Messages.MemberFilterActionGroup_hide_local_functions_error, e);
				}
			}
			return true;
		}
	}

	@Override
	protected void registerSpecialToolbarActions(IActionBars actionBars) {
		IToolBarManager toolBarManager = actionBars.getToolBarManager();

		MemberFilterActionGroup memberFilterActionGroup = new MemberFilterActionGroup(fOutlineViewer, fStore);
		ArrayList<MemberFilterAction> actions = new ArrayList<MemberFilterAction>(1);

		// Hide local functions
		String title = Messages.MemberFilterActionGroup_hide_local_functions_label;
		LocalFunctionFilter filter = new LocalFunctionFilter();
		MemberFilterAction hideLocalFunctionsAction = new MemberFilterAction(memberFilterActionGroup, title, filter, "", true); //$NON-NLS-1$
		hideLocalFunctionsAction.setDescription(Messages.MemberFilterActionGroup_hide_local_functions_description);
		hideLocalFunctionsAction.setToolTipText(Messages.MemberFilterActionGroup_hide_local_functions_tooltip);
		DLTKPluginImages.setLocalImageDescriptors(hideLocalFunctionsAction, "filter_methods.gif"); //$NON-NLS-1$
		actions.add(hideLocalFunctionsAction);

		MemberFilterAction[] filterActions = (MemberFilterAction[]) actions.toArray(new MemberFilterAction[actions.size()]);
		memberFilterActionGroup.setActions(filterActions);
		memberFilterActionGroup.contributeToToolBar(toolBarManager);
	}

}
