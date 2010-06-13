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
package com.firegnom.valkyrie.engine.fight;

import com.firegnom.valkyrie.common.Dir;
import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.Player;
import com.firegnom.valkyrie.map.pathfinding.Path;
import com.firegnom.valkyrie.map.pathfinding.Step;

// TODO: Auto-generated Javadoc
/**
 * The Class MoveThread.
 */
public class MoveThread extends Thread {
	
	/** The player. */
	Player player;
	
	/** The path. */
	Path path;

	/**
	 * Instantiates a new move thread.
	 *
	 * @param player the player
	 * @param path the path
	 */
	public MoveThread(final Player player, final Path path) {
		this.player = player;
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		final FightController fc = GameController.getInstance().fightController;
		// Step = path.;

		Step st = path.steps.poll();
		Step step = path.steps.peek();

		while (step != null) {
			player.animation.changeDir(Dir.DIR_MATRIX[st.getX() - step.getX()
					+ 1][st.getY() - step.getY() + 1]);
			for (int i = 0; i < player.animation.getNumOfFrames() * 3; i++) {
				player.fightMoveX += (step.getX() - st.getX())
						* (fc.map.tileWidth / 3.0 / player.animation
								.getNumOfFrames());
				player.fightMoveY += (step.getY() - st.getY())
						* (fc.map.tileHeight / 3.0 / player.animation
								.getNumOfFrames());
				fc.postInvalidate();
				try {
					sleep(60);
				} catch (final InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// player.fightPosition.y=step.getY();

			}

			player.fightMoveX = 0;
			player.fightMoveY = 0;
			player.setFightPosition(step.getX(), step.getY());
			st = step;
			path.steps.poll();
			step = path.steps.peek();
			fc.postInvalidate();

		}
		fc.action.finished();
	}

}
