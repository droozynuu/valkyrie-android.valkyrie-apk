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

// TODO: Auto-generated Javadoc
/**
 * The Class MapObject.
 */
public class MapObject {
	
	/** The currentid. */
	private static int currentid = 0;
	
	/** The id. */
	private int id;
	
	/** The x. */
	public int x;
	
	/** The y. */
	public int y;
	
	/** The height. */
	public int height;
	
	/** The width. */
	public int width;
	
	/** The name. */
	public String name;
	
	/** The image. */
	public String image;
	
	/** The visible. */
	boolean visible;

	/**
	 * Instantiates a new map object.
	 */
	public MapObject() {
		currentid++;
		id = currentid;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

}
