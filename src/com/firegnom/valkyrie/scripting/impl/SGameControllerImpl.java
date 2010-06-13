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

// TODO: Auto-generated Javadoc
/**
 * The Class SGameControllerImpl.
 */
public class SGameControllerImpl implements SGameController {
	
	/** The sc. */
	private final GameController sc;
	
	/** The context. */
	private final Context context;
	
	/** The Constant z1. */
	public static final String z1 = "test4";
	
	/** The Constant z2. */
	public static final String z2 = "test6";
	
	/** The v. */
	private final View v;

	/**
	 * Instantiates a new s game controller impl.
	 *
	 * @param sc the sc
	 * @param context the context
	 * @param v the v
	 */
	public SGameControllerImpl(final GameController sc, final Context context,
			final View v) {
		this.sc = sc;
		// TODO Auto-generated constructor stub
		this.context = context;
		this.v = v;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SGameController#followPlayer(boolean)
	 */
	@Override
	public void followPlayer(final boolean b) {
		GameController.followPlayer = b;

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SGameController#load(java.lang.String)
	 */
	@Override
	public void load(final String name) {
		// sc.loadZone(name, context);
		new ZoneLoaderThread().load(name, 10, 10);
		v.postInvalidate();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SGameController#moves()
	 */
	@Override
	public void moves() {
		sc.toogleMoveMatrix();
		v.postInvalidate();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SGameController#sleep(int)
	 */
	@Override
	public void sleep(final int time) {
		try {
			Thread.sleep(time);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
