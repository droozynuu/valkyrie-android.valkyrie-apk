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

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.firegnom.valkyrie.FightActivity;
import com.firegnom.valkyrie.GameActivity;
import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.Gui;
import com.firegnom.valkyrie.engine.Player;
import com.firegnom.valkyrie.graphics.DrawableFeature;
import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.util.ResourceLoader;

public class FightController {
	FightMap map;
	ScheduledThreadPoolExecutor executor;

	protected static final String TAG = FightController.class.getName();
	boolean canMove = false;
	int screenWidth = 0;
	int screenHeight = 0;
	Paint paint;
	Picture background = null;
	Picture range = null;
	int limitScrollDist = 30;
	public FightAction action;

	int pictureSize = 96;
	private float sX;
	private float sY;
	TurnThread turnThread;
	ArrayList<DrawableFeature> features;

	View view;
	Context context;

	public Player enemy;
	private int timeLeft = 1000;

	public void connect(View v, Context c) {
		view = v;
		context = c;
	}

	public void init() {
		if (map == null) {
			map = new FightMap();
		}

		turnThread.startTurn(new ProgressObserver() {
			@Override
			public void progressChanged(int progress) {
				// Log.d(TAG,"progress changed :"+progress);
				((FightActivity) context).progress.setProgress(progress);
				timeLeft = progress;
			}
		}, timeLeft);
	}

	public void finish() {
	}

	public void disconnect() {
		view = null;
		context = null;
	}

