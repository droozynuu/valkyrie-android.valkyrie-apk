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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * A resource loading location that searches somewhere on the classpath.
 *
 * @author kevin
 */
public class FileSystemLocation implements ResourceLocation {
	
	/** The root of the file system to search. */
	private File root;

	/**
	 * Create a new resoruce location based on the file system.
	 *
	 * @param root The root of the file system to search
	 */
	public FileSystemLocation(File root) {
		this.root = root;
	}

	/**
	 * Gets the resource as stream.
	 *
	 * @param ref the ref
	 * @return the resource as stream
	 * @see ResourceLocation#getResourceAsStream(String)
	 */
	public InputStream getResourceAsStream(String ref) {
		try {
			File file = new File(root, ref);
			if (!file.exists()) {
				file = new File(ref);
			}
			return new FileInputStream(file);
		} catch (IOException e) {
			return null;
		}
	}

}
