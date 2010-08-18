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
import android.graphics.Canvas;
import android.graphics.Rect;

//good idea will be to add TTL how long image can stay in memory until it will be wiped out. by ImageManager
public class Image implements DrawableFeature {
	private String name;
	private int width;
	private int height;
	private int xOffset = 0;
	private int yOffset = 0;
	private int x = 0, y = 0;

	private long ltu;
	private int ttl = 0;

	public Image(String name, int width, int height, int xOffset, int yOffset) {
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.name = name;
	}

	@Override
	public void draw(Canvas c) {
		ImageManager im = ImageManager.getInstance();
		Bitmap bitmap = im.getBitmap(name);
		if (bitmap == null) {
			c.drawRect(getX(), getY(), getX() + width, getY() + height,
					im.outOfMemoryPaint);
		} else {
			c.drawBitmap(bitmap, getX() + xOffset, getY() + yOffset, null);
		}
		ltu = System.currentTimeMillis();
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public Image setX(int x) {
		this.x = x;
		return this;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public Image setY(int y) {
		this.y = y;
		return this;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	@Override
	public Rect getBounds() {
		return new Rect(x + xOffset, y + yOffset, x + width + xOffset, y
				+ height + yOffset);
	}

}
