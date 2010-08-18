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
package com.firegnom.valkyrie.scripting.impl;

import android.view.View;

import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.Player;
import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.scripting.SPlayer;

// TODO: Auto-generated Javadoc
/**
 * The Class SPlayerImpl.
 */
public class SPlayerImpl implements SPlayer {
	
	/** The v. */
	private final View v;

	/**
	 * Instantiates a new s player impl.
	 *
	 * @param v the v
	 */
	public SPlayerImpl(View v) {
		this.v = v;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SPlayer#goTo(int, int)
	 */
	@Override
	public void goTo(int x, int y) {
		GameController.getInstance().user.setPosition(x, y);
		v.postInvalidate();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SPlayer#move(int, int)
	 */
	@Override
	public void move(int x, int y) {
		GameController.getInstance().user.goTo(x, y, v);
		v.postInvalidate();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SPlayer#moveW(int, int)
	 */
	@Override
	public void moveW(int x, int y) {
		move(x, y);
		waitForMove();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SPlayer#canReach(int, int)
	 */
	@Override
	public boolean canReach(int x, int y) {

		return getPath(x, y) != null;

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SPlayer#getPath(int, int)
	 */
	@Override
	public com.firegnom.valkyrie.net.protocol.Path getPath(int x, int y) {
		// return GameController.getInstance().user.getPathTo(x, y,
		// GameController.getInstance()).convertToNetPath();
		return null;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SPlayer#show()
	 */
	@Override
	public void show() {
		GameController.getInstance().user.showMe(GameController.getInstance());
		v.postInvalidate();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SPlayer#speed(int)
	 */
	@Override
	public void speed(int s) {
		Player.MOVE_SPEED = s;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SPlayer#waitForMove()
	 */
	@Override
	public void waitForMove() {
		try {
			while (GameController.getInstance().user.moverThread != null) {
				Thread.sleep(200);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SPlayer#distance(int, int)
	 */
	@Override
	public int distance(int x, int y) {
		Player p = GameController.getInstance().user;
		float dx = p.position.x - x;
		float dy = p.position.y - y;
		return (int) (Math.sqrt((dx * dx) + (dy * dy)));
	}

	/**
	 * Distance.
	 *
	 * @param p the p
	 * @return the int
	 */
	public int distance(Position p) {
		return distance(p.x, p.y);
	}

}
