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

import java.util.Iterator;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.firegnom.valkyrie.map.pathfinding.Path;
import com.firegnom.valkyrie.map.pathfinding.Step;

public class PathHelper {
	Paint mPaint;

	public PathHelper() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(false);
		mPaint.setColor(0x6000FF00);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(10);
	}

	public void draw(Path p, Canvas c, int tileWidth, int tileHeight) {
		android.graphics.Path pa = new android.graphics.Path();
		if (p == null) {
			return;
		}
		Iterator<Step> i = p.steps.iterator();
		Step step;
		if (i.hasNext()) {
			step = i.next();
			pa.moveTo(step.getX() * tileWidth + tileWidth / 2, step.getY()
					* tileHeight + tileHeight / 2);
		} else {
			return;
		}
		while (i.hasNext()) {
			Step nextStep;
			if (i.hasNext()) {
				nextStep = i.next();
				pa.lineTo(nextStep.getX() * tileWidth + tileWidth / 2,
						nextStep.getY() * tileHeight + tileHeight / 2);
			}
		}
		c.drawPath(pa, mPaint);
	}
}
