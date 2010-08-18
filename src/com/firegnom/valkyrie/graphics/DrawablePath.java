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
import android.graphics.Rect;

import com.firegnom.valkyrie.map.pathfinding.Path;
import com.firegnom.valkyrie.util.PathHelper;

public class DrawablePath implements DrawableFeature {
	Path proxy;
	int tileWidth;
	int tileHeight;

	public DrawablePath(Path p, int tileWidth, int tileHeight) {
		proxy = p;
		this.tileHeight = tileHeight;
		this.tileWidth = tileWidth;
	}

	@Override
	public void draw(Canvas canvas) {
		new PathHelper().draw(proxy, canvas, tileWidth, tileHeight);
	}

	@Override
	public Rect getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

}
