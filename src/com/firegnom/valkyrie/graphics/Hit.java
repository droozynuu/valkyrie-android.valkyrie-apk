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

public class Hit implements DrawableFeature, Positionable {
	private String text;
	private int textwidth;
	private Position p;
	private static Paint circle;
	private static Paint num;
	private static int physical = 0xffff0000;
	private static int magic = 0xff0000ff;
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

	@Override
	public void draw(Canvas c) {
		c.drawCircle(p.x, p.y, 30, circle);
		c.drawText(text, p.x - textwidth / 2, p.y + 10, num);
	}

	@Override
	public Position getPosition() {
		return p;
	}

	@Override
	public void setPosition(Position p) {
		this.p = p;
	}

	public void setText(String text) {
		this.text = text;
		textwidth = (int) num.measureText(text);

	}

	@Override
	public Rect getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

}
