/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.debug.core.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dltk.dbgp.DbgpBaseCommands;
import org.eclipse.dltk.dbgp.DbgpRequest;
import org.eclipse.dltk.dbgp.IDbgpCommunicator;
import org.eclipse.dltk.dbgp.IDbgpFeature;
import org.eclipse.dltk.dbgp.IDbgpNotificationManager;
import org.eclipse.dltk.dbgp.IDbgpProperty;
import org.eclipse.dltk.dbgp.IDbgpRawListener;
import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.dbgp.IDbgpSessionInfo;
import org.eclipse.dltk.dbgp.IDbgpStackLevel;
import org.eclipse.dltk.dbgp.IDbgpStatus;
import org.eclipse.dltk.dbgp.breakpoints.DbgpBreakpointConfig;
import org.eclipse.dltk.dbgp.breakpoints.IDbgpBreakpoint;
import org.eclipse.dltk.dbgp.commands.IDbgpContextCommands;
import org.eclipse.dltk.dbgp.commands.IDbgpCoreCommands;
import org.eclipse.dltk.dbgp.commands.IDbgpExtendedCommands;
import org.eclipse.dltk.dbgp.commands.IDbgpPropertyCommands;
import org.eclipse.dltk.dbgp.commands.IDbgpStackCommands;
import org.eclipse.dltk.dbgp.exceptions.DbgpDebuggingEngineException;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.dbgp.internal.IDbgpTerminationListener;
import org.eclipse.dltk.dbgp.internal.managers.IDbgpStreamManager;
import org.eclipse.dltk.dbgp.internal.utils.DbgpXmlEntityParser;
import org.eclipse.dltk.dbgp.internal.utils.DbgpXmlParser;
import org.eclipse.dltk.debug.core.IDebugOptions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a proxy class for Lua coroutines which automatically binds the coroutine ID on necessary commands.
 */
