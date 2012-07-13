package org.eclipse.koneki.ldt.debug.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementEditor;

@SuppressWarnings("restriction")
public class LuaDebugElementAdapterFactory implements IAdapterFactory {

	private IElementEditor editor = new LuaVariableEditor();

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adapterType.equals(IElementEditor.class)) {
			if (adaptableObject instanceof IVariable) {
				return editor;
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { IElementEditor.class };
	}
}
