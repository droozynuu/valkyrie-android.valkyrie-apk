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
package com.firegnom.valkyrie.action;

// TODO: Auto-generated Javadoc
/**
 * The Class ContextAction.
 */
public class ContextAction {
	
	/** The currentid. */
	private static int currentid = 0;
	
	/** The id. */
	private int id;
	
	/** The x. */
	public float x;
	
	/** The y. */
	public float y;
	
	/** The height. */
	public float height;
	
	/** The width. */
	public float width;
	
	/** The script. */
	public byte[] script;
	
	/** The name. */
	public String name;
	
	/** The container. */
	public String container;
	
	/** The type. */
	public int type = 0;
	
	/** The action id. */
	public int actionId;
	
	/** The Constant SCRIPT_ACTION. */
	public static final int SCRIPT_ACTION = 0;
	
	/** The Constant DEBUG_SCRIPT_EDITOR. */
	public static final int DEBUG_SCRIPT_EDITOR = 1;
	
	/** The Constant ACTION_MANAGER. */
	public static final int ACTION_MANAGER = 2;

	/**
	 * Instantiates a new context action.
	 */
	public ContextAction() {
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

	/**
	 * Sets the name.
	 *
	 * @param name the name
	 * @return the context action
	 */
	public ContextAction setName(final String name) {
		currentid++;
		id = currentid;
		this.name = name;
		return this;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the type
	 * @return the context action
	 */
	public ContextAction setType(final int type) {
		this.type = type;
		return this;
	}

}
