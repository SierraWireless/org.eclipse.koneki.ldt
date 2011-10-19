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
package org.eclipse.koneki.ldt.templates.internal;

import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.koneki.ldt.ui.LuaUILanguageToolkit;

public class LuaTemplateUILanguageToolkit extends LuaUILanguageToolkit {

    public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {

	return new LuaTemplateSourceViewerConfiguration(getTextTools()
		.getColorManager(), getPreferenceStore(), null,
		getPartitioningId(), false);
    }
}
