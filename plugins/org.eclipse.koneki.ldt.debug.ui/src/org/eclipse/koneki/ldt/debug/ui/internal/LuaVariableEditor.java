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
package org.eclipse.koneki.ldt.debug.ui.internal;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.actions.variables.details.DetailPaneAssignValueAction;
import org.eclipse.debug.internal.ui.elements.adapters.DefaultVariableCellModifier;
import org.eclipse.debug.internal.ui.elements.adapters.VariableColumnPresentation;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementEditor;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class LuaVariableEditor implements IElementEditor {

	/**
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.IElementEditor#getCellEditor(org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext,
	 *      java.lang.String, java.lang.Object, org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public CellEditor getCellEditor(IPresentationContext context, String columnId, Object element, Composite parent) {
		return new TextCellEditor(parent);
	}

	/**
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.IElementEditor#getCellModifier(org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext,
	 *      java.lang.Object)
	 */
	@Override
	public ICellModifier getCellModifier(IPresentationContext context, Object element) {
		return new DefaultVariableCellModifier() {
			@SuppressWarnings("deprecation")
			public Object getValue(Object element, String property) {
				if (VariableColumnPresentation.COLUMN_VARIABLE_VALUE.equals(property)) {
					if (element instanceof IVariable) {
						IVariable variable = (IVariable) element;
						try {
							return variable.getValue().getValueString();
						} catch (DebugException e) {
							DebugUIPlugin.log(e);
						}
					}
				}
				return null;
			}

			@SuppressWarnings("deprecation")
			public void modify(Object element, String property, Object value) {
				Object oldValue = getValue(element, property);
				if (!value.equals(oldValue)) {
					if (VariableColumnPresentation.COLUMN_VARIABLE_VALUE.equals(property)) {
						if (element instanceof IVariable) {
							if (value instanceof String) {
								// The value column displays special characters escaped, so encode the string with any special characters escaped
								// properly
								IVariable variable = (IVariable) element;
								DetailPaneAssignValueAction.assignValue(DebugUIPlugin.getShell(), variable, (String) value);
							}
						}
					}
				}
			}
		};
	}

}
