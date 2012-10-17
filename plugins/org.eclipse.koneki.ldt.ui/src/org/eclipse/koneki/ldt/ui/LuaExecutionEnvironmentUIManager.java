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
package org.eclipse.koneki.ldt.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironment;
import org.eclipse.koneki.ldt.core.internal.buildpath.LuaExecutionEnvironmentManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivity;
import org.eclipse.ui.activities.IActivityListener;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IActivityPatternBinding;

/**
 * A class similar to LuaExecutionEnvironmentManager but take in account activities enable/disabled EEs
 * 
 * Only activities with following format of binding pattern are take in account:
 * <code><plugin id>/org.eclipse.koneki.ldt.executionEnvironment.<EE's id>-<EE's version></code>
 */
public final class LuaExecutionEnvironmentUIManager {

	public static final String EE_EXTENTION_POINT_ID = "org.eclipse.koneki.ldt.executionEnvironment"; //$NON-NLS-1$

	private LuaExecutionEnvironmentUIManager() {
	}

	/**
	 * List all the installed and contributed activities enabled EEs.
	 */
	public static List<LuaExecutionEnvironment> getAvailableExecutionEnvironments() {

		List<LuaExecutionEnvironment> executionEnvironments = LuaExecutionEnvironmentManager.getAvailableExecutionEnvironments();
		List<LuaExecutionEnvironment> newExecutionEnvironments = new ArrayList<LuaExecutionEnvironment>();
		newExecutionEnvironments.addAll(executionEnvironments);

		IActivityManager activityManager = PlatformUI.getWorkbench().getActivitySupport().getActivityManager();

		// For all activities
		@SuppressWarnings("unchecked")
		Set<String> activities = activityManager.getDefinedActivityIds();
		for (String activityId : activities) {
			IActivity activity = activityManager.getActivity(activityId);

			@SuppressWarnings("unchecked")
			Set<IActivityPatternBinding> bindings = activity.getActivityPatternBindings();

			if (!activity.isEnabled()) {
				// For all binding witch contains the EE extension point id
				for (IActivityPatternBinding binding : bindings) {
					if (binding.getString().contains(EE_EXTENTION_POINT_ID)) {

						// Remove all the EE with the same id and version
						for (LuaExecutionEnvironment luaExecutionEnvironment : executionEnvironments) {
							if (luaExecutionEnvironment.isEmbedded() && binding.getString().endsWith(luaExecutionEnvironment.getEEIdentifier())) {
								newExecutionEnvironments.remove(luaExecutionEnvironment);
							}
						}
					}
				}
			}
		}
		return newExecutionEnvironments;
	}

	public static Set<IActivity> addListenerToEERelatedActivity(IActivityListener activityListener) {
		HashSet<IActivity> activitiesWatched = new HashSet<IActivity>();

		// Listen to all activities than can hide EE to refresh the UI
		IActivityManager activityManager = PlatformUI.getWorkbench().getActivitySupport().getActivityManager();

		// For all activities
		@SuppressWarnings("unchecked")
		Set<String> activities = activityManager.getDefinedActivityIds();
		for (String activityId : activities) {
			IActivity activity = activityManager.getActivity(activityId);

			@SuppressWarnings("unchecked")
			Set<IActivityPatternBinding> bindings = activity.getActivityPatternBindings();

			// For all binding witch contains the EE extension point id
			for (IActivityPatternBinding binding : bindings) {
				if (binding.getString().contains(LuaExecutionEnvironmentUIManager.EE_EXTENTION_POINT_ID)) {

					// save activities to watch to be able to unregister listeners
					activitiesWatched.add(activity);

					activity.addActivityListener(activityListener);
				}
			}
		}
		return activitiesWatched;
	}
}
