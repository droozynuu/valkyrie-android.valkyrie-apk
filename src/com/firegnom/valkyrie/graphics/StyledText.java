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

public class StyledText implements DrawableFeature {
	private Paint p;
	private String text;
	private float x;
	private float y;

	public StyledText(String text, int fontColor, int size, float x, float y) {
		this.text = text;
		p = new Paint();
		p.setColor(fontColor);
		p.setTextSize(size);
		this.x = x;
		this.y = y;
	}

	@Override
	public void draw(Canvas c) {
		c.drawText(text, x, y, p);

	}

	Rect getTextBounds(Rect ret) {
		p.getTextBounds(text, 0, text.length(), ret);
		return ret;
	}

	Rect getTextBounds() {
		Rect ret = new Rect();
		return getTextBounds(ret);
	}

	@Override
	public Rect getBounds() {
		// TODO Auto-generated method stub
		return null;
	}
}
