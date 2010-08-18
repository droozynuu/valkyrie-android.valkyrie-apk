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
package com.firegnom.valkyrie.map;

import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * The Class Layer.
 */
public class Layer {
	
	/** The index of this layer. */
	public int index;
	
	/** The name of this layer - read from the XML. */
	public String name;
	
	/** The tile data representing this data, index 0 = tileset, index 1 = tile id. */
	public int[][] data;
	
	/** The width of this layer. */
	public int width;
	
	/** The height of this layer. */
	public int height;

	/** the properties of this layer. */
	public Properties props;

	/**
	 * Instantiates a new layer.
	 *
	 * @param name the name
	 * @param width the width
	 * @param height the height
	 */
	public Layer(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		data = new int[width][height];
	}

	/**
	 * Get the gloal ID of the tile at the specified location in this layer.
	 *
	 * @param x The x coorindate of the tile
	 * @param y The y coorindate of the tile
	 * @return The global ID of the tile
	 */
	public int getTileID(int x, int y) {
		return data[x][y];
	}

}
