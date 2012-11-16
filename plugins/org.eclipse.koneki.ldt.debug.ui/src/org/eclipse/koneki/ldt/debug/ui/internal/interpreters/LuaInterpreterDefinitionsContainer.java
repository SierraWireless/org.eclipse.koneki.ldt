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
package org.eclipse.koneki.ldt.debug.ui.internal.interpreters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.internal.launching.IInterpreterInstallExtensionContainer;
import org.eclipse.dltk.internal.launching.InterpreterDefinitionsContainer;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.LibraryLocation;
import org.eclipse.dltk.launching.ScriptRuntime.DefaultInterpreterEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/////////////////////////////////////////////////////////////////////////////////////
// This code contains a lot of code copied from org.eclipse.dltk.internal.launching.InterpreterDefinitionsContainer
// to workaround DLTK BUG 390358 
/////////////////////////////////////////////////////////////////////////////////////
//CHECKSTYLE:OFF
public class LuaInterpreterDefinitionsContainer extends InterpreterDefinitionsContainer {

	private final class DefaultInterpreterComparator implements Comparator<DefaultInterpreterEntry> {
		public int compare(DefaultInterpreterEntry entry0, DefaultInterpreterEntry entry1) {
			String k0 = entry0.getEnvironment() + ":" + entry0.getNature(); //$NON-NLS-1$
			String k1 = entry1.getEnvironment() + ":" + entry1.getNature(); //$NON-NLS-1$
			return k0.compareTo(k1);
		}
	}

	private static final String PATH_ATTR = "path"; //$NON-NLS-1$
	private static final String INTERPRETER_NAME_ATTR = "name"; //$NON-NLS-1$
	private static final String INTERPRETER_TAG = "interpreter"; //$NON-NLS-1$
	private static final String INTERPRETER_TYPE_TAG = "interpreterType"; //$NON-NLS-1$
	private static final String ID_ATTR = "id"; //$NON-NLS-1$
	private static final String NATURE_ATTR = "nature"; //$NON-NLS-1$
	private static final String ENVIRONMENT_ATTR = "environment"; //$NON-NLS-1$
	private static final String DEFAULT_INTERPRETER_TAG = "defaultInterpreter"; //$NON-NLS-1$
	private static final String INTERPRETER_SETTINGS_TAG = "interpreterSettings"; //$NON-NLS-1$
	private static final String VARIABLE_VALUE_ATTR = "variableValue"; //$NON-NLS-1$
	private static final String VARIABLE_NAME_ATTR = "variableName"; //$NON-NLS-1$
	private static final String LIBRARY_PATH_ATTR = "libraryPath"; //$NON-NLS-1$
	private static final String IARGS_ATTR = "iargs"; //$NON-NLS-1$
	private static final String ENVIRONMENT_VARIABLES_TAG = "environmentVariables"; //$NON-NLS-1$
	private static final String ENVIRONMENT_VARIABLE_TAG = "environmentVariable"; //$NON-NLS-1$
	private static final String LIBRARY_LOCATIONS_TAG = "libraryLocations"; //$NON-NLS-1$
	private static final String LIBRARY_LOCATION_TAG = "libraryLocation"; //$NON-NLS-1$
	private static final String ENVIRONMENT_ID = "environmentId"; //$NON-NLS-1$
	private static final String EXTENSIONS_TAG = "extensions"; //$NON-NLS-1$
	/**
	 * Cached list of Interpreters in this container
	 */
	private List<IInterpreterInstall> fInterpreterList;

	// /**
	// * Interpreters managed by this container whose install locations don't
	// * actually exist.
	// */
	// private List fInvalidInterpreterList;

	/**
	 * The composite identifier of the default Interpreter. This consists of the install type ID plus an ID for the Interpreter.
	 */
	// map bind default interpreter to each nature
	private Map<DefaultInterpreterEntry, String> fDefaultInterpreterInstallCompositeID;

	/**
	 * The identifier of the connector to use for the default Interpreter.
	 */
	// map bind default connector to each nature
	private Map<DefaultInterpreterEntry, String> fDefaultInterpreterInstallConnectorTypeID;

	/**
	 * Constructs an empty Interpreter container
	 */
	public LuaInterpreterDefinitionsContainer() {
		new ArrayList<Object>(10);
		fInterpreterList = new ArrayList<IInterpreterInstall>(10);
		fDefaultInterpreterInstallCompositeID = new HashMap<DefaultInterpreterEntry, String>();
		fDefaultInterpreterInstallConnectorTypeID = new HashMap<DefaultInterpreterEntry, String>();
	}

	/**
	 * Returns list of default interpreters natures TODO: rename
	 * 
	 * @return
	 */
	public DefaultInterpreterEntry[] getInterpreterNatures() {
		Set<DefaultInterpreterEntry> s = new HashSet<DefaultInterpreterEntry>(fDefaultInterpreterInstallCompositeID.keySet());
		for (IInterpreterInstall install : fInterpreterList) {
			s.add(new DefaultInterpreterEntry(install.getNatureId(), install.getEnvironmentId()));
		}
		return s.toArray(new DefaultInterpreterEntry[s.size()]);
	}

