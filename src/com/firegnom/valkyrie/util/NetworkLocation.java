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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

// TODO: Auto-generated Javadoc
/**
 * A resource location that searches the classpath.
 *
 * @author kevin
 */

public class NetworkLocation implements ResourceLocation {
	
	/** The link. */
	private final String link;

	/**
	 * Instantiates a new network location.
	 *
	 * @param string the string
	 */
	public NetworkLocation(final String string) {
		link = string;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.util.ResourceLocation#getResourceAsStream(java.lang.String)
	 */
	@Override
	public InputStream getResourceAsStream(final String ref) {
		try {
			return new URL(link + ref).openStream();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
