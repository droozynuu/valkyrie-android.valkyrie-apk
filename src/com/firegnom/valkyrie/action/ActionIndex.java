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

import gnu.trove.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Properties;

import com.firegnom.valkyrie.util.IntProcedure;
import com.firegnom.valkyrie.util.Point;
import com.firegnom.valkyrie.util.Rectangle;
import com.firegnom.valkyrie.util.rtree.RTree;

// TODO: Auto-generated Javadoc
/**
 * The Class ActionIndex.
 */
public class ActionIndex implements IntProcedure {
	
	/** The actiontree. */
	private final RTree actiontree;
	
	/** The map. */
	private final TIntObjectHashMap<ContextAction> map;
	
	/** The ret. */
	private ArrayList<ContextAction> ret;
	
	/** The retindex. */
	private int retindex;
	
	/** The max items. */
	public int maxItems = 20;
	
	/** The Constant MAX_NUMBER_OF_ACTIONS. */
	public static final int MAX_NUMBER_OF_ACTIONS = 250;
	
	/** The showeditor. */
	public boolean showeditor = false;
	
	/** The Constant editor. */
	private final static ContextAction editor = new ContextAction().setName(
			"script editor").setType(ContextAction.DEBUG_SCRIPT_EDITOR);

	/**
	 * Instantiates a new action index.
	 */
	public ActionIndex() {
		actiontree = new RTree();
		final Properties p = new Properties();
		// p.setProperty("TreeVariant", "Linear");
		p.setProperty("MinNodeEntries", "2");
		p.setProperty("MaxNodeEntries", "20"); // reasonable values?
		actiontree.init(p);
		map = new TIntObjectHashMap<ContextAction>();

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.util.IntProcedure#execute(int)
	 */
	@Override
	public boolean execute(final int id) {
		ret.add(map.get(id));
		retindex++;
		return false;
	}

	/**
	 * Gets the.
	 *
	 * @param x the x
	 * @param y the y
	 * @param radius the radius
	 * @return the array list
	 */
	public ArrayList<ContextAction> get(final int x, final int y,
			final int radius) {
		ret = new ArrayList<ContextAction>();
		retindex = 0;
		actiontree.nearest(new Point(x, y), this, radius);
		if (showeditor) {
			ret.add(editor);
		}
		return ret;
	}

	/**
	 * Gets the.
	 *
	 * @param r the r
	 * @return the array list
	 */
	public ArrayList<ContextAction> get(final Rectangle r) {
		ret = new ArrayList<ContextAction>();
		retindex = 0;

		actiontree.intersects(r, this);
		if (showeditor) {
			ret.add(editor);
		}
		return ret;
	}

	/**
	 * Put.
	 *
	 * @param ca the ca
	 */
	public void put(final ContextAction ca) {
		map.put(ca.getId(), ca);
		actiontree.add(new Rectangle(ca.x, ca.y, ca.x + ca.width, ca.y
				+ ca.height), ca.getId());
	}
}
