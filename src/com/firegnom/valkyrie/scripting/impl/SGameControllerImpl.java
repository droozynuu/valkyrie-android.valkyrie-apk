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

import android.content.Context;
import android.view.View;

import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.ZoneLoaderThread;
import com.firegnom.valkyrie.scripting.SGameController;

public class SGameControllerImpl implements SGameController {
	private final GameController sc;
	private final Context context;
	public static final String z1 = "test4";
	public static final String z2 = "test6";
	private final View v;

	public SGameControllerImpl(GameController sc, Context context, View v) {
		this.sc = sc;
		// TODO Auto-generated constructor stub
		this.context = context;
		this.v = v;
	}

	@Override
	public void load(String name) {
		// sc.loadZone(name, context);
		new ZoneLoaderThread().load(name, 10, 10);
		v.postInvalidate();
	}

	@Override
	public void moves() {
		sc.toogleMoveMatrix();
		v.postInvalidate();
	}

	@Override
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void followPlayer(boolean b) {
		GameController.followPlayer = b;

	}

}
