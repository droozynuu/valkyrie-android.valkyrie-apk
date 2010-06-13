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

// TODO: Auto-generated Javadoc
/**
 * The Class DrawablePath.
 */
public class DrawablePath implements DrawableFeature {
	
	/** The proxy. */
	Path proxy;
	
	/** The tile width. */
	int tileWidth;
	
	/** The tile height. */
	int tileHeight;

	/**
	 * Instantiates a new drawable path.
	 *
	 * @param p the p
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 */
	public DrawablePath(final Path p, final int tileWidth, final int tileHeight) {
		proxy = p;
		this.tileHeight = tileHeight;
		this.tileWidth = tileWidth;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.graphics.DrawableFeature#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(final Canvas canvas) {
		new PathHelper().draw(proxy, canvas, tileWidth, tileHeight);
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.graphics.DrawableFeature#getBounds()
	 */
	@Override
	public Rect getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

}