	/**
	 * Returns the composite ID for the default Interpreter. The composite ID consists of an ID for the Interpreter install type together with an ID
	 * for Interpreter. This is necessary because Interpreter ids by themselves are not necessarily unique across Interpreter install types.
	 * 
	 * @return String returns the composite ID of the current default Interpreter
	 */
	public String getDefaultInterpreterInstallCompositeID(DefaultInterpreterEntry nature) {
		return fDefaultInterpreterInstallCompositeID.get(nature);
	}

	public String[] getDefaultInterpreterInstallCompositeID() {
		Collection<String> ids = fDefaultInterpreterInstallCompositeID.values();
		return ids.toArray(new String[ids.size()]);
	}

	/**
	 * Sets the composite ID for the default Interpreter. The composite ID consists of an ID for the Interpreter install type together with an ID for
	 * Interpreter. This is necessary because Interpreter ids by themselves are not necessarily unique across Interpreter install types.
	 * 
	 * @param id
	 *            identifies the new default Interpreter using a composite ID
	 */
	public void setDefaultInterpreterInstallCompositeID(DefaultInterpreterEntry nature, String id) {
		if (id != null)
			fDefaultInterpreterInstallCompositeID.put(nature, id);
		else
			fDefaultInterpreterInstallCompositeID.remove(nature);
	}

	/**
	 * Return the default Interpreter's connector type ID.
	 * 
	 * @return String the current value of the default Interpreter's connector type ID
	 */
	public String getDefaultInterpreterInstallConnectorTypeID(DefaultInterpreterEntry nature) {
		return fDefaultInterpreterInstallConnectorTypeID.get(nature);
	}

	/**
	 * Set the default Interpreter's connector type ID.
	 * 
	 * @param id
	 *            the new value of the default Interpreter's connector type ID
	 */
	public void setDefaultInterpreterInstallConnectorTypeID(DefaultInterpreterEntry nature, String id) {
		fDefaultInterpreterInstallConnectorTypeID.put(nature, id);
	}

	/**
	 * Return the Interpreter definitions contained in this object as a String of XML. The String is suitable for storing in the workbench
	 * preferences.
	 * <p>
	 * The resulting XML is compatible with the static method <code>parseXMLIntoContainer</code>.
	 * </p>
	 * 
	 * @return String the results of flattening this object into XML
	 * @throws IOException
	 *             if this method fails. Reasons include:
	 *             <ul>
	 *             <li>serialization of the XML document failed</li>
	 *             </ul>
	 * @throws ParserConfigurationException
	 *             if creation of the XML document failed
	 * @throws TransformerException
	 *             if serialization of the XML document failed
	 */
	@Override
	public String getAsXML() throws ParserConfigurationException, IOException, TransformerException {

		// Create the Document and the top-level node
		Document doc = DLTKLaunchingPlugin.getDocument();
		Element config = doc.createElement(INTERPRETER_SETTINGS_TAG);
		doc.appendChild(config);

		// Set the defaultInterpreter attribute on the top-level node
		List<DefaultInterpreterEntry> keys = new ArrayList<DefaultInterpreterEntry>();
		keys.addAll(fDefaultInterpreterInstallCompositeID.keySet());
		Collections.sort(keys, new DefaultInterpreterComparator());

		for (Iterator<DefaultInterpreterEntry> iter = keys.iterator(); iter.hasNext();) {
			DefaultInterpreterEntry entry = iter.next();
			Element defaulte = doc.createElement(DEFAULT_INTERPRETER_TAG);
			config.appendChild(defaulte);
			defaulte.setAttribute(NATURE_ATTR, entry.getNature());
			defaulte.setAttribute(ENVIRONMENT_ATTR, entry.getEnvironment());
			defaulte.setAttribute(ID_ATTR, fDefaultInterpreterInstallCompositeID.get(entry));
		}

		List<DefaultInterpreterEntry> keys2 = new ArrayList<DefaultInterpreterEntry>();
		keys2.addAll(fDefaultInterpreterInstallConnectorTypeID.keySet());
		Collections.sort(keys2, new DefaultInterpreterComparator());

		// Set the defaultInterpreterConnector attribute on the top-level node
		for (Iterator<DefaultInterpreterEntry> iter = keys2.iterator(); iter.hasNext();) {
			DefaultInterpreterEntry entry = iter.next();
			Element defaulte = doc.createElement("defaultInterpreterConnector"); //$NON-NLS-1$
			config.appendChild(defaulte);
			defaulte.setAttribute(NATURE_ATTR, entry.getNature());
			defaulte.setAttribute(ENVIRONMENT_ATTR, entry.getEnvironment());
			defaulte.setAttribute(ID_ATTR, fDefaultInterpreterInstallConnectorTypeID.get(entry));
		}

		// Create a node for each install type represented in this container
		Set<IInterpreterInstallType> InterpreterInstallTypeSet = getInterpreterTypeToInterpreterMap().keySet();
		Iterator<IInterpreterInstallType> keyIterator = InterpreterInstallTypeSet.iterator();
		while (keyIterator.hasNext()) {
			IInterpreterInstallType InterpreterInstallType = keyIterator.next();
			Element InterpreterTypeElement = interpreterTypeAsElement(doc, InterpreterInstallType);
			config.appendChild(InterpreterTypeElement);
		}

		// Serialize the Document and return the resulting String
		return DLTKLaunchingPlugin.serializeDocument(doc);
	}

