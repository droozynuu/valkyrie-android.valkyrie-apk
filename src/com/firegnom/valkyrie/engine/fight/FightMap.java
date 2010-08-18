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

public class FightMap implements Pathfindable {

	int width = 8;
	int height = 8;

	int tileWidth = 64;
	int tileHeight = 48;

	AStarPathFinder finder;
	private String TAG = FightMap.class.getName();

	public FightMap() {
		finder = new AStarPathFinder(this, 3, 10000, false);
	}

	public Path findPath(int x, int y, Player player) {
		int cX = player.fightPosition.x, cY = player.fightPosition.y;
		if (player.moverThread.isMoveing()) {
			return null;
			// if (!paths.isEmpty()) return null;
			// if (moverThread.p !=null) {
			// cX= moverThread.p.getX(moverThread.p.getLength()-1);
			// cY= moverThread.p.getY(moverThread.p.getLength()-1);
			// }
		}

		GameController sc = GameController.getInstance();
		Path p = finder.findPath(player, cX, cY, x, y);
		if (p == null) {
			return null;
		}
		return p;
	}

	@Override
	public boolean blocked(Mover mover, int x, int y) {
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

	@Override
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeightInTiles() {
		// TODO Auto-generated method stub
		return height;
	}

	@Override
	public int getWidthInTiles() {
		// TODO Auto-generated method stub
		return width;
	}

	@Override
	public void pathFinderVisited(int x, int y) {
		// TODO Auto-generated method stub

	}

	public Set<Position> findRange(Mover mover, int sx, int sy,
			int maxSearchDistance) {
		return finder.findRange(mover, sx, sy, maxSearchDistance);
	}

}
