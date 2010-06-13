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

import java.lang.ref.WeakReference;

import com.firegnom.valkyrie.common.Constants;
import com.firegnom.valkyrie.common.Dir;
import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.map.pathfinding.Path;

// TODO: Auto-generated Javadoc
/**
 * The Class MoverThread.
 */
public class MoverThread extends Thread {
	
	/** The pl. */
	WeakReference<Player> pl;
	
	/** The p. */
	Path p = null;
	
	/** The gc. */
	GameController gc = GameController.getInstance();
	
	/** The is user. */
	boolean isUser;
	
	/** The moveing. */
	boolean moveing = false;
	
	/** The destination. */
	public Position destination = null;

	/**
	 * Instantiates a new mover thread.
	 *
	 * @param player the player
	 * @param isUser the is user
	 */
	public MoverThread(final Player player, final boolean isUser) {
		this.isUser = isUser;
		this.pl = new WeakReference<Player>(player);
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public Path getPath() {
		return p;
	}

	/**
	 * Checks if is moveing.
	 *
	 * @return true, if is moveing
	 */
	public boolean isMoveing() {
		return moveing;
	}

	/**
	 * Post invalidate.
	 */
	synchronized private void postInvalidate() {
		gc.postInvalidate();
	}

	/**
	 * Post invalidate.
	 *
	 * @param rLeft the r left
	 * @param rTop the r top
	 * @param rRight the r right
	 * @param rBottom the r bottom
	 */
	synchronized private void postInvalidate(final int rLeft, final int rTop,
			final int rRight, final int rBottom) {
		if (GameController.followPlayer || GameController.fullInvalidate) {
			postInvalidate();
		} else {
			gc.postInvalidateScroll(rLeft - 20, rTop - 20, rRight + 20,
					rBottom + 20);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		try {
			while (true) {
				final Player player = pl.get();
				if (player == null) {
					return;
				}
				setName(player.name);
				p = player.paths.take();
				destination = new Position(p.getX(p.getLength() - 1), p.getY(p
						.getLength() - 1));
				moveing = true;
				int i, len;
				i = 1;
				len = p.getLength();
				player.move_position = 0;
				while (i < len) {
					final int x = p.getX(i), y = p.getY(i);
					int px = player.position.x, py = player.position.y;
					// check if player position is close to path a bit dirty but
					// should
					// work fine
					if ((px - x + 1) < 0 || (px - x + 1) > 2) {
						player.position.x = x;
					}
					if ((py - y + 1) < 0 || (py - y + 1) > 2) {
						player.position.y = y;
					}
					px = player.position.x;
					py = player.position.y;
					player.direction = Dir.DIR_MATRIX[px - x + 1][py - y + 1];

					if (gc.zone == null) {
						continue;
					}

					Position ppp = player.getOnscreenPosition(gc.zone);
					postInvalidate(ppp.x, ppp.y, ppp.x + player.pictureSize,
							ppp.y + player.pictureSize);
					short j = player.move_position;
					if (GameController.jumpyMoves) {
						Thread.sleep(8 * Player.MOVE_SPEED);
					} else {
						while (j < 8) {
							player.position_trans.x = (px - x) * (j * (32 / 8));
							player.position_trans.y = (py - y) * (j * (24 / 8));
							if (GameController.changeMoveImages) {
								player.move_position = j;
							}
							ppp = player.getOnscreenPosition(gc.zone);
							if (ppp != null) {
								postInvalidate(ppp.x, ppp.y, ppp.x
										+ player.pictureSize, ppp.y
										+ player.pictureSize);
							}
							j++;
							Thread.sleep(Player.MOVE_SPEED);
						}
					}
					player.position.x = x;
					player.position.y = y;
					player.position_trans.x = 0;
					player.position_trans.y = 0;
					player.move_position = 0;
					i++;
				}
				if (!gc.user.position.inRange(player.position,
						Constants.VISIBILITY_RANGE)) {
					gc.players.remove(player.name);
					player.destroy();
					gc.postInvalidate();
					return;
				}
				player.move_position = 7;
				destination = null;
				moveing = false;
				gc.postInvalidate();
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
			moveing = false;
			gc.postInvalidate();
		}

	}

}
