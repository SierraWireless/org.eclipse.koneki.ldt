/*******************************************************************************
 * Copyright (c) 2013 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.support.lua52.internal.interpreter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public final class TransportLayerModule {

	private TransportLayerModule() {
	}

	/**
	 * register this java module in the given lua vm
	 */
	public static final void registerModelFactory(LuaState l) {
		NamedJavaFunction[] namedJavaFunctions = createFunctions();
		l.register("debugger.transport.javasocket", namedJavaFunctions, false); //$NON-NLS-1$
		l.pop(1);
	}

	/* create all factory function which will be available in javamodelfactory module */
	private static NamedJavaFunction[] createFunctions() {
		List<NamedJavaFunction> javaFunctions = new ArrayList<NamedJavaFunction>();

		javaFunctions.add(create());
		javaFunctions.add(sleep());
		javaFunctions.add(rawb64());
		javaFunctions.add(b64());
		javaFunctions.add(unb64());

		return javaFunctions.toArray(new NamedJavaFunction[javaFunctions.size()]);
	}

	public static class SocketWrapper {

		private final Socket socket;

		public SocketWrapper() {
			socket = new Socket();
		}

		public void connect(String host, int port) throws IOException {
			socket.connect(new InetSocketAddress(host, port));
		}

		public Object receive() throws IOException {
			byte readByte = (byte) socket.getInputStream().read();
			if (readByte == 0)
				return ""; //$NON-NLS-1$
			if (readByte == -1)
				return null;
			return new String(new byte[] { readByte });
		}

		public void send(String data) throws IOException {
			if (data.isEmpty())
				socket.getOutputStream().write(0);
			else
				socket.getOutputStream().write(data.getBytes());
		}

		public void close() throws IOException {
			socket.close();
		}

		public void settimeout(Integer sec) throws SocketException {
			if (sec == null) {
				socket.setSoTimeout(0);
			} else if (sec == 0) {
				socket.setSoTimeout(1);
			} else {
				socket.setSoTimeout(sec * 1000);
			}

		}
	}

	private static NamedJavaFunction create() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				SocketWrapper socketWrapper = new SocketWrapper();
				l.pushJavaObject(socketWrapper);
				return 1;
			}

			@Override
			public String getName() {
				return "create"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction sleep() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				// time amount of time to wait in seconds (decimal numbers
				// allowed).
				double timeInSeconds = l.checkInteger(1);
				int timeInMillis = (int) (timeInSeconds * 1000);

				try {
					Thread.sleep(timeInMillis);
				} catch (InterruptedException e) {
					l.pushNil();
					l.pushString(e.getMessage());
					return 2;
				}
				return 0;
			}

			@Override
			public String getName() {
				return "sleep"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction rawb64() {
		return new NamedJavaFunction() {

			@Override
			public int invoke(LuaState l) {
				String string = l.checkString(1);
				byte[] resultb64 = Base64.encodeBase64(string.getBytes());
				String b64String = new String(resultb64);
				l.pushString(b64String);
				return 1;
			}

			@Override
			public String getName() {
				return "rawb64"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction b64() {
		return new NamedJavaFunction() {

			@Override
			public int invoke(LuaState l) {
				String string = l.checkString(1);
				byte[] b64Result = Base64.encodeBase64Chunked(string.getBytes());
				String b64String = new String(b64Result);
				l.pushString(b64String);
				return 1;
			}

			@Override
			public String getName() {
				return "b64"; //$NON-NLS-1$
			}
		};
	}

	private static NamedJavaFunction unb64() {
		return new NamedJavaFunction() {
			@Override
			public int invoke(LuaState l) {
				String b64String = l.checkString(1);
				byte[] result = Base64.decodeBase64(b64String.getBytes());
				String string = new String(result);
				l.pushString(string);
				return 1;
			}

			@Override
			public String getName() {
				return "unb64"; //$NON-NLS-1$
			}
		};
	}
}