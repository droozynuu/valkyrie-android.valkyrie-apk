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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.map.Positionable;

// TODO: Auto-generated Javadoc
/**
 * The Class Hit.
 */
public class Hit implements DrawableFeature, Positionable {
	
	/** The text. */
	private String text;
	
	/** The textwidth. */
	private int textwidth;
	
	/** The p. */
	private Position p;
	
	/** The circle. */
	private static Paint circle;
	
	/** The num. */
	private static Paint num;
	
	/** The physical. */
	private static int physical = 0xffff0000;
	
	/** The magic. */
	private static int magic = 0xff0000ff;
	
	/** The skill. */
	private static int skill = 0xff00ff00;

	static {
		circle = new Paint();
		circle.setColor(physical);
		circle.setStyle(Paint.Style.FILL);
		circle.setDither(false);
		circle.setAntiAlias(true);
		circle.setFilterBitmap(false);

		num = new Paint();
		num.setColor(0xffffffff);
		num.setAntiAlias(true);
		num.setTextSize(25);
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.graphics.DrawableFeature#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(final Canvas c) {
		c.drawCircle(p.x, p.y, 30, circle);
		c.drawText(text, p.x - textwidth / 2, p.y + 10, num);
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.graphics.DrawableFeature#getBounds()
	 */
	@Override
	public Rect getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.Positionable#getPosition()
	 */
	@Override
	public Position getPosition() {
		return p;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.Positionable#setPosition(com.firegnom.valkyrie.map.Position)
	 */
	@Override
	public void setPosition(final Position p) {
		this.p = p;
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(final String text) {
		this.text = text;
		textwidth = (int) num.measureText(text);

	}

}
