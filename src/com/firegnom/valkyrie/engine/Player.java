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

// TODO: Auto-generated Javadoc
/**
 * The Class Player.
 */
public class Player implements Mover, Comparable<Player>, DrawableFeature {
	
	/** The TAG. */
	private final String TAG = Player.class.getName();
	
	/** The name. */
	public String name;
	
	/** The display name. */
	public String displayName;
	
	/** The position. */
	public Position position;
	
	/** The stats. */
	public PlayerStats stats;

	/** The animation. */
	public DirectionalAnimation animation;

	/** The fight position. */
	public Position fightPosition = new Position(0, 0);

	/** The fight move x. */
	public double fightMoveX = 0;
	
	/** The fight move y. */
	public double fightMoveY = 0;

	/** The paths. */
	public LinkedBlockingQueue<Path> paths;

	/** The player class. */
	private int playerClass;

	/** The health paint. */
	public static Paint healthPaint;
	
	/** The health paint out. */
	public static Paint healthPaintOut;
	
	/** The ghost. */
	public static Paint ghost;

	/** The fight mode. */
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

	/** The MOV e_ speed. */
	public static int MOVE_SPEED = 80;
	
	/** The direction. */
	public short direction;
	
	/** The picture size. */
	public int pictureSize = 96;
	
	/** The picture xoffset. */
	public int pictureXoffset = -pictureSize + 45;
	
	/** The picture yoffset. */
	public int pictureYoffset = -pictureSize + 20;

	/** The move tile set. */
	public StringTileSet moveTileSet;
	// public Bitmap[][] movesTiles;
	/** The mover thread. */
	public MoverThread moverThread;
	
	/** The move_position. */
	public short move_position = 0;
	
	/** The position_trans. */
	Position position_trans;
	// ResourceLoader rl ;

	/** The actions. */
	ArrayList<ContextAction> actions;

	/** The on screen position. */
	private final Position onScreenPosition = new Position();

	/** The h. */
	Hit h = new Hit();

	/**
	 * Instantiates a new player.
	 *
	 * @param name the name
	 */
	public Player(final String name) {
		paths = new LinkedBlockingQueue<Path>(10);
		this.name = name;
		position = new Position();
		position_trans = new Position(0, 0);
		direction = Dir.E;
		actions = new ArrayList<ContextAction>();
		final ContextAction info = new ContextAction();
		info.container = name;
		info.name = "Info";
		info.type = ContextAction.ACTION_MANAGER;
		info.actionId = ActionManager.INFO;
		actions.add(info);

		final ContextAction attack = new ContextAction();
		attack.container = name;
		attack.name = "Attack";
		attack.type = ContextAction.ACTION_MANAGER;
		attack.actionId = ActionManager.ATTACK;
		actions.add(attack);

		// temporary
		h.setText("Miss");
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Player another) {
		return position.y - another.position.y;
	}

	/**
	 * Destination.
	 *
	 * @return the position
	 */
	public Position destination() {
		if (moverThread.destination == null) {
			return position;
		}
		return moverThread.destination;
	}

	/**
	 * Use only to stop players not user !!!.
	 */
	public void destroy() {
		moverThread.interrupt();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.graphics.DrawableFeature#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(final Canvas c) {

		final GameController gc = GameController.getInstance();
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

			animation.setX((p.x)).setY((p.y)).draw(c);

		} else {

			final Bitmap playerImage = getPicture();
			if (playerImage == null) {
				c.drawRect(p.x, p.y, p.x + 96, p.y + 96, gc.outOfMemoryPaint);
			} else {
				c.drawBitmap(playerImage, p.x, p.y, null);
			}
		}

