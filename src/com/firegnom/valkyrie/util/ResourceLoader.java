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
package com.firegnom.valkyrie.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.util.Log;

import com.firegnom.valkyrie.engine.ValkyrieRuntimeException;
import com.firegnom.valkyrie.net.AsyncDownload;
import com.firegnom.valkyrie.service.ILoaderCallback;
import com.firegnom.valkyrie.service.IResourceLoaderService;

// TODO: Auto-generated Javadoc
/**
 * The Class ResourceLoader.
 */
public class ResourceLoader {
	// public static final String SD_CACHE_FOLDER =
	// "/sdcard/com.firegnom.valkyrie/cache/";
	// TODO test maybe will be faster
	/** The Constant SD_CACHE_FOLDER. */
	public static final String SD_CACHE_FOLDER = "/data/data/com.firegnom.valkyrie/cache/";
	
	/** The Constant DATA_CACHE_FOLDER. */
	public static final String DATA_CACHE_FOLDER = "/data/data/com.firegnom.valkyrie/cache/";
	
	/** The Constant URL. */
	public static final String URL = "http://valkyrie.firegnom.com/data/";
	
	/** The Constant TAG. */
	private static final String TAG = ResourceLoader.class.getName();
	
	/** The Constant SERVICE_TIMEOUT. */
	private static final int SERVICE_TIMEOUT = 200;

	/** The bitmap cache. */
	public static HashMap<String, Bitmap> bitmapCache = new HashMap<String, Bitmap>();

	/** The cache location. */
	public static String cacheLocation = null;
	
	/** The m service. */
	IResourceLoaderService mService;

	/**
	 * Free service.
	 */
	public void freeService() {
		mService = null;
	}

	/**
	 * Instantiates a new resource loader.
	 *
	 * @param mService the m service
	 */
	public ResourceLoader(IResourceLoaderService mService) {
		Log.d(TAG, "ResourceLoader - create");

		this.mService = mService;
		initCacheLocation();
		// emptyCache();
	}

	/**
	 * Instantiates a new resource loader.
	 */
	public ResourceLoader() {
		initCacheLocation();
	}

	/**
	 * Inits the cache location.
	 */
	private static void initCacheLocation() {
		File cache = new File(SD_CACHE_FOLDER);
		if (cache.exists()) {
			Log.d(TAG, "Using sd card as a cache dir");
			cacheLocation = SD_CACHE_FOLDER;
			return;
		}
		if (cache.mkdirs()) {
			Log.d(TAG, "Using sd card as a cache dir");
			cacheLocation = SD_CACHE_FOLDER;
			return;
		}
		/*
		 * now we can't create data on sdcard so let's use data directory (still
		 * more space on sd so it is better to use sd but in the future maby it
		 * will be better to use data directory)
		 */
		cache = new File(DATA_CACHE_FOLDER);
		if (cache.exists()) {
			Log.d(TAG, "Using data dir  as a cache dir");
			cacheLocation = DATA_CACHE_FOLDER;
			return;
		}
		if (cache.mkdirs()) {
			Log.d(TAG, "Using data dir  as a cache dir");
			cacheLocation = DATA_CACHE_FOLDER;
			return;
		}

	}

	/**
	 * Gets the resource as stream.
	 *
	 * @param ref the ref
	 * @param download the download
	 * @return the resource as stream
	 */
	public InputStream getResourceAsStream(String ref, boolean download) {
		InputStream in = getResourceFromCache(ref);
		if (in == null && download) {
			downloadService(ref);
			in = getResourceFromCache(ref);
		}
		return in;
	}

	/**
	 * Gets the resource as stream download.
	 *
	 * @param ref the ref
	 * @return the resource as stream download
	 */
	public InputStream getResourceAsStreamDownload(String ref) {
		deleteFile(ref);
		downloadService(ref);
		return getResourceFromCache(ref);
	}

	/**
	 * Delete file.
	 *
	 * @param ref the ref
	 */
	private void deleteFile(String ref) {
		new File(cacheLocation + ref).delete();

	}

	/** The lastloadedref. */
	String lastloadedref = "";
	
	/** The last loadedimg. */
	Bitmap lastLoadedimg = null;

