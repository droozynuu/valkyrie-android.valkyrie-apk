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

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firegnom.valkyrie.R;
import com.firegnom.valkyrie.action.ActionManager;
import com.firegnom.valkyrie.action.ContextAction;
import com.firegnom.valkyrie.common.Dir;
import com.firegnom.valkyrie.common.PlayerClass;
import com.firegnom.valkyrie.graphics.DirectionalAnimation;
import com.firegnom.valkyrie.graphics.DrawableFeature;
import com.firegnom.valkyrie.graphics.Hit;
import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.map.StringTileSet;
import com.firegnom.valkyrie.map.Zone;
import com.firegnom.valkyrie.map.pathfinding.Mover;
import com.firegnom.valkyrie.map.pathfinding.ParcelablePath;
import com.firegnom.valkyrie.map.pathfinding.Path;
import com.firegnom.valkyrie.net.protocol.PlayerStats;

public class Player implements Mover, Comparable<Player>, DrawableFeature {
	private String TAG = Player.class.getName();
	public String name;
	public String displayName;
	public Position position;
	public PlayerStats stats;

	public DirectionalAnimation animation;

	public Position fightPosition = new Position(0, 0);

	public double fightMoveX = 0;
	public double fightMoveY = 0;

	public LinkedBlockingQueue<Path> paths;

	private int playerClass;

	public static Paint healthPaint;
	public static Paint healthPaintOut;
	public static Paint ghost;

	public boolean fightMode = false;

	static {
		healthPaint = new Paint();
		healthPaint.setColor(0x80ff0000);
		healthPaint.setStyle(Paint.Style.FILL);
		healthPaint.setDither(false);
		healthPaint.setAntiAlias(false);
		healthPaint.setFilterBitmap(false);

		healthPaintOut = new Paint();
		healthPaintOut.setColor(0x80ff0000);
		healthPaintOut.setStrokeWidth(1);
		healthPaintOut.setStyle(Paint.Style.STROKE);
		healthPaintOut.setDither(false);
		healthPaintOut.setAntiAlias(false);
		healthPaintOut.setFilterBitmap(false);

		ghost = new Paint();
		ghost.setAlpha(100);
	}

	// public static final short DIR_E = 0;
	// public static final short DIR_N = 1;
	// public static final short DIR_NE = 2;
	// public static final short DIR_NW = 3;
	// public static final short DIR_S = 4;
	// public static final short DIR_SE = 5;
	// public static final short DIR_SW = 6;
	// public static final short DIR_W = 7;
	//
	// public static final short [][] DIR_MATRIX = {
	// {DIR_SE,DIR_E,DIR_NE},
	// {DIR_S,/*TODO*/DIR_N,DIR_N},
	// {DIR_SW,DIR_W,DIR_NW}
	// };

	public static int MOVE_SPEED = 80;
	public short direction;
	public int pictureSize = 96;
	public int pictureXoffset = -pictureSize + 45;
	public int pictureYoffset = -pictureSize + 20;

	public StringTileSet moveTileSet;
	// public Bitmap[][] movesTiles;
	public MoverThread moverThread;
	public short move_position = 0;
	Position position_trans;
	// ResourceLoader rl ;

	ArrayList<ContextAction> actions;

	public Player(String name) {
		paths = new LinkedBlockingQueue<Path>(10);
		this.name = name;
		position = new Position();
		position_trans = new Position(0, 0);
		direction = Dir.E;
		actions = new ArrayList<ContextAction>();
		ContextAction info = new ContextAction();
		info.container = name;
		info.name = "Info";
		info.type = ContextAction.ACTION_MANAGER;
		info.actionId = ActionManager.INFO;
		actions.add(info);

		ContextAction attack = new ContextAction();
		attack.container = name;
		attack.name = "Attack";
		attack.type = ContextAction.ACTION_MANAGER;
		attack.actionId = ActionManager.ATTACK;
		actions.add(attack);

		// temporary
		h.setText("Miss");
	}

	public void setPlayerClass(int playerClass) {
		this.playerClass = playerClass;
		// TODO Auto-generated method stub
		animation = new DirectionalAnimation(PlayerClass.IMAGES[playerClass],
				(short) 8, 96, 96, 0, 0);
	}

	public int getPlayerClass() {
		return playerClass;
	}

	public void startMoverThread() {
		moverThread = new MoverThread(this, false);
		moverThread.start();
	}

	private Position onScreenPosition = new Position();

	public Position getOnscreenPosition(Zone z) {
		onScreenPosition.x = z.getXCoords(this.position.x) + pictureXoffset
				- position_trans.x;
		onScreenPosition.y = z.getYCoords(this.position.y) + pictureYoffset
				- position_trans.y;
		return onScreenPosition;
	}

