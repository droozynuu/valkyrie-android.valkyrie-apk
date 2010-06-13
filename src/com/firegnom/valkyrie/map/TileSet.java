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
//package com.firegnom.valkyrie.map;
//
//import com.firegnom.valkyrie.util.ResourceLoader;
//
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.Config;
//import android.util.Log;
//
//public class TileSet {
//	private static final String TAG = TileSet.class.getName();
//  public Bitmap tiles;
//	public int tileWidth;
//	public int tileHeight;
//	public int columns;
//	public int rows;
//	public String name;
//	/** The index of the tile set */
//	public int index;
//	/** The first global tile id in the set */
//	public int firstGID;
//	/** The local global tile id in the set */
//	public int lastGID;
//
//	/** The width of the tiles */
//
//	public TileSet() {
//
//	}
//
//	public TileSet(int first,int last) {
//		firstGID = first;
//		lastGID = last;
//
//	}
//
//	public TileSet(Bitmap tileSet, int tileWidth, int tileHeight, int columns,
//			int rows) {
//		super();
//		if (tileSet == null)
//			throw new ExceptionInInitializerError("Tileset cannot be null");
//		this.tiles = tileSet;
//		this.tileWidth = tileWidth;
//		this.tileHeight = tileHeight;
//		this.columns = columns;
//		this.rows = rows;
//	}
//
//	public TileSet(Bitmap tiles, int tileWidth, int tileHeight) {
//		super();
//		// check if tile is not null
//		if (tiles == null)
//			throw new ExceptionInInitializerError("Tileset cannot be null");
//		// check if bitmap width and height divides tile width and height
//		if (tiles.getWidth() % tileWidth != 0
//				|| tiles.getHeight() % tileHeight != 0)
//			throw new ExceptionInInitializerError(
//					"Incorrect size of tile set bitmap Bitmap");
//		this.tiles = tiles;
//		this.tileWidth = tileWidth;
//		this.tileHeight = tileHeight;
//
//		columns = (tiles.getWidth() / tileWidth);
//		rows = (tiles.getHeight() / tileHeight);
//	}
//
//	public Bitmap getBitmap(int x, int y) throws IndexOutOfBoundsException {
//		if (x >= columns || y >= rows)
//			throw new IndexOutOfBoundsException("Wrong position in tileset");
//		Bitmap b;
//    try {
//      b = Bitmap.createBitmap(tiles, tileWidth * x, tileHeight * y,
//          tileWidth, tileHeight);
//    } catch (OutOfMemoryError e) {
//      Log.d(TAG,"OutOfMemoryError clean cache");
//      ResourceLoader.emptyBitmapCache();
//      System.gc();
//    }
//    b = Bitmap.createBitmap(tiles, tileWidth * x, tileHeight * y,
//        tileWidth, tileHeight);
//		return b;
//	}
//
//	public Bitmap getBitmap(int gid) throws IndexOutOfBoundsException {
//		if (!contains(gid))
//			throw new IndexOutOfBoundsException("Wrong position in tileset");
//		int id = gid - firstGID;
//		int x = id % columns;
//		int y = id / columns;
//		return getBitmap(x, y);
//	}
//	
//	
//
//	/**
//	 * Check if this tileset contains a particular tile
//	 * 
//	 * @param gid
//	 *            The global id to seach for
//	 * @return True if the ID is contained in this tileset
//	 */
//	public boolean contains(int gid) {
//		return (gid >= firstGID) && (gid <= lastGID);
//	}
//
// }
