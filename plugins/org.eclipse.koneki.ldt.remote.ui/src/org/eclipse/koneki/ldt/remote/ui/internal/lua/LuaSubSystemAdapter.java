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
package org.eclipse.koneki.ldt.remote.ui.internal.lua;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.koneki.ldt.remote.core.internal.lua.LuaSubSystem;
import org.eclipse.koneki.ldt.remote.ui.internal.Activator;
import org.eclipse.rse.internal.ui.view.SystemViewSubSystemAdapter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

@SuppressWarnings("restriction")
public class LuaSubSystemAdapter extends SystemViewSubSystemAdapter {

	private LuaSubSystem luaSubSystem;

	public LuaSubSystemAdapter(LuaSubSystem subSystem) {
		luaSubSystem = subSystem;
	}

	/**
	 * @see org.eclipse.rse.ui.view.AbstractSystemViewAdapter#internalGetPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object property) {
		if (property instanceof String) {
			return luaSubSystem.getLuaPropertyValue((String) property);
		} else {
			if (property == null)
				Activator.logWarning("Try to get a property for null key"); //$NON-NLS-1$
			else
				Activator.logWarning(MessageFormat.format("Try to get a property for a non String key (key = {0}, keytype = {1}) ", //$NON-NLS-1$
						property.toString(), property.getClass().getName()));
			return null;
		}
	}

	/**
	 * @see org.eclipse.rse.internal.ui.view.SystemViewSubSystemAdapter#internalGetPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		List<IPropertyDescriptor> result = new ArrayList<IPropertyDescriptor>();

		// Lua Command
		PropertyDescriptor luaCommandPD = new TextPropertyDescriptor(LuaSubSystem.LUACOMMAND_PROPERTY_KEY, "Lua Command"); //$NON-NLS-1$
		luaCommandPD.setDescription("The command use to run the Lua VM."); //$NON-NLS-1$
		result.add(luaCommandPD);

		// Lua path
		PropertyDescriptor luaPathPD = new TextPropertyDescriptor(LuaSubSystem.LUAPATH_PROPERTY_KEY, "Lua Path"); //$NON-NLS-1$
		luaPathPD.setDescription("The Lua path of the lua VM."); //$NON-NLS-1$
		luaPathPD.setFilterFlags(new String[] { IPropertySheetEntry.FILTER_ID_EXPERT });
		result.add(luaPathPD);

		// Lua cpath
		PropertyDescriptor luaCPathPD = new TextPropertyDescriptor(LuaSubSystem.LUACPATH_PROPERTY_KEY, "Lua CPath"); //$NON-NLS-1$
		luaCPathPD.setDescription("The Lua cpath of the lua VM."); //$NON-NLS-1$
		luaCPathPD.setFilterFlags(new String[] { IPropertySheetEntry.FILTER_ID_EXPERT });
		result.add(luaCPathPD);

		// LD_LIBRARY_PATH
		PropertyDescriptor ldLibraryPathPD = new TextPropertyDescriptor(LuaSubSystem.LDLIBRARYPATH_PROPERTY_KEY, "LD_LIBRARY_PATH"); //$NON-NLS-1$
		ldLibraryPathPD.setDescription("The Lua cpath of the lua VM."); //$NON-NLS-1$
		ldLibraryPathPD.setFilterFlags(new String[] { IPropertySheetEntry.FILTER_ID_EXPERT });
		result.add(ldLibraryPathPD);

		// output directory
		PropertyDescriptor outputDirectoryPD = new TextPropertyDescriptor(LuaSubSystem.OUTPUTDIRECTORY_PROPERTY_KEY, "Output Directory"); //$NON-NLS-1$
		outputDirectoryPD.setDescription("The directory where lua application could be deploy at run or debug time."); //$NON-NLS-1$
		result.add(outputDirectoryPD);

		return result.toArray(new IPropertyDescriptor[result.size()]);
	}

	/**
	 * @see org.eclipse.rse.internal.ui.view.SystemViewSubSystemAdapter#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object property, Object value) {
		if (property instanceof String) {
			if (value == null)
				luaSubSystem.setLuaPropertyValue((String) property, null);
			else
				luaSubSystem.setLuaPropertyValue((String) property, value.toString());
		} else {
			if (property == null)
				Activator.logWarning("Try to set a property for null key"); //$NON-NLS-1$
			else
				Activator.logWarning(MessageFormat.format("Try to set a property for a non String key (key = {0}, keytype = {1}) ", //$NON-NLS-1$
						property.toString(), property.getClass().getName()));
		}
	}

	/**
	 * @see org.eclipse.rse.internal.ui.view.SystemViewSubSystemAdapter#resetPropertyValue(java.lang.Object)
	 */
	@Override
	public void resetPropertyValue(Object property) {
		setPropertyValue(property, null);
	}

	/**
	 * @see org.eclipse.rse.internal.ui.view.SystemViewSubSystemAdapter#isPropertySet(java.lang.Object)
	 */
	@Override
	public boolean isPropertySet(Object propertyObject) {
		return true;
	}
}