	public Position getOnscreenPositionCenter(Zone z) {
		if (z == null) {
			Log.w(TAG, "getOnscreenPositionCenter no zone loaded");
			return null;
		}
		Position p = getOnscreenPosition(z);
		p.x += pictureSize / 2;
		p.y += pictureSize / 2;
		return p;
	}

	public Path getPathTo(int x, int y, GameController sc) {
		return sc.zone.finder.findPath(this, position.x, position.y, x, y);
	}

	public Bitmap getPicture() {
		// if (moverThread.moveing){
		return moveTileSet.getBitmap(move_position, direction,
				GameController.getInstance().rl);
		// ResourceLoader.emptyBitmapCache();
		// return movesTiles[direction][move_position];
		// }
		// return null;

	}

	public Path goTo(int x, int y, View v) {
		int cX = position.x, cY = position.y;
		if (moverThread.isMoveing()) {
			return null;
		}
		if (!paths.isEmpty()) {
			return null;
		}
		if (moverThread.p != null) {
			cX = moverThread.p.getX(moverThread.p.getLength() - 1);
			cY = moverThread.p.getY(moverThread.p.getLength() - 1);
		}

		GameController sc = GameController.getInstance();
		Path p = sc.zone.finder.findPath(this, cX, cY, x, y);
		Log.d(TAG, "Player go to path");
		if (p == null) {
			Toast.makeText(
					v.getContext(),
					v.getResources().getString(
							R.string.pathfinding_how_to_get_there),
					Toast.LENGTH_SHORT).show();
			;
			return null;
		}
		try {
			paths.put(p);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			sc.service.move(new ParcelablePath(p), playerClass);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return p;
	}

	public void setMoveTileSet(StringTileSet tilestet) {
		this.moveTileSet = tilestet;
		// tilestet.downloadAll(rl);
	}

	public void setPosition(int x, int y) {
		synchronized (position) {
			position.x = (short) x;
			position.y = (short) y;
		}
	}

	public void setFightPosition(int x, int y) {
		synchronized (fightPosition) {

			animation.getBitmap().setX(x);
			animation.getBitmap().setY(y);

			fightPosition.x = (short) x;
			fightPosition.y = (short) y;
		}
	}

	public void showMe(GameController sc) {
		sc.sX = -getOnscreenPosition(sc.zone).x + sc.screenWidth / 2
				- pictureSize / 2;
		sc.sY = -getOnscreenPosition(sc.zone).y + sc.screenHeight / 2
				- pictureSize / 2;
		sc.renderScreen();
	}

	public Position getMovePositions() {
		if (moverThread != null && moverThread.isMoveing()) {
			return new Position(moverThread.getPath().getX(
					moverThread.getPath().getLength() - 1), moverThread
					.getPath().getY(moverThread.getPath().getLength() - 1));
		} else {
			return null;
		}
	}

	/**
	 * Use only to stop players not user !!!
	 */
	public void destroy() {
		moverThread.interrupt();
	}

	public Position destination() {
		if (moverThread.destination == null) {
			return position;
		}
		return moverThread.destination;
	}

	@Override
	public int compareTo(Player another) {
		return position.y - another.position.y;
	}

	public ArrayList<ContextAction> getActions() {
		return actions;
	}

	Hit h = new Hit();

	@Override
	public void draw(Canvas c) {

		GameController gc = GameController.getInstance();
		Position p;
		if (fightMode) {
			p = gc.fightController.getScreenPosition(this);
			p.x += fightMoveX;
			p.y += fightMoveY;
		} else {
			p = getOnscreenPosition(gc.zone);
		}
		if (p == null) {
			return;
		}
		// draw name
		c.drawText(name, p.x + 15, p.y + 15, gc.playerLabel);

		if (fightMode) {
			// draw health
			c.drawRect(p.x + 10, p.y + 15, p.x + 14, p.y + 85, healthPaintOut);
			c.drawRect(p.x + 10, p.y + 15, p.x + 14, p.y + 85, healthPaint);

			animation.setX((int) (p.x)).setY((int) (p.y)).draw(c);

		} else {

			Bitmap playerImage = getPicture();
			if (playerImage == null) {
				c.drawRect(p.x, p.y, p.x + 96, p.y + 96, gc.outOfMemoryPaint);
			} else {
				c.drawBitmap(playerImage, p.x, p.y, null);
			}
		}

		// h.setPosition(getOnscreenPositionCenter(gc.zone));
		// h.draw(c);

	}

	@Override
	public Rect getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

}
