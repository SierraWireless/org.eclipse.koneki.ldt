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

package org.eclipse.koneki.ldt.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.koneki.ldt.editor.internal.text.LuaTextTools;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.eclipse.koneki.ldt.editor";//$NON-NLS-1$

	/** The shared instance */
	private static Activator plugin;

	private LuaTextTools fLuaTextTools;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public synchronized LuaTextTools getTextTools() {
		if (fLuaTextTools == null) {
			fLuaTextTools = new LuaTextTools(true);
		}
		return fLuaTextTools;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String pluginId, String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, path);
	}

	/**
	 * Add an image to the image registry
	 * 
	 * @param registry
	 *            the registry to use
	 * @param imgPath
	 *            the path of the image to add
	 */
	public static void addImageToRegistry(ImageRegistry registry, String imgPath) {
		ImageDescriptor descriptor = getImageDescriptor(PLUGIN_ID, imgPath);
		addImageToRegistry(registry, imgPath, descriptor);
	}

	/**
	 * Register in the given ImageRegistry the ImageDescriptor using the Image's path as key.
	 * 
	 * @param registry
	 *            ImageRegistry
	 * @param imgPath
	 *            String
	 * @param imgDesc
	 *            ImageDescriptor
	 */
	public static void addImageToRegistry(ImageRegistry registry, String imgPath, ImageDescriptor imgDesc) {
		ImageRegistry imgRegistry = registry;
		if (imgRegistry == null) {
			imgRegistry = getDefault().getImageRegistry();
		}

		imgRegistry.put(imgPath, imgDesc);
	}

	/**
	 * Get an image from the local ImageRegistry. If the given Image's path is not already registered, do it.
	 * 
	 * @param imagePath
	 *            String, path and key identifying the image in the ImageRegistry
	 * 
	 * @return Image or null if nothing corresponds to the given key
	 */
	public static Image getImage(String imagePath) {
		Image result = getDefault().getImageRegistry().get(imagePath);

		if (result == null) {
			addImageToRegistry(getDefault().getImageRegistry(), imagePath);
			result = getDefault().getImageRegistry().get(imagePath);
		}

		return result;
	}

	/**
	 * Log a error message caused by the given exception
	 * 
	 * @param message
	 *            message to log
	 * @param throwable
	 *            exception which causes the error
	 */
	public static void logError(final String message, final Throwable throwable) {
		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, message, throwable);
		getDefault().getLog().log(status);
	}

	/**
	 * Log a simple warning message
	 * 
	 * @param message
	 *            message to log
	 */
	public static void logWarning(final String message) {
		IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, message);
		getDefault().getLog().log(status);
	}

	/**
	 * Log a warning message caused by the given exception
	 * 
	 * @param message
	 *            message to log
	 * @param throwable
	 *            exception which causes the warning
	 */
	public static void logWarning(final String message, final Throwable throwable) {
		IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, message, throwable);
		getDefault().getLog().log(status);
	}

	/**
	 * Log the given status
	 * 
	 * @param status
	 *            status to log
	 */
	public static void log(final IStatus status) {
		getDefault().getLog().log(status);
	}

}
