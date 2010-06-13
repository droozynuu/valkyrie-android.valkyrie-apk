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
package com.firegnom.valkyrie.engine;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.firegnom.valkyrie.action.ContextAction;
import com.firegnom.valkyrie.common.Constants;
import com.firegnom.valkyrie.map.Position;

// TODO: Auto-generated Javadoc
/**
 * The Class PlayersTable.
 */
public class PlayersTable {
	
	/** The players. */
	Hashtable<String, Player> players;

	/**
	 * Instantiates a new players table.
	 */
	public PlayersTable() {
		players = new Hashtable<String, Player>();
	}

	/**
	 * Clean.
	 */
	public void clean() {
		final Position userpos = GameController.getInstance().user.position;
		final Position userdest = GameController.getInstance().user
				.destination();
		final Enumeration<Player> elements = players.elements();
		while (elements.hasMoreElements()) {
			final Player p = elements.nextElement();
			if (!p.destination().inRange(userdest, Constants.VISIBILITY_RANGE)
					|| !p.destination().inRange(userpos,
							Constants.VISIBILITY_RANGE)) {
				players.remove(p.name);
				p.destroy();
			}
		}
		// TODO Auto-generated method stub

	}

	/**
	 * Clear.
	 */
	public void clear() {
		players.clear();

	}

	/**
	 * Contains player.
	 *
	 * @param p the p
	 * @return true, if successful
	 */
	public boolean containsPlayer(final Player p) {
		return players.containsKey(p.name);
	}

	/**
	 * Elements.
	 *
	 * @return the enumeration
	 */
	public Enumeration<Player> elements() {
		return players.elements();
	}

	/**
	 * Gets the.
	 *
	 * @param p the p
	 * @param range the range
	 * @return the array list
	 */
	public ArrayList<Player> get(final Position p, final int range) {

		final ArrayList<Player> list = new ArrayList<Player>();
		final Enumeration<Player> elements = players.elements();
		Player pl;
		while (elements.hasMoreElements()) {
			pl = elements.nextElement();
			if (pl.position.inRange(p, range)) {
				list.add(pl);
			}
		}
		return list;
	}

	/**
	 * Gets the.
	 *
	 * @param username the username
	 * @return the player
	 */
	public Player get(final String username) {
		return players.get(username);
	}

	/**
	 * Gets the actions.
	 *
	 * @param p the p
	 * @param range the range
	 * @return the actions
	 */
	public ArrayList<ContextAction> getActions(final Position p, final int range) {
		final ArrayList<ContextAction> list = new ArrayList<ContextAction>();
		final Enumeration<Player> elements = players.elements();
		Player pl;
		while (elements.hasMoreElements()) {
			pl = elements.nextElement();
			if (pl.position.inRange(p, range)) {
				list.addAll(pl.getActions());
			}
		}
		return list;
	}

	/**
	 * New tab.
	 *
	 * @param newp the newp
	 */
	public void newTab(final Hashtable<String, Player> newp) {
		players.clear();
		players = newp;
	}

	/**
	 * Put.
	 *
	 * @param username the username
	 * @param pl the pl
	 */
	public void put(final String username, final Player pl) {
		players.put(username, pl);
	}

	/**
	 * Removes the.
	 *
	 * @param username the username
	 */
	public void remove(final String username) {
		players.remove(username);

	}

}
