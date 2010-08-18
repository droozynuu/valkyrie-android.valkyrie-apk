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
import com.firegnom.valkyrie.graphics.DrawablePath;

public class RunAwayAction implements FightAction {

	private static final String TAG = RunAwayAction.class.getName();
	private boolean active = false;
	private DrawablePath dp;

	@Override
	public void activated() {
		GameController gc = GameController.getInstance();
		gc.user.fightPosition.setXY(0, 0);
		gc.user.animation.changeDir(Dir.E);

		gc.fightController.exit();

	}

	@Override
	public void deactivated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSingleTapUp(int x, int y) {

	}

	@Override
	public void finished() {

	}

	@Override
	public boolean isActive() {
		return active;
	}

}