		// h.setPosition(getOnscreenPositionCenter(gc.zone));
		// h.draw(c);

	}

	/**
	 * Gets the actions.
	 *
	 * @return the actions
	 */
	public ArrayList<ContextAction> getActions() {
		return actions;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.graphics.DrawableFeature#getBounds()
	 */
	@Override
	public Rect getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the move positions.
	 *
	 * @return the move positions
	 */
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
	 * Gets the onscreen position.
	 *
	 * @param z the z
	 * @return the onscreen position
	 */
	public Position getOnscreenPosition(final Zone z) {
		onScreenPosition.x = z.getXCoords(this.position.x) + pictureXoffset
				- position_trans.x;
		onScreenPosition.y = z.getYCoords(this.position.y) + pictureYoffset
				- position_trans.y;
		return onScreenPosition;
	}

	/**
	 * Gets the onscreen position center.
	 *
	 * @param z the z
	 * @return the onscreen position center
	 */
	public Position getOnscreenPositionCenter(final Zone z) {
		if (z == null) {
			Log.w(TAG, "getOnscreenPositionCenter no zone loaded");
			return null;
		}
		final Position p = getOnscreenPosition(z);
		p.x += pictureSize / 2;
		p.y += pictureSize / 2;
		return p;
	}

	/**
	 * Gets the path to.
	 *
	 * @param x the x
	 * @param y the y
	 * @param sc the sc
	 * @return the path to
	 */
	public Path getPathTo(final int x, final int y, final GameController sc) {
		return sc.zone.finder.findPath(this, position.x, position.y, x, y);
	}

	/**
	 * Gets the picture.
	 *
	 * @return the picture
	 */
	public Bitmap getPicture() {
		// if (moverThread.moveing){
		return moveTileSet.getBitmap(move_position, direction,
				GameController.getInstance().rl);
		// ResourceLoader.emptyBitmapCache();
		// return movesTiles[direction][move_position];
		// }
		// return null;

	}

	/**
	 * Gets the player class.
	 *
	 * @return the player class
	 */
	public int getPlayerClass() {
		return playerClass;
	}

	/**
	 * Go to.
	 *
	 * @param x the x
	 * @param y the y
	 * @param v the v
	 * @return the path
	 */
	public Path goTo(final int x, final int y, final View v) {
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

		final GameController sc = GameController.getInstance();
		final Path p = sc.zone.finder.findPath(this, cX, cY, x, y);
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
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			sc.service.move(new ParcelablePath(p), playerClass);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return p;
	}

	/**
	 * Sets the fight position.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setFightPosition(final int x, final int y) {
		synchronized (fightPosition) {

			animation.getBitmap().setX(x);
			animation.getBitmap().setY(y);

			fightPosition.x = (short) x;
			fightPosition.y = (short) y;
		}
	}

	/**
	 * Sets the move tile set.
	 *
	 * @param tilestet the new move tile set
	 */
	public void setMoveTileSet(final StringTileSet tilestet) {
		this.moveTileSet = tilestet;
		// tilestet.downloadAll(rl);
	}

	/**
	 * Sets the player class.
	 *
	 * @param playerClass the new player class
	 */
	public void setPlayerClass(final int playerClass) {
		this.playerClass = playerClass;
		// TODO Auto-generated method stub
		animation = new DirectionalAnimation(PlayerClass.IMAGES[playerClass],
				(short) 8, 96, 96, 0, 0);
	}

	/**
	 * Sets the position.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setPosition(final int x, final int y) {
		synchronized (position) {
			position.x = (short) x;
			position.y = (short) y;
		}
	}

	/**
	 * Show me.
	 *
	 * @param sc the sc
	 */
	public void showMe(final GameController sc) {
		sc.sX = -getOnscreenPosition(sc.zone).x + sc.screenWidth / 2
				- pictureSize / 2;
		sc.sY = -getOnscreenPosition(sc.zone).y + sc.screenHeight / 2
				- pictureSize / 2;
		sc.renderScreen();
	}

	/**
	 * Start mover thread.
	 */
	public void startMoverThread() {
		moverThread = new MoverThread(this, false);
		moverThread.start();
	}

}
