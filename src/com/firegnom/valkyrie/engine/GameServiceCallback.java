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

import com.firegnom.valkyrie.common.PlayerClass;
import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.map.StringTileSet;
import com.firegnom.valkyrie.map.pathfinding.ParcelablePath;
import com.firegnom.valkyrie.map.pathfinding.Path;
import com.firegnom.valkyrie.service.IGameServiceCallback;

public class GameServiceCallback extends IGameServiceCallback.Stub {

	private String TAG = GameServiceCallback.class.getName();

	@Override
	public void playerMoved(String username, int playerClass, ParcelablePath pa)
			throws RemoteException {
		GameController gc = GameController.getInstance();
		Log.d(TAG, "playerMoved");
		Player pl = gc.players.get(username);
		Path p = pa.path;
		if (pl == null) {
			pl = new Player(username);
			pl.setMoveTileSet(new StringTileSet(
					PlayerClass.IMAGES[playerClass], 96, 96, 768, 768));
			pl.position.x = p.getX(0);
			pl.position.y = p.getY(0);
			pl.setPlayerClass(playerClass);
			gc.players.put(username, pl);
			pl.startMoverThread();
		}
		pl.paths.add(p);
	}

	@Override
	public void disconnected() throws RemoteException {
		GameController gc = GameController.getInstance();
		gc.goToLogin();
	}

	@Override
	public void playerDisconnected(String username) throws RemoteException {
		GameController gc = GameController.getInstance();
		Log.d(TAG, "playerDisconnected");
		gc.players.remove(username);
		gc.postInvalidate();
	}

	@Override
	public void downloadChanged(int bytes) throws RemoteException {
		GameController gc = GameController.getInstance();
		gc.networkDownload = bytes;
	}

	@Override
	public void uploadChanged(int bytes) throws RemoteException {
		GameController gc = GameController.getInstance();
		gc.networkUpload = bytes;
	}

	@Override
	public void positionChanged(String userName, int x, int y, int playerClass)
			throws RemoteException {
		GameController gc = GameController.getInstance();
		Player pl = gc.players.get(userName);
		if (pl == null) {
			pl = new Player(userName);
			pl.setMoveTileSet(new StringTileSet(
					PlayerClass.IMAGES[playerClass], 96, 96, 768, 768));
			pl.position.x = x;
			pl.position.y = y;
			gc.players.put(userName, pl);
			pl.startMoverThread();
		}
		if (!pl.destination().equals(new Position(x, y))) {
			pl.destroy();
			pl = new Player(userName);
			pl.setPlayerClass(playerClass);
			pl.moveTileSet = new StringTileSet(
					PlayerClass.IMAGES[pl.getPlayerClass()], 96, 96, 768, 768);
			pl.position = new Position(x, y);
			pl.startMoverThread();
		}
		gc.postInvalidate();

	}

	@Override
	public void playerInfoReciaved(int playerClass, String zone, int x, int y)
			throws RemoteException {
		GameController gc = GameController.getInstance();
		if (gc.user.moveTileSet == null) {
			// ResourceLoader.emptyBitmapCache();
			// System.gc();
			gc.user.setMoveTileSet(new StringTileSet(
					PlayerClass.IMAGES[playerClass], 96, 96, 768, 768));
		}
		gc.user.setPlayerClass(playerClass);
		if (gc.zone == null || !gc.zone.name.equals(zone)) {
			if (!gc.zoneLoading) {
				new ZoneLoaderThread().load(zone, x, y);
			}
			// _performLoad(zone, x, y);
		}

	}

	@Override
	public void playerPositionsMessageRecieaved(String[] userNames, int[] x,
			int[] y, int[] playerClass) throws RemoteException {
		GameController gc = GameController.getInstance();
		Log.d(TAG, "playerPositionsMessageRecieaved");

		for (int i = 0; i < userNames.length; i++) {
			Player pl = gc.players.get(userNames[i]);
			if (pl == null) {
				pl = new Player(userNames[i]);
				pl.setPlayerClass(playerClass[i]);
				pl.moveTileSet = new StringTileSet(
						PlayerClass.IMAGES[pl.getPlayerClass()], 96, 96, 768,
						768);
				pl.position = new Position(x[i], y[i]);
				gc.players.put(pl.name, pl);
				pl.startMoverThread();
			} else {
				if (!pl.destination().equals(new Position(x[i], y[i]))) {
					pl.destroy();
					pl = new Player(userNames[i]);
					pl.setPlayerClass(playerClass[i]);
					pl.moveTileSet = new StringTileSet(
							PlayerClass.IMAGES[pl.getPlayerClass()], 96, 96,
							768, 768);
					pl.position = new Position(x[i], y[i]);
					gc.players.put(pl.name, pl);
					pl.startMoverThread();
				}
			}
		}
		gc.players.clean();

	}
}
