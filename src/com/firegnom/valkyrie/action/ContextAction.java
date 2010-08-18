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

public class ContextAction {
	private static int currentid = 0;
	private int id;
	public float x;
	public float y;
	public float height;
	public float width;
	public byte[] script;
	public String name;
	public String container;
	public int type = 0;
	public int actionId;
	public static final int SCRIPT_ACTION = 0;
	public static final int DEBUG_SCRIPT_EDITOR = 1;
	public static final int ACTION_MANAGER = 2;

	public ContextAction() {
		currentid++;
		id = currentid;
	}

	public ContextAction setType(int type) {
		this.type = type;
		return this;
	}

	public int getId() {
		return id;
	}

	public ContextAction setName(String name) {
		currentid++;
		id = currentid;
		this.name = name;
		return this;
	}

}
