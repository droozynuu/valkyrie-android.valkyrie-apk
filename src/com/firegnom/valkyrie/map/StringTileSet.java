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

// TODO: Auto-generated Javadoc
/**
 * The Class StringTileSet.
 */
public class StringTileSet {
	
	/** The Constant TAG. */
	private static final String TAG = "StringTileSet";
	
	/** The image name. */
	public String imageName;
	
	/** The tile width. */
	public int tileWidth;
	
	/** The tile height. */
	public int tileHeight;
	
	/** The max x. */
	public int maxX;
	
	/** The max y. */
	public int maxY;
	
	/** The name. */
	public String name;
	
	/** The index of the tile set. */
	public int index;
	
	/** The first global tile id in the set. */
	public int firstGID;
	
	/** The local global tile id in the set. */
	public int lastGID;
	
	/** The image width. */
	private int imageWidth;
	
	/** The image height. */
	private int imageHeight;

	/**
	 * The width of the tiles.
	 */

	public StringTileSet() {

	}

	/**
	 * Instantiates a new string tile set.
	 *
	 * @param first the first
	 * @param last the last
	 */
	public StringTileSet(final int first, final int last) {
		firstGID = first;
		lastGID = last;
	}

	/**
	 * Instantiates a new string tile set.
	 *
	 * @param imageName the image name
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 * @param imageHeight the image height
	 * @param imageWidth the image width
	 */
	public StringTileSet(final String imageName, final int tileWidth,
			final int tileHeight, final int imageHeight, final int imageWidth) {
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

	/**
	 * Check if this tileset contains a particular tile.
	 *
	 * @param gid The global id to seach for
	 * @return True if the ID is contained in this tileset
	 */
	public boolean contains(final int gid) {
		return (gid >= firstGID) && (gid <= lastGID);
	}

	/**
	 * Download all.
	 *
	 * @param rl the rl
	 */
	public void downloadAll(final ResourceLoader rl) {
		rl.downloadService(imageName, maxX, maxY);
	}

	/*
	 * TODO allocation problem maybe it will be nice to create String[][] array
	 * with already concanated filenames this way we will avoid string building
	 * in get bitmap function and unnecessary memory allocation this will take
	 * additional memory but might improve performance
	 */
	/**
	 * Gets the bitmap.
	 *
	 * @param x the x
	 * @param y the y
	 * @param rl the rl
	 * @return the bitmap
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public Bitmap getBitmap(final int x, final int y, final ResourceLoader rl)
			throws IndexOutOfBoundsException {
		if (x >= maxX || y >= maxY) {
			throw new IndexOutOfBoundsException("Wrong position (" + x + ","
					+ y + ") in tileset:" + imageName);
		}
		return rl.getBitmapResource(imageName + "," + x + "," + y + ".png",
				false);
	}

	/**
	 * Gets the bitmap.
	 *
	 * @param gid the gid
	 * @param rl the rl
	 * @return the bitmap
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public Bitmap getBitmap(final int gid, final ResourceLoader rl)
			throws IndexOutOfBoundsException {
		if (!contains(gid)) {
			throw new IndexOutOfBoundsException("Wrong gid (" + gid
					+ ") in tileset:" + imageName);
		}
		final int id = gid - firstGID;
		final int x = id % maxX;
		final int y = id / maxX;
		return getBitmap(x, y, rl);
	}

	/**
	 * Gets the tile name png.
	 *
	 * @param gid the gid
	 * @return the tile name png
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public String getTileNamePng(final int gid)
			throws IndexOutOfBoundsException {
		if (!contains(gid)) {
			throw new IndexOutOfBoundsException("Wrong position in tileset");
		}
		final int id = gid - firstGID;
		final int x = id % maxX;
		final int y = id / maxX;
		return imageName.replace(".png", ",") + x + "," + y + ".png";
	}

	/**
	 * Gets the tile name png.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the tile name png
	 */
	public String getTileNamePng(final int x, final int y) {
		return imageName.replace(".png", ",") + x + "," + y + ".png";
	}

}
