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

import android.util.Log;

import com.firegnom.valkyrie.FightActivity;
import com.firegnom.valkyrie.R;
import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.Gui;
import com.firegnom.valkyrie.graphics.DrawablePath;
import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.map.pathfinding.Path;

public class WalkAction implements FightAction {

	private static final String TAG = WalkAction.class.getName();
	private boolean active = false;
	private DrawablePath dp;

	@Override
	public void activated() {
		GameController.getInstance().fightController.prepareRange();
		GameController.getInstance().fightController.view.post(new Runnable() {
			@Override
			public void run() {
				((FightActivity) GameController.getInstance().fightController.context).walkB
						.setBackgroundResource(R.drawable.walk_en);
			}
		});
	}

	@Override
	public void deactivated() {
		GameController.getInstance().fightController.view.post(new Runnable() {
			@Override
			public void run() {
				((FightActivity) GameController.getInstance().fightController.context).walkB
						.setBackgroundResource(R.drawable.walk);
			}
		});

	}

	@Override
	public void onSingleTapUp(int x, int y) {
		if (active) {
			return;
		}
		active = true;
		GameController gc = GameController.getInstance();
		gc.fightController.range = null;
		Path p = gc.fightController.map.findPath(x, y, gc.user);
		if (p == null) {
			Gui.toast(R.string.pathfinding_how_to_get_there,
					gc.fightController.view, gc.fightController.context);
			finished();

			return;
		}
		dp = new DrawablePath(p, gc.fightController.map.tileWidth,
				gc.fightController.map.tileHeight);
		gc.fightController.features.add(dp);
		Log.d(TAG, "Clicked : " + new Position(x, y) + " Path :" + p);
		// gc.fightController.executor.execute(command);
		new MoveThread(gc.user, p).start();
		gc.user.animation.start(gc.fightController.executor);
		// gc.fightController.executor.execute(new MovePathTask(p,
		// gc.user,gc.fightController.executor));
		gc.fightController.view.invalidate();
	}

	@Override
	public void finished() {
		GameController gc = GameController.getInstance();
		active = false;
		gc.fightController.features.remove(dp);
		gc.fightController.postInvalidate();
		gc.fightController.action = null;
		gc.user.animation.stop();
		GameController.getInstance().fightController.view.post(new Runnable() {
			@Override
			public void run() {
				((FightActivity) GameController.getInstance().fightController.context).walkB
						.setBackgroundResource(R.drawable.walk);
			}
		});

	}

	@Override
	public boolean isActive() {
		return active;
	}

}
