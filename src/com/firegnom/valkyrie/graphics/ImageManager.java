/*******************************************************************************
 * Copyright (c) 2010 Maciej Kaniewski (mk@firegnom.com).
 * 
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 * 
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software Foundation,
 *    Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * 
 *    Contributors:
 *     Maciej Kaniewski (mk@firegnom.com) - initial API and implementation
 ******************************************************************************/
package com.firegnom.valkyrie.graphics;

import android.graphics.Bitmap;
import android.graphics.Paint;

import com.firegnom.valkyrie.service.IResourceLoaderService;
import com.firegnom.valkyrie.util.ResourceLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageManager.
 */
public class ImageManager extends Thread {
	
	/** The Constant instance. */
	private static final ImageManager instance = new ImageManager();
	
	/** The out of memory paint. */
	public Paint outOfMemoryPaint;

	/**
	 * Gets the single instance of ImageManager.
	 *
	 * @return single instance of ImageManager
	 */
	public static ImageManager getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new image manager.
	 */
	private ImageManager() {
		outOfMemoryPaint = new Paint();
		outOfMemoryPaint.setAntiAlias(true);
		outOfMemoryPaint.setDither(false);
		outOfMemoryPaint.setColor(0x80ff8f00);
		outOfMemoryPaint.setStyle(Paint.Style.FILL);
	}

	// //this will take care of cleaning unused images based on their ttl and
	// last usage time
	// public void run() {
	//
	//
	// };

	/** The service. */
	IResourceLoaderService service;
	
	/** The rl. */
	ResourceLoader rl;

	/**
	 * Connect service.
	 *
	 * @param service the service
	 */
	public void connectService(IResourceLoaderService service) {
		this.service = service;
		rl = new ResourceLoader(service);
	}

	/**
	 * Disconnect service.
	 */
	public void disconnectService() {
		this.service = null;
		rl.freeService();
		// ResourceLoader.emptyBitmapCache();
		rl = null;
	}

	/**
	 * Gets the bitmap.
	 *
	 * @param name the name
	 * @return the bitmap
	 */
	Bitmap getBitmap(String name) {
		return rl.getBitmapResource(name, false);
	}

	/**
	 * Sets the resource loader.
	 *
	 * @param rl2 the new resource loader
	 */
	public void setResourceLoader(ResourceLoader rl2) {
		this.rl = rl2;
	}

}