	public FightController() {
		features = new ArrayList<DrawableFeature>();
		turnThread = new TurnThread();
		paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setAlpha(100);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);
		paint.setDither(false);
		paint.setStyle(Paint.Style.FILL);
		executor = new ScheduledThreadPoolExecutor(1);
	}

	public Position getScreenPosition(Player user) {
		Position p = user.fightPosition;
		Position ret = new Position();
		ret.x = ((p.x * map.tileWidth) + map.tileWidth / 2)
				+ user.pictureXoffset;
		ret.y = ((p.y * map.tileHeight) + map.tileHeight / 2)
				+ user.pictureYoffset;
		return ret;
	}

	public void doDraw(Canvas canvas) {
		Player user = GameController.getInstance().user;
		if (background == null) {
			renderBackground();
		}
		canvas.translate(sX, sY);
		background.draw(canvas);
		if (range != null) {
			range.draw(canvas);
		}
		// Position p = getScreenPosition(user);
		for (DrawableFeature feature : features) {
			feature.draw(canvas);
		}
		// canvas.drawPath(path, paint)

		user.draw(canvas);
		enemy.draw(canvas);
		// canvas.drawBitmap(GameController.getInstance().user.getPicture(),p.x,p.y,null);

	}

	public void prepareRange() {
		range = new Picture();
		Canvas canvas = range.beginRecording(3 * map.tileWidth,
				3 * map.tileHeight);
		Log.d(TAG, "finding range");
		Position fightPosition = GameController.getInstance().user.fightPosition;
		Set<Position> l = map.findRange(null, fightPosition.x, fightPosition.y,
				5);

		for (Position p : l) {
			canvas.drawRect(p.x * map.tileWidth, p.y * map.tileHeight, p.x
					* map.tileWidth + map.tileWidth, p.y * map.tileHeight
					+ map.tileHeight, paint);
		}

		// for (int x = 0;x < 3;x++){
		// for (int y = 0;y < 3;y++){
		// canvas.drawRect(x*map.tileWidth, y*map.tileHeight,
		// x*map.tileWidth+map.tileWidth, y*map.tileHeight+map.tileHeight,
		// paint);
		// }
		// }

		range.endRecording();
		view.invalidate();

	}

	private void renderBackground() {
		background = new Picture();
		ResourceLoader rl = new ResourceLoader();
		Bitmap image = rl.getBitmapResource("tileset2,7,4.png", true);
		Canvas c = background.beginRecording(screenWidth, screenHeight - 50);
		for (int i = 0; i < map.width; i++) {
			for (int j = 0; j < map.height; j++) {
				c.drawBitmap(image, i * map.tileWidth, j * map.tileHeight, null);
			}
		}

		// for (int j = 0; j < map.width; j++) {
		// c.drawLine(j*map.tileWidth, 0, j*map.tileWidth,
		// map.height*map.tileHeight, paint);
		// }
		//
		// for (int j = 0; j < map.height; j++) {
		// c.drawLine(0, j*map.tileHeight,
		// map.width*map.tileWidth,j*map.tileHeight, paint);
		// }

		background.endRecording();

	}

	public void setScreenSize(int w, int h) {
		screenWidth = w;
		screenHeight = h;
	}

	public boolean doScroll(float distanceX, float distanceY) {
		int xmin = 0 - 96, xmax = map.width * map.tileWidth + screenWidth + 96, ymin = 0 - 96, ymax = map.height
				* map.tileHeight + screenHeight + 96;
		if (limitScrollDist > 0) {
			// Position ppos = user.getOnscreenPosition(zone);
			Position ppos = new Position(0, 0);

			ppos.x -= 96 / 2;
			ppos.y -= 96 / 2;

			xmin = ppos.x - (limitScrollDist * map.tileWidth) - screenWidth / 2
					- pictureSize / 2;
			xmax = ppos.x + (limitScrollDist * map.tileWidth) + screenWidth / 2
					- pictureSize / 2;
			ymin = ppos.y - (limitScrollDist * map.tileHeight) - screenHeight
					/ 2 - pictureSize / 2;
			ymax = ppos.y + (limitScrollDist * map.tileHeight) + screenHeight
					/ 2 - pictureSize / 2;
		}

		if (-1 * (sX - distanceX) > xmin && -1 * (sX - distanceX) < xmax) {
			sX -= distanceX;
		} else if (-1 * (sX - distanceX) < xmin) {
			sX = -xmin;
		} else if (-1 * (sX - distanceX) > xmax) {
			sX = -xmax;
		}

		if (-1 * (sY - distanceY) > ymin && -1 * (sY - distanceY) < ymax) {
			sY -= distanceY;
		} else if (-1 * (sY - distanceY) < ymin) {
			sY = -ymin;
		} else if (-1 * (sY - distanceY) > ymax) {
			sY = -ymax;
		}
		view.invalidate();
		return true;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		int x = (int) (((-1 * sX) + e.getX()) / map.tileWidth);
		int y = (int) (((-1 * sY) + e.getY() - FightActivity.progressHeight) / map.tileHeight);
		if (action != null) {
			action.onSingleTapUp(x, y);
		}
		return true;
	}

	public void changeAction(FightAction action) {
		if (this.action != null) {
			if (this.action.isActive()) {
				Gui.toast("Other Action is already active", view, context);
				return;
			}
			this.action.deactivated();
		}
		this.action = action;
		this.action.activated();
	}

	public void exit() {
		sX = 0;
		sY = 0;
		action = null;
		features.clear();
		range = null;
		context.startActivity(new Intent(context, GameActivity.class));
		GameController.getInstance().user.fightMode = false;
		enemy.fightMode = false;
		((FightActivity) context).finish();
	}

	public void postInvalidateScroll(int rLeft, int rTop, int rRight,
			int rBottom) {
		if (view == null) {
			return;
		}
		rLeft += sX;
		rRight += sX;
		rBottom += sY;
		rTop += sY;
		view.postInvalidate(rLeft, rTop, rRight, rBottom);
	}

	public void postInvalidateScroll(Rect bounds) {
		postInvalidateScroll(bounds.left, bounds.top, bounds.right,
				bounds.bottom);
	}

	public void postInvalidate(int i) {
		if (view == null) {
			return;
		}
		view.postInvalidateDelayed(i);
	}

	public void postInvalidate() {
		if (view == null) {
			return;
		}
		view.postInvalidate();
	}

	public void postInvalidate(Rect bounds) {
		if (view == null) {
			return;
		}
		view.postInvalidate(bounds.left, bounds.top, bounds.right,
				bounds.bottom);
	}

	public void postInvalidate(Player pl) {
		if (view == null) {
			return;
		}
		Position pos = getScreenPosition(pl);
		postInvalidateScroll(pl.animation.getBitmap().setX(pos.x).setY(pos.y)
				.getBounds());

	}

}
