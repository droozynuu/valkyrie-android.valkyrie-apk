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

import android.os.RemoteException;
import android.util.Log;

import com.firegnom.valkyrie.map.ZoneLoader;
import com.firegnom.valkyrie.map.tiled.TiledZoneLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class ZoneLoaderThread.
 */
public class ZoneLoaderThread extends Thread {
	
	/** The Constant TAG. */
	private static final String TAG = ZoneLoaderThread.class.getName();
	
	/** The name. */
	private String name;
	
	/** The y. */
	int x, y;

	/**
	 * Load.
	 *
	 * @param name the name
	 * @param x the x
	 * @param y the y
	 */
	public void load(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Log.d(TAG, "jestem");
		GameController gc = GameController.getInstance();
		// ((GameActivity)context).setContentView(R.layout.please_wait);
		// gc.wait = new
		// TileSet(BitmapFactory.decodeResource(gc.context.getResources(),
		// R.drawable.wait), 96, 96);
		gc.postInvalidate();
		gc.disableContext = true;
		gc.disableInput = true;
		gc.disableScroll = true;
		gc.zoneLoading = true;
		gc.zone = null;
		ZoneLoader zl = new TiledZoneLoader(gc.context);
		gc.zone = zl.load(name);
		int height = gc.zone.height;
		gc.user.setPosition(x, y);
		gc.user.showMe(gc);
		gc.renderScreen();
		gc.postInvalidate();
		try {
			
			//TODO throws a null pointer exception here can lose service during load it might be better to create function in gc 
			//which will check if service is connected if not put request on the queue and as soon it conects again execute it

			//quick fix
			if (gc.service != null )
				gc.service.requestPlayersPositions();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gc.zoneLoading = false;
		gc.disableContext = false;
		gc.disableInput = false;
		gc.disableScroll = false;
		System.gc();
	}

}