	/**
	 * Gets the bitmap resource.
	 *
	 * @param ref the ref
	 * @param waitForDownload the wait for download
	 * @return the bitmap resource
	 */
	public Bitmap getBitmapResource(String ref, boolean waitForDownload) {
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 10;
		// options.inPurgeable = true;
		// options.inInputShareable = true;
		// options.inJustDecodeBounds
		//
		// if (!waitForDownload)return null;
		// ZipFile zip = null;;
		// try {
		// zip = new ZipFile("/sdcard/test.zip");
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// Bitmap ret = bitmapCache.get(ref);
		// Bitmap copy = null;
		// if (ret == null) {
		// try {
		// copy =
		// BitmapFactory.decodeStream(zip.getInputStream(zip.getEntry(ref)));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// ret = copy.copy(Config.ARGB_4444, false);
		// copy.recycle();
		// bitmapCache.put(ref, ret);
		// }
		// return ret;

		//
		Bitmap ret = bitmapCache.get(ref);
		Bitmap copy;

		if (ret == null) {
			try {
				copy = BitmapFactory.decodeStream(getResourceAsStream(ref,
						waitForDownload));
				if (copy == null) {
					addToDownloadQueue(ref);
					return null;
				}
				ret = copy.copy(Config.ARGB_4444, false);
				copy.recycle();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				ResourceLoader.emptyBitmapCache();
				System.gc();
			}
			if (ret == null) {
				try {
					copy = BitmapFactory.decodeStream(getResourceAsStream(ref,
							waitForDownload));
					if (copy == null) {
						addToDownloadQueue(ref);
						return null;
					}
					ret = copy.copy(Config.ARGB_4444, false);
					copy.recycle();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
			// if (ret.hasAlpha())
			// bitmap = Bitmap.createBitmap(ret.getWidth(), ret.getHeight(),
			// Config.ARGB_4444);
			// Canvas canvas = new Canvas(bitmap);
			// //ret.setBounds(0, 0, width, height);
			// canvas.drawBitmap(ret, 0,0, null);
			// ret = bitmap;
			bitmapCache.put(ref, ret);
		}
		return ret;
	}

	/**
	 * Empty bitmap cache.
	 */
	public static void emptyBitmapCache() {
		for (Entry<String, Bitmap> s : bitmapCache.entrySet()) {
			s.getValue().recycle();
		}
		bitmapCache = new HashMap<String, Bitmap>();
		System.gc();
	}

	/**
	 * Gets the resource from cache.
	 *
	 * @param ref the ref
	 * @return the resource from cache
	 */
	private InputStream getResourceFromCache(String ref) {
		try {
			File file = new File(cacheLocation, ref);
			if (!file.exists()) {
				return null;
			}
			return new FileInputStream(file);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Empty cache.
	 */
	public void emptyCache() {
		File dir = new File(cacheLocation);
		deleteDir(dir);
		initCacheLocation();

	}

	/**
	 * Delete dir.
	 *
	 * @param dir the dir
	 * @return true, if successful
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	/**
	 * Adds the to download queue.
	 *
	 * @param ref the ref
	 */
	public void addToDownloadQueue(String ref) {
		if (mService == null) {
			return;
		}
		try {
			mService.addToDownloadQueue(ref);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Download service.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean downloadService(String name) {
		int time = 0;
		answer = false;
		failed = false;
		while (mService == null) {
			if (time >= SERVICE_TIMEOUT) {
				throw new ValkyrieRuntimeException("Service timeout");
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time++;
		}
		try {
			mService.download(name, callback);
		} catch (RemoteException e1) {
			answer = false;
			e1.printStackTrace();
			return false;
		}

		while (!answer) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return !failed;

	}

	/** The answer. */
	boolean answer = false;
	
	/** The failed. */
	boolean failed = false;
	
	/** The callback. */
	ILoaderCallback.Stub callback = new ILoaderCallback.Stub() {
		@Override
		public void loadFailed() throws RemoteException {
			Log.d(TAG, "loadFailed");
			failed = true;

		}

		@Override
		public void loadComplete() throws RemoteException {
			Log.d(TAG, "loadComplete");
			answer = true;
		}
	};

	/**
	 * Download.
	 *
	 * @param name the name
	 * @param o the o
	 * @return true, if successful
	 */
	public boolean download(String name, Observer o) {
		InputStream in = getResourceFromCache(name);
		if (in == null) {
			Log.d(TAG, "url: " + URL + name);
			try {
				new AsyncDownload(new URL(URL + name), cacheLocation, o);
			} catch (MalformedURLException e) {
				throw new ValkyrieRuntimeException(e);
			}
			return false;
		}
		return true;
	}

	/**
	 * Download service.
	 *
	 * @param imageName the image name
	 * @param maxX the max x
	 * @param maxY the max y
	 */
	public void downloadService(String imageName, int maxX, int maxY) {
		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				downloadService(imageName + "," + x + "," + y + ".png");
			}
		}
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public static String getPath() {
		initCacheLocation();
		return cacheLocation;
	}

}