	/**
	 * Create and return a node for the specified Interpreter install type in the specified Document.
	 */
	private Element interpreterTypeAsElement(Document doc, IInterpreterInstallType InterpreterType) {

		// Create a node for the Interpreter type and set its 'id' attribute
		Element element = doc.createElement(INTERPRETER_TYPE_TAG);
		element.setAttribute(ID_ATTR, InterpreterType.getId());

		// For each Interpreter of the specified type, create a subordinate node
		// for it
		List<IInterpreterInstall> InterpreterList = getInterpreterTypeToInterpreterMap().get(InterpreterType);
		Iterator<IInterpreterInstall> InterpreterIterator = InterpreterList.iterator();
		while (InterpreterIterator.hasNext()) {
			IInterpreterInstall Interpreter = InterpreterIterator.next();
			Element InterpreterElement = interpreterAsElement(doc, Interpreter);
			element.appendChild(InterpreterElement);
		}

		return element;
	}

	/**
	 * Create and return a node for the specified Interpreter in the specified Document.
	 */
	private Element interpreterAsElement(Document doc, IInterpreterInstall interpreter) {

		// Create the node for the Interpreter and set its 'id' & 'name'
		// attributes
		Element element = doc.createElement(INTERPRETER_TAG);
		element.setAttribute(ID_ATTR, interpreter.getId());
		element.setAttribute(INTERPRETER_NAME_ATTR, interpreter.getName());
		element.setAttribute(ENVIRONMENT_ID, interpreter.getInstallLocation().getEnvironmentId());

		// Determine and set the 'path' attribute for the Interpreter
		String installPath = ""; //$NON-NLS-1$
		IFileHandle installLocation = interpreter.getRawInstallLocation();
		if (installLocation != null) {
			installPath = installLocation.getPath().toPortableString();
		}
		element.setAttribute(PATH_ATTR, installPath);

		// If the 'libraryLocations' attribute is specified, create a node for
		// it
		LibraryLocation[] libraryLocations = interpreter.getLibraryLocations();
		if (libraryLocations != null) {
			Element libLocationElement = libraryLocationsAsElement(doc, libraryLocations);
			element.appendChild(libLocationElement);
		}

		EnvironmentVariable[] environmentVariables = interpreter.getEnvironmentVariables();
		if (environmentVariables != null) {
			Element environmentVariableElement = environmentVariablesAsElement(doc, environmentVariables);
			element.appendChild(environmentVariableElement);
		}

		// ///////////////////////////////////////////////////////////////////////////////////
		// START HACK CUSTOM CODE
		// ///////////////////////////////////////////////////////////////////////////////////
		// TODO BUG_ECLIPSE 390358
		final String interpreterArgs = interpreter.getInterpreterArgs();
		if (interpreterArgs != null && interpreterArgs.length() > 0) {
			element.setAttribute(IARGS_ATTR, interpreterArgs);
		}
		// ///////////////////////////////////////////////////////////////////////////////////
		// END HACK CUSTOM CODE
		// ///////////////////////////////////////////////////////////////////////////////////

		if (interpreter instanceof IInterpreterInstallExtensionContainer) {
			String extensions = ((IInterpreterInstallExtensionContainer) interpreter).saveExtensions();
			if (extensions != null && extensions.length() != 0) {
				final Element extensionsElement = doc.createElement(EXTENSIONS_TAG);
				extensionsElement.appendChild(doc.createCDATASection(extensions));
				element.appendChild(extensionsElement);
			}
		}

		return element;
	}

	/**
	 * Create and return a 'libraryLocations' node. This node owns subordinate nodes that list individual library locations.
	 */
	private static Element libraryLocationsAsElement(Document doc, LibraryLocation[] locations) {
		Element root = doc.createElement(LIBRARY_LOCATIONS_TAG);
		for (int i = 0; i < locations.length; i++) {
			Element element = doc.createElement(LIBRARY_LOCATION_TAG);
			element.setAttribute(LIBRARY_PATH_ATTR, locations[i].getLibraryPath().toString());
			root.appendChild(element);
		}
		return root;
	}

	private static Element environmentVariablesAsElement(Document doc, EnvironmentVariable[] variables) {
		Element root = doc.createElement(ENVIRONMENT_VARIABLES_TAG);
		for (int i = 0; i < variables.length; i++) {
			Element element = doc.createElement(ENVIRONMENT_VARIABLE_TAG);
			element.setAttribute(VARIABLE_NAME_ATTR, variables[i].getName());
			element.setAttribute(VARIABLE_VALUE_ATTR, variables[i].getValue());
			root.appendChild(element);
		}
		return root;
	}
}
// CHECKSTYLE:ON