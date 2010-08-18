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
package com.firegnom.valkyrie.map;

import android.graphics.Bitmap;

import com.firegnom.valkyrie.util.ResourceLoader;

public class StringTileSet {
	private static final String TAG = "StringTileSet";
	public String imageName;
	public int tileWidth;
	public int tileHeight;
	public int maxX;
	public int maxY;
	public String name;
	/** The index of the tile set */
	public int index;
	/** The first global tile id in the set */
	public int firstGID;
	/** The local global tile id in the set */
	public int lastGID;
	private int imageWidth;
	private int imageHeight;

	/** The width of the tiles */

	public StringTileSet() {

	}

	public StringTileSet(int first, int last) {
		firstGID = first;
		lastGID = last;
	}

	public String getTileNamePng(int x, int y) {
		return imageName.replace(".png", ",") + x + "," + y + ".png";
	}

	public String getTileNamePng(int gid) throws IndexOutOfBoundsException {
		if (!contains(gid)) {
			throw new IndexOutOfBoundsException("Wrong position in tileset");
		}
		int id = gid - firstGID;
		int x = id % maxX;
		int y = id / maxX;
		return imageName.replace(".png", ",") + x + "," + y + ".png";
	}

	public StringTileSet(String imageName, int tileWidth, int tileHeight,
			int imageHeight, int imageWidth) {
		if (imageName == null) {
			throw new ExceptionInInitializerError("imageName cannot be null");
		}

		this.imageName = imageName;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		maxX = (imageWidth / tileWidth);
		maxY = (imageHeight / tileHeight);
	}

	/*
	 * TODO allocation problem maybe it will be nice to create String[][] array
	 * with already concanated filenames this way we will avoid string building
	 * in get bitmap function and unnecessary memory allocation this will take
	 * additional memory but might improve performance
	 */
	public Bitmap getBitmap(int x, int y, ResourceLoader rl)
			throws IndexOutOfBoundsException {
		if (x >= maxX || y >= maxY) {
			throw new IndexOutOfBoundsException("Wrong position (" + x + ","
					+ y + ") in tileset:" + imageName);
		}
		return rl.getBitmapResource(imageName + "," + x + "," + y + ".png",
				false);
	}

	public void downloadAll(ResourceLoader rl) {
		rl.downloadService(imageName, maxX, maxY);
	}

	public Bitmap getBitmap(int gid, ResourceLoader rl)
			throws IndexOutOfBoundsException {
		if (!contains(gid)) {
			throw new IndexOutOfBoundsException("Wrong gid (" + gid
					+ ") in tileset:" + imageName);
		}
		int id = gid - firstGID;
		int x = id % maxX;
		int y = id / maxX;
		return getBitmap(x, y, rl);
	}

	/**
	 * Check if this tileset contains a particular tile
	 * 
	 * @param gid
	 *            The global id to seach for
	 * @return True if the ID is contained in this tileset
	 */
	public boolean contains(int gid) {
		return (gid >= firstGID) && (gid <= lastGID);
	}

}
