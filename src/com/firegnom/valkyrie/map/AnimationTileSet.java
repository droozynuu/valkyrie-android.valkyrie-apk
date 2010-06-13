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
//
//import android.graphics.Bitmap;
//import android.graphics.drawable.AnimationDrawable;
//import android.graphics.drawable.BitmapDrawable;
///**
// * 
// * @author macio
// *
// */
//public class AnimationTileSet extends TileSet {
//	private boolean horizontal = true;
//	private boolean oneShot = false;
//	/**
//	 * 
//	 * @param tileSet
//	 * @param tileWidth
//	 * @param tileHeight
//	 */
//	public AnimationTileSet(Bitmap tileSet, short tileWidth, short tileHeight ) {
//		super(tileSet, tileWidth, tileHeight);
//		// TODO Auto-generated constructor stub
//	}
//	/**This function is returning {@link AnimationDrawable} at given position of 
//	 *  {@link TileSet} 
//	 * 
//	 * @param position
//	 * @return {@link AnimationDrawable}
//	 */
//	public AnimationDrawable getAnimation(int position, int speed){
//		int size = horizontal ? this.columns : this.rows;
//		AnimationDrawable ret = new AnimationDrawable();
//		for (int i = 0 ; i < size; i++ ){
//			if (horizontal)
//				ret.addFrame(new BitmapDrawable(this.getBitmap(i, position)), speed);
//			else
//				ret.addFrame(new BitmapDrawable(this.getBitmap(position, i)), speed);
//		}
//		ret.setOneShot(oneShot);
//		return ret;
//	}
//
// }
