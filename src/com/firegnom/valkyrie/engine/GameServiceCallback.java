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

// TODO: Auto-generated Javadoc
/**
 * The Class GameServiceCallback.
 */
public class GameServiceCallback extends IGameServiceCallback.Stub {

	/** The TAG. */
	private final String TAG = GameServiceCallback.class.getName();

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IGameServiceCallback#disconnected()
	 */
	@Override
	public void disconnected() throws RemoteException {
		final GameController gc = GameController.getInstance();
		gc.goToLogin();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IGameServiceCallback#downloadChanged(int)
	 */
	@Override
	public void downloadChanged(final int bytes) throws RemoteException {
		final GameController gc = GameController.getInstance();
		GameController.networkDownload = bytes;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IGameServiceCallback#playerDisconnected(java.lang.String)
	 */
	@Override
	public void playerDisconnected(final String username)
			throws RemoteException {
		final GameController gc = GameController.getInstance();
		Log.d(TAG, "playerDisconnected");
		gc.players.remove(username);
		gc.postInvalidate();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IGameServiceCallback#playerInfoReciaved(int, java.lang.String, int, int)
	 */
	@Override
	public void playerInfoReciaved(final int playerClass, final String zone,
			final int x, final int y) throws RemoteException {
		final GameController gc = GameController.getInstance();
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

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IGameServiceCallback#playerMoved(java.lang.String, int, com.firegnom.valkyrie.map.pathfinding.ParcelablePath)
	 */
	@Override
	public void playerMoved(final String username, final int playerClass,
			final ParcelablePath pa) throws RemoteException {
		final GameController gc = GameController.getInstance();
		Log.d(TAG, "playerMoved");
		Player pl = gc.players.get(username);
		final Path p = pa.path;
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

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IGameServiceCallback#playerPositionsMessageRecieaved(java.lang.String[], int[], int[], int[])
	 */
	@Override
	public void playerPositionsMessageRecieaved(final String[] userNames,
			final int[] x, final int[] y, final int[] playerClass)
			throws RemoteException {
		final GameController gc = GameController.getInstance();
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

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IGameServiceCallback#positionChanged(java.lang.String, int, int, int)
	 */
	@Override
	public void positionChanged(final String userName, final int x,
			final int y, final int playerClass) throws RemoteException {
		final GameController gc = GameController.getInstance();
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

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IGameServiceCallback#uploadChanged(int)
	 */
	@Override
	public void uploadChanged(final int bytes) throws RemoteException {
		final GameController gc = GameController.getInstance();
		GameController.networkUpload = bytes;
	}
}