@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public class LuaCoroutineDbgpSession implements IDbgpSession {
	private static final String COROUTINE_FLAG = "-o"; //$NON-NLS-1$

	private IDbgpSession actualSession;
	private LuaCoroutine coroutine;
	private IDbgpCoreCommands proxyCoreCommands;

	private class CoroutineStackCommands extends DbgpBaseCommands implements IDbgpStackCommands {
		private static final String STACK_DEPTH_COMMAND = "stack_depth"; //$NON-NLS-1$
		private static final String STACK_GET_COMMAND = "stack_get"; //$NON-NLS-1$
		private static final String TAG_STACK = "stack"; //$NON-NLS-1$
		private static final String ATTR_DEPTH = "depth"; //$NON-NLS-1$

		private final Comparator levelComparator = new Comparator() {

			public int compare(Object o1, Object o2) {
				final IDbgpStackLevel level1 = (IDbgpStackLevel) o1;
				final IDbgpStackLevel level2 = (IDbgpStackLevel) o2;
				return level1.getLevel() - level2.getLevel();
			}

		};

		/**
		 * @param communicator
		 */
		public CoroutineStackCommands(IDbgpCommunicator communicator) {
			super(communicator);
		}

		protected int parseStackDepthResponse(Element response) throws DbgpDebuggingEngineException {
			return Integer.parseInt(response.getAttribute(ATTR_DEPTH));
		}

		protected IDbgpStackLevel[] parseStackLevels(Element response) throws DbgpException {
			NodeList nodes = response.getElementsByTagName(TAG_STACK);
			IDbgpStackLevel[] list = new IDbgpStackLevel[nodes.getLength()];
			for (int i = 0; i < nodes.getLength(); ++i) {
				final Element level = (Element) nodes.item(i);
				list[i] = DbgpXmlEntityParser.parseStackLevel(level);
			}
			Arrays.sort(list, levelComparator);
			return list;
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpStackCommands#getStackDepth()
		 */
		@Override
		public int getStackDepth() throws DbgpException {
			DbgpRequest request = createRequest(STACK_DEPTH_COMMAND);
			request.addOption(COROUTINE_FLAG, coroutine.getCoroutineId());
			return parseStackDepthResponse(communicate(request));
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpStackCommands#getStackLevels()
		 */
		@Override
		public IDbgpStackLevel[] getStackLevels() throws DbgpException {
			DbgpRequest request = createRequest(STACK_GET_COMMAND);
			request.addOption(COROUTINE_FLAG, coroutine.getCoroutineId());
			return parseStackLevels(communicate(request));
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpStackCommands#getStackLevel(int)
		 */
		@Override
		public IDbgpStackLevel getStackLevel(int stackDepth) throws DbgpException {
			DbgpRequest request = createRequest(STACK_GET_COMMAND);
			request.addOption("-d", stackDepth); //$NON-NLS-1$
			request.addOption(COROUTINE_FLAG, coroutine.getCoroutineId());
			IDbgpStackLevel[] levels = parseStackLevels(communicate(request));
			return levels.length == 1 ? levels[0] : null;
		}
	}

	private class CoroutineContextCommands extends DbgpBaseCommands implements IDbgpContextCommands {
		private static final String CONTEXT_NAMES_COMMAND = "context_names"; //$NON-NLS-1$
		private static final String CONTEXT_GET = "context_get"; //$NON-NLS-1$
		private static final String TAG_CONTEXT = "context"; //$NON-NLS-1$
		private static final String ATTR_NAME = "name"; //$NON-NLS-1$
		private static final String ATTR_ID = "id"; //$NON-NLS-1$

		public CoroutineContextCommands(IDbgpCommunicator communicator) {
			super(communicator);
			// TODO Auto-generated constructor stub
		}

		protected Map parseContextNamesResponse(Element response) throws DbgpException {
			Map map = new HashMap();

			NodeList contexts = response.getElementsByTagName(TAG_CONTEXT);
			for (int i = 0; i < contexts.getLength(); ++i) {
				Element context = (Element) contexts.item(i);
				String name = context.getAttribute(ATTR_NAME);
				Integer id = new Integer(context.getAttribute(ATTR_ID));
				map.put(id, name);
			}

			return map;
		}

		protected IDbgpProperty[] parseContextPropertiesResponse(Element response) throws DbgpException {
			NodeList properties = response.getChildNodes();

			List list = new ArrayList();
			for (int i = 0; i < properties.getLength(); ++i) {

				Node item = properties.item(i);
				if (item instanceof Element) {
					if (item.getNodeName().equals(DbgpXmlEntityParser.TAG_PROPERTY)) {
						list.add(DbgpXmlEntityParser.parseProperty((Element) item));
					}
				}
			}

			return (IDbgpProperty[]) list.toArray(new IDbgpProperty[list.size()]);
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpContextCommands#getContextNames(int)
		 */
		@Override
		public Map getContextNames(int stackDepth) throws DbgpException {
			DbgpRequest request = createRequest(CONTEXT_NAMES_COMMAND);
			request.addOption(COROUTINE_FLAG, coroutine.getCoroutineId());
			request.addOption("-d", stackDepth); //$NON-NLS-1$
			return parseContextNamesResponse(communicate(request));
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpContextCommands#getContextProperties(int)
		 */
		@Override
		public IDbgpProperty[] getContextProperties(int stackDepth) throws DbgpException {
			DbgpRequest request = createRequest(CONTEXT_GET);
			request.addOption(COROUTINE_FLAG, coroutine.getCoroutineId());
			request.addOption("-d", stackDepth); //$NON-NLS-1$
			return parseContextPropertiesResponse(communicate(request));
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpContextCommands#getContextProperties(int, int)
		 */
		@Override
		public IDbgpProperty[] getContextProperties(int stackDepth, int contextId) throws DbgpException {
			DbgpRequest request = createRequest(CONTEXT_GET);
			request.addOption(COROUTINE_FLAG, coroutine.getCoroutineId());
			request.addOption("-d", stackDepth); //$NON-NLS-1$
			request.addOption("-c", contextId); //$NON-NLS-1$
			return parseContextPropertiesResponse(communicate(request));
		}
	}

	private class CoroutinePropertyCommands extends DbgpBaseCommands implements IDbgpPropertyCommands {
		private static final String PROPERTY_GET_COMMAND = "property_get"; //$NON-NLS-1$
		private static final String PROPERTY_SET_COMMAND = "property_set"; //$NON-NLS-1$

		/**
		 * @param communicator
		 */
		public CoroutinePropertyCommands(IDbgpCommunicator communicator) {
			super(communicator);
		}

		protected IDbgpProperty parsePropertyResponse(Element response) throws DbgpException {
			// TODO: check length!!!
			NodeList properties = response.getElementsByTagName(DbgpXmlEntityParser.TAG_PROPERTY);
			return DbgpXmlEntityParser.parseProperty((Element) properties.item(0));
		}

		protected IDbgpProperty getProperty(Integer page, String name, Integer stackDepth, Integer contextId) throws DbgpException {
			DbgpRequest request = createRequest(PROPERTY_GET_COMMAND);
			request.addOption(COROUTINE_FLAG, coroutine.getCoroutineId());
			request.addOption("-n", name); //$NON-NLS-1$

			if (stackDepth != null) {
				request.addOption("-d", stackDepth); //$NON-NLS-1$
			}

			if (contextId != null) {
				request.addOption("-c", contextId); //$NON-NLS-1$
			}

			if (page != null) {
				request.addOption("-p", page); //$NON-NLS-1$
			}
			return parsePropertyResponse(communicate(request));
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpPropertyCommands#getPropertyByKey(java.lang.String, java.lang.String)
		 */
		@Override
		public IDbgpProperty getPropertyByKey(String name, String key) throws DbgpException {
			DbgpRequest request = createRequest(PROPERTY_GET_COMMAND);
			request.addOption("-n", name); //$NON-NLS-1$
			request.addOption("-k", key); //$NON-NLS-1$
			return parsePropertyResponse(communicate(request));
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpPropertyCommands#getProperty(java.lang.String)
		 */
		@Override
		public IDbgpProperty getProperty(String name) throws DbgpException {
			return getProperty(null, name, null, null);
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpPropertyCommands#getProperty(java.lang.String, int)
		 */
		@Override
		public IDbgpProperty getProperty(String name, int stackDepth) throws DbgpException {
			return getProperty(null, name, stackDepth, null);
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpPropertyCommands#getProperty(java.lang.String, int, int)
		 */
		@Override
		public IDbgpProperty getProperty(String name, int stackDepth, int contextId) throws DbgpException {
			return getProperty(name, stackDepth, contextId);
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpPropertyCommands#getProperty(int, java.lang.String, int)
		 */
		@Override
		public IDbgpProperty getProperty(int page, String name, int stackDepth) throws DbgpException {
			return getProperty(page, name, stackDepth, null);
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpPropertyCommands#setProperty(org.eclipse.dltk.dbgp.IDbgpProperty)
		 */
		@Override
		public boolean setProperty(IDbgpProperty property) throws DbgpException {
			DbgpRequest request = createRequest(PROPERTY_SET_COMMAND);
			request.addOption("-n", property.getName()); //$NON-NLS-1$
			request.addOption(COROUTINE_FLAG, coroutine.getCoroutineId());
			request.setData(property.getValue());
			return DbgpXmlParser.parseSuccess(communicate(request));
		}

		/**
		 * @see org.eclipse.dltk.dbgp.commands.IDbgpPropertyCommands#setProperty(java.lang.String, int, java.lang.String)
		 */
		@Override
		public boolean setProperty(String name, int stackDepth, String value) throws DbgpException {
			DbgpRequest request = createRequest(PROPERTY_SET_COMMAND);
			request.addOption("-n", name); //$NON-NLS-1$
			request.addOption(COROUTINE_FLAG, coroutine.getCoroutineId());
			request.addOption("-d", stackDepth); //$NON-NLS-1$
			request.setData(value);
			return DbgpXmlParser.parseSuccess(communicate(request));
		}
	}

	private class CoroutineCoreCommands implements IDbgpCoreCommands {
		private IDbgpCoreCommands actualCoreCommands;
		private IDbgpStackCommands proxiedStackCommands;
		private IDbgpContextCommands proxiedContextCommands;
		private IDbgpPropertyCommands proxiedPropertyCommands;

		/**
		 * @param actualCoreCommands
		 */
		public CoroutineCoreCommands() {
			super();
			this.actualCoreCommands = actualSession.getCoreCommands();
			this.proxiedStackCommands = new CoroutineStackCommands(getCommunicator());
			this.proxiedContextCommands = new CoroutineContextCommands(getCommunicator());
			this.proxiedPropertyCommands = new CoroutinePropertyCommands(getCommunicator());
		}

		public String getSource(URI uri) throws DbgpException {
			return actualCoreCommands.getSource(uri);
		}

		public IDbgpStatus getStatus() throws DbgpException {
			return actualCoreCommands.getStatus();
		}

		public IDbgpStatus run() throws DbgpException {
			return actualCoreCommands.run();
		}

		public String getSource(URI uri, int beginLine) throws DbgpException {
			return actualCoreCommands.getSource(uri, beginLine);
		}

		public boolean configureStdout(int value) throws DbgpException {
			return actualCoreCommands.configureStdout(value);
		}

		public boolean configureStderr(int value) throws DbgpException {
			return actualCoreCommands.configureStderr(value);
		}

		public String getSource(URI uri, int beginLine, int endLine) throws DbgpException {
			return actualCoreCommands.getSource(uri, beginLine, endLine);
		}

		public String setLineBreakpoint(URI uri, int lineNumber, DbgpBreakpointConfig config) throws DbgpException {
			return actualCoreCommands.setLineBreakpoint(uri, lineNumber, config);
		}

		public IDbgpStatus stepInto() throws DbgpException {
			return actualCoreCommands.stepInto();
		}

		public Map getTypeMap() throws DbgpException {
			return actualCoreCommands.getTypeMap();
		}

		public String setCallBreakpoint(URI uri, String function, DbgpBreakpointConfig config) throws DbgpException {
			return actualCoreCommands.setCallBreakpoint(uri, function, config);
		}

		public IDbgpStatus stepOver() throws DbgpException {
			return actualCoreCommands.stepOver();
		}

		public String setReturnBreakpoint(URI uri, String function, DbgpBreakpointConfig config) throws DbgpException {
			return actualCoreCommands.setReturnBreakpoint(uri, function, config);
		}

		public String setExceptionBreakpoint(String exception, DbgpBreakpointConfig config) throws DbgpException {
			return actualCoreCommands.setExceptionBreakpoint(exception, config);
		}

		public String setConditionalBreakpoint(URI uri, int lineNumber, DbgpBreakpointConfig config) throws DbgpException {
			return actualCoreCommands.setConditionalBreakpoint(uri, lineNumber, config);
		}

		public IDbgpStatus stepOut() throws DbgpException {
			return actualCoreCommands.stepOut();
		}

		public String setConditionalBreakpoint(URI uri, DbgpBreakpointConfig config) throws DbgpException {
			return actualCoreCommands.setConditionalBreakpoint(uri, config);
		}

		public IDbgpStatus stop() throws DbgpException {
			return actualCoreCommands.stop();
		}

		public String setWatchBreakpoint(URI uri, int line, DbgpBreakpointConfig config) throws DbgpException {
			return actualCoreCommands.setWatchBreakpoint(uri, line, config);
		}

		public void removeBreakpoint(String id) throws DbgpException {
			actualCoreCommands.removeBreakpoint(id);
		}

		public void updateBreakpoint(String id, DbgpBreakpointConfig config) throws DbgpException {
			actualCoreCommands.updateBreakpoint(id, config);
		}

		public IDbgpBreakpoint getBreakpoint(String id) throws DbgpException {
			return actualCoreCommands.getBreakpoint(id);
		}

		public IDbgpStatus detach() throws DbgpException {
			return actualCoreCommands.detach();
		}

		public IDbgpBreakpoint[] getBreakpoints() throws DbgpException {
			return actualCoreCommands.getBreakpoints();
		}

		public IDbgpFeature getFeature(String featureName) throws DbgpException {
			return actualCoreCommands.getFeature(featureName);
		}

		public boolean setFeature(String featureName, String featureValue) throws DbgpException {
			return actualCoreCommands.setFeature(featureName, featureValue);
		}

		public int getStackDepth() throws DbgpException {
			return proxiedStackCommands.getStackDepth();
		}

		public IDbgpStackLevel[] getStackLevels() throws DbgpException {
			return proxiedStackCommands.getStackLevels();
		}

		public IDbgpStackLevel getStackLevel(int stackDepth) throws DbgpException {
			return proxiedStackCommands.getStackLevel(stackDepth);
		}

		public Map getContextNames(int stackDepth) throws DbgpException {
			return proxiedContextCommands.getContextNames(stackDepth);
		}

		public IDbgpProperty[] getContextProperties(int stackDepth) throws DbgpException {
			return proxiedContextCommands.getContextProperties(stackDepth);
		}

		public IDbgpProperty[] getContextProperties(int stackDepth, int contextId) throws DbgpException {
			return proxiedContextCommands.getContextProperties(stackDepth, contextId);
		}

		public IDbgpProperty getPropertyByKey(String name, String key) throws DbgpException {
			return proxiedPropertyCommands.getPropertyByKey(name, key);
		}

		public IDbgpProperty getProperty(String name) throws DbgpException {
			return proxiedPropertyCommands.getProperty(name);
		}

		public IDbgpProperty getProperty(String name, int stackDepth) throws DbgpException {
			return proxiedPropertyCommands.getProperty(name, stackDepth);
		}

		public IDbgpProperty getProperty(String name, int stackDepth, int contextId) throws DbgpException {
			return proxiedPropertyCommands.getProperty(name, stackDepth, contextId);
		}

		public IDbgpProperty getProperty(int page, String name, int stackDepth) throws DbgpException {
			return proxiedPropertyCommands.getProperty(page, name, stackDepth);
		}

		public boolean setProperty(IDbgpProperty property) throws DbgpException {
			return proxiedPropertyCommands.setProperty(property);
		}

		public boolean setProperty(String name, int stackDepth, String value) throws DbgpException {
			return proxiedPropertyCommands.setProperty(name, stackDepth, value);
		}

	}

	/**
	 * @param actualSession
	 * @param coroutine
	 */
	public LuaCoroutineDbgpSession(IDbgpSession actualSession, LuaCoroutine coroutine) {
		super();
		this.actualSession = actualSession;
		this.coroutine = coroutine;
		this.proxyCoreCommands = new CoroutineCoreCommands();
	}

	public void bindToCoroutine(LuaCoroutine newCoroutine) {
		this.coroutine = newCoroutine;
	}

	public IDbgpCoreCommands getCoreCommands() {
		return proxyCoreCommands;
	}

	// ***** PROXY METHODS *****

	public void addTerminationListener(IDbgpTerminationListener listener) {
		actualSession.addTerminationListener(listener);
	}

	public IDbgpExtendedCommands getExtendedCommands() {
		return actualSession.getExtendedCommands();
	}

	public void removeTerminationListener(IDbgpTerminationListener listener) {
		actualSession.removeTerminationListener(listener);
	}

	public Object get(Class type) {
		return actualSession.get(type);
	}

	public IDebugOptions getDebugOptions() {
		return actualSession.getDebugOptions();
	}

	public void configure(IDebugOptions debugOptions) {
		actualSession.configure(debugOptions);
	}

	public void requestTermination() {
		actualSession.requestTermination();
	}

	public void waitTerminated() throws InterruptedException {
		actualSession.waitTerminated();
	}

	public IDbgpSessionInfo getInfo() {
		return actualSession.getInfo();
	}

	public IDbgpStreamManager getStreamManager() {
		return actualSession.getStreamManager();
	}

	public IDbgpNotificationManager getNotificationManager() {
		return actualSession.getNotificationManager();
	}

	public void addRawListener(IDbgpRawListener listener) {
		actualSession.addRawListener(listener);
	}

	public void removeRawListenr(IDbgpRawListener listener) {
		actualSession.removeRawListenr(listener);
	}

	public IDbgpCommunicator getCommunicator() {
		return actualSession.getCommunicator();
	}

}
