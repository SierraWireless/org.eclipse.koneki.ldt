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
package org.eclipse.koneki.ldt.editor.internal.text;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;

public class LuaSemanticHighlighting extends SemanticHighlighting {

	private final String preferenceKey;
	private final String displayName;
	private final boolean enablement;

	public LuaSemanticHighlighting(String preferenceKey, String displayName) {
		this(preferenceKey, displayName, true);
	}

	public LuaSemanticHighlighting(String preferenceKey, String displayName, boolean enablement) {
		Assert.isNotNull(preferenceKey);
		this.preferenceKey = preferenceKey;
		this.displayName = displayName;
		this.enablement = enablement;
	}

	public String getPreferenceKey() {
		return preferenceKey;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isSemanticOnly() {
		return displayName != null;
	}

	public String getEnabledPreferenceKey() {
		return enablement ? super.getEnabledPreferenceKey() : null;
	}

	public int hashCode() {
		return preferenceKey.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof LuaSemanticHighlighting) {
			final LuaSemanticHighlighting other = (LuaSemanticHighlighting) obj;
			return preferenceKey.equals(other.preferenceKey);
		}
		return false;
	}

}
