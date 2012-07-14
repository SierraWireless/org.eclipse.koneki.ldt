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
package org.eclipse.koneki.ldt.ui.internal.editor.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateProposal;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.swt.graphics.Image;

public class LuaDocumentorTemplateProposal extends ScriptTemplateProposal {

	public LuaDocumentorTemplateProposal(Template template, TemplateContext context, IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
	}

	@Override
	public boolean isAutoInsertable() {
		return getTemplate().isAutoInsertable();
	}

}
