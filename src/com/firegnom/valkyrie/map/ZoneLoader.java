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

import android.content.Context;

// TODO: Auto-generated Javadoc
/**
 * The Interface ZoneLoader.
 */
public interface ZoneLoader {
	
	/**
	 * Load.
	 *
	 * @param name the name
	 * @param version the version
	 * @return the zone
	 */
	Zone load(String name, int version);

	/**
	 * Load.
	 *
	 * @param name the name
	 * @param c the c
	 * @return the zone
	 */
	Zone load(String name, Context c);

	/**
	 * Load.
	 *
	 * @param name the name
	 * @return the zone
	 */
	Zone load(String name);
}
