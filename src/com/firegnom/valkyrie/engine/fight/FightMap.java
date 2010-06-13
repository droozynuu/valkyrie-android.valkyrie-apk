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

import java.util.Set;

import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.Player;
import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.map.pathfinding.AStarPathFinder;
import com.firegnom.valkyrie.map.pathfinding.Mover;
import com.firegnom.valkyrie.map.pathfinding.Path;
import com.firegnom.valkyrie.map.pathfinding.Pathfindable;

// TODO: Auto-generated Javadoc
/**
 * The Class FightMap.
 */
public class FightMap implements Pathfindable {

	/** The width. */
	int width = 8;
	
	/** The height. */
	int height = 8;

	/** The tile width. */
	int tileWidth = 64;
	
	/** The tile height. */
	int tileHeight = 48;

	/** The finder. */
	AStarPathFinder finder;
	
	/** The TAG. */
	private final String TAG = FightMap.class.getName();

	/**
	 * Instantiates a new fight map.
	 */
	public FightMap() {
		finder = new AStarPathFinder(this, 3, 10000, false);
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#blocked(com.firegnom.valkyrie.map.pathfinding.Mover, int, int)
	 */
	@Override
	public boolean blocked(final Mover mover, final int x, final int y) {
		// if (x == 4&&y==3) return true;
		if (GameController.getInstance().user.fightPosition.equals(x, y)) {
			return true;
		}
		if (GameController.getInstance().fightController.enemy.fightPosition
				.equals(x, y)) {
			return true;
		}
		return false;
	}

	/**
	 * Find path.
	 *
	 * @param x the x
	 * @param y the y
	 * @param player the player
	 * @return the path
	 */
	public Path findPath(final int x, final int y, final Player player) {
		final int cX = player.fightPosition.x, cY = player.fightPosition.y;
		if (player.moverThread.isMoveing()) {
			return null;
			// if (!paths.isEmpty()) return null;
			// if (moverThread.p !=null) {
			// cX= moverThread.p.getX(moverThread.p.getLength()-1);
			// cY= moverThread.p.getY(moverThread.p.getLength()-1);
			// }
		}

		final GameController sc = GameController.getInstance();
		final Path p = finder.findPath(player, cX, cY, x, y);
		if (p == null) {
			return null;
		}
		return p;
	}

	/**
	 * Find range.
	 *
	 * @param mover the mover
	 * @param sx the sx
	 * @param sy the sy
	 * @param maxSearchDistance the max search distance
	 * @return the set
	 */
	public Set<Position> findRange(final Mover mover, final int sx,
			final int sy, final int maxSearchDistance) {
		return finder.findRange(mover, sx, sy, maxSearchDistance);
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#getCost(com.firegnom.valkyrie.map.pathfinding.Mover, int, int, int, int)
	 */
	@Override
	public float getCost(final Mover mover, final int sx, final int sy,
			final int tx, final int ty) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#getHeightInTiles()
	 */
	@Override
	public int getHeightInTiles() {
		// TODO Auto-generated method stub
		return height;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#getWidthInTiles()
	 */
	@Override
	public int getWidthInTiles() {
		// TODO Auto-generated method stub
		return width;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.map.pathfinding.Pathfindable#pathFinderVisited(int, int)
	 */
	@Override
	public void pathFinderVisited(final int x, final int y) {
		// TODO Auto-generated method stub

	}

}
