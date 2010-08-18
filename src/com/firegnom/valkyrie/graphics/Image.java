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

// TODO: Auto-generated Javadoc
//good idea will be to add TTL how long image can stay in memory until it will be wiped out. by ImageManager
/**
 * The Class Image.
 */
public class Image implements DrawableFeature {
	
	/** The name. */
	private String name;
	
	/** The width. */
	private int width;
	
	/** The height. */
	private int height;
	
	/** The x offset. */
	private int xOffset = 0;
	
	/** The y offset. */
	private int yOffset = 0;
	
	/** The y. */
	private int x = 0, y = 0;

	/** The ltu. */
	private long ltu;
	
	/** The ttl. */
	private int ttl = 0;

	/**
	 * Instantiates a new image.
	 *
	 * @param name the name
	 * @param width the width
	 * @param height the height
	 * @param xOffset the x offset
	 * @param yOffset the y offset
	 */
	public Image(String name, int width, int height, int xOffset, int yOffset) {
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.graphics.DrawableFeature#draw(android.graphics.Canvas)
	 */
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
	 * Sets the x.
	 *
	 * @param x the x to set
	 * @return the image
	 */
	public Image setX(int x) {
		this.x = x;
		return this;
	}

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the y.
	 *
	 * @param y the y to set
	 * @return the image
	 */
	public Image setY(int y) {
		this.y = y;
		return this;
	}

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.graphics.DrawableFeature#getBounds()
	 */
	@Override
	public Rect getBounds() {
		return new Rect(x + xOffset, y + yOffset, x + width + xOffset, y
				+ height + yOffset);
	}

}
