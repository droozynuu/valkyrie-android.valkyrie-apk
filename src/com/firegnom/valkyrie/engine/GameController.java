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

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.firegnom.valkyrie.GameActivity;
import com.firegnom.valkyrie.LoginActivity;
import com.firegnom.valkyrie.Preferences;
import com.firegnom.valkyrie.action.ActionManager;
import com.firegnom.valkyrie.common.Constants;
import com.firegnom.valkyrie.engine.fight.FightController;
import com.firegnom.valkyrie.graphics.ImageManager;
import com.firegnom.valkyrie.graphics.StyledText;
import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.map.Zone;
import com.firegnom.valkyrie.map.ZoneLoader;
import com.firegnom.valkyrie.map.tiled.TiledZoneLoader;
import com.firegnom.valkyrie.scripting.impl.ParserImpl;
import com.firegnom.valkyrie.service.IGameService;
import com.firegnom.valkyrie.service.IGameServiceCallback;
import com.firegnom.valkyrie.service.ILoginService;
import com.firegnom.valkyrie.service.IQueuleChangeListener;
import com.firegnom.valkyrie.service.IResourceLoaderService;
import com.firegnom.valkyrie.share.constant.GameModes;
import com.firegnom.valkyrie.util.ResourceLoader;

public class GameController {
	public Zone zone;
	public User user;
	public PlayersTable players;

	public FightController fightController;

	public IGameService service = null;
	public ILoginService loginService = null;

	public int sX = 0, sY = 0;
	public int screenWidth;
	public int screenHeight;

	// stats
	static double networkUpload = 0;
	static double networkDownload = 0;

	private Picture background;
	private Paint mPaint;
	public Paint outOfMemoryPaint;
	Paint playerLabel = new Paint();

	android.graphics.Path pa = new android.graphics.Path();
	public Position longPressPosition;
	public Position longPressPositionMap;
	public ParserImpl parser;
	public boolean zoneLoading = false;
	// if 0 no limit
	public static int limitScrollDist = 3;
	private boolean rendering = false;

	public static boolean disableInput = false;
	public static boolean disableScroll = false;
	public static boolean disableContext = false;

	// preferences
	public static boolean followPlayer = false;
	public static boolean SHOW_MOVE_MATRIX = false;
	public static boolean globalChat = false;
	// performance
	public static boolean sortPlayers = true;
	public static boolean drawRange = true;
	public static boolean drawStats = false;

	public static boolean changeMoveImages = true;
	public static boolean jumpyMoves = false;
	public static boolean fullInvalidate = true;

	private static final String TAG = "GameController";
	private static GameController me;

	public View view;
	public Context context;

	public boolean finishing = false;
	// TileSet wait;
	private int remainingImages = 0;
	public ResourceLoader rl;

	public boolean papksLoading = false;
	public boolean downloadPackages = true;
	public boolean packsDownloading = false;
	public static final int viewRange = (int) (Constants.VISIBILITY_RANGE * 0.8);

	public Gui gui;
	public ActionManager actionManager;

	private GameController() {
		iGameServiceCallback = new GameServiceCallback();
		players = new PlayersTable();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(false);
		mPaint.setColor(0x6000FF00);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(10);

		outOfMemoryPaint = new Paint();
		outOfMemoryPaint.setAntiAlias(true);
		outOfMemoryPaint.setDither(false);
		outOfMemoryPaint.setColor(0x80ff8f00);
		outOfMemoryPaint.setStyle(Paint.Style.FILL);

		playerLabel.setAntiAlias(true);
		playerLabel.setColor(Color.WHITE);
		gui = new Gui();
		actionManager = new ActionManager();
		fightController = new FightController();

	}

	public void bindServices(Context c) {
		if (resourceLoaderService == null) {
			c.bindService(new Intent(IResourceLoaderService.class.getName()),
					resourceLoaderConnection, Context.BIND_AUTO_CREATE);
		}
		if (service == null) {
			c.bindService(new Intent(IGameService.class.getName()),
					gameServiceConnection, 0);
		}

		if (loginService == null) {
			c.bindService(new Intent(ILoginService.class.getName()),
					loginServiceConnection, 0);
		}

		if (parser == null) {
			parser = new ParserImpl();
		}
	}

	public void unbindServices(Context context) {
		changeGamemode(GameModes.BACKGROUND);
		Log.d(TAG, "unbindServices");
		if (resourceLoaderService != null) {
			context.unbindService(resourceLoaderConnection);
		}
		resourceLoaderService = null;
		if (service != null) {
			context.unbindService(gameServiceConnection);
		}
		service = null;
		if (loginService != null) {
			context.unbindService(loginServiceConnection);
		}
		loginService = null;

	}

	public IResourceLoaderService resourceLoaderService;
	private ServiceConnection resourceLoaderConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "resourceLoaderService onServiceConnected");
			resourceLoaderService = IResourceLoaderService.Stub
					.asInterface(service);
			rl = new ResourceLoader(resourceLoaderService);
			ImageManager.getInstance().setResourceLoader(rl);
			try {
				resourceLoaderService
						.registerQueuleChangeListener(downloadListener);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "resourceLoaderService onServiceDisconnected");
			resourceLoaderService = null;
		}

	};

	private ServiceConnection gameServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "Game controler connected to game service");
			GameController.this.service = IGameService.Stub
					.asInterface(service);
			try {
				GameController.this.service
						.registerCallback(iGameServiceCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (user == null) {
				try {
					user = new User(GameController.this.service.getUsername());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					GameController.this.service.requestPlayerInfo();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			changeGamemode(GameModes.MAP_MODE);

		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "gameServiceConnection onServiceDisconnected");
			// goToLogin();
			service = null;
			if (context == null) {
				return;
			}

			// do something

		}
	};

	private ServiceConnection loginServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "loginServiceConnection onServiceConnected");
			GameController.this.loginService = ILoginService.Stub
					.asInterface(service);
			try {
				if (!GameController.this.loginService.isLoggedIn()) {
					goToLogin();
				}
			} catch (RemoteException e) {
				throw new ValkyrieRuntimeException(e);
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "loginServiceConnection onServiceDisconnected");
			GameController.this.loginService = null;
		}
	};

	IGameServiceCallback.Stub iGameServiceCallback;

	void goToLogin() {
		if (finishing || context == null) {
			return;
		}
		Log.d(TAG, "goToLogin");
		// un(context);
		this.zone = null;
		this.user = null;
		Intent i = new Intent(context, LoginActivity.class);
		((GameActivity) context).startActivityForResult(i, 0);
		((GameActivity) context).finish();
	}

	public void connectView(View view, Context c) {
		this.view = view;
		this.context = c;
		DisplayMetrics dm = new DisplayMetrics();
		((GameActivity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		Log.d(TAG, "screen size screenWidth:" + screenWidth + " screenHeight:"
				+ screenHeight);
		if (zone == null) {
			return;
		}
		renderScreen();
	}

	public void disconnectView() {

		this.view = null;
		this.context = null;
		pa.reset();

		System.gc();
	}

	public void changeGamemode(int mode) {
		if (service == null) {
			Log.d(TAG, "FAILED - changeGamemode :" + mode);
			return;
		}
		Log.d(TAG, "changeGamemode :" + mode);
		try {
			service.changeGameMode(mode);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public static GameController getInstance() {
		if (me == null) {
			me = new GameController();
		}
		return me;
	}

	public void _performLoad(String name, int x, int y) {
		Log.d(TAG, "jestem");
		// ((GameActivity)context).setContentView(R.layout.please_wait);
		disableContext = true;
		disableInput = true;
		disableScroll = true;
		ZoneLoader zl = new TiledZoneLoader(context);
		zone = zl.load(name);
		user.setPosition(x, y);
		user.showMe(this);
		renderScreen();
		postInvalidate();
		disableContext = false;
		disableInput = false;
		disableScroll = false;
		// ((GameActivity)context).setContentView(view);
	}

	long lastRender = 0;

	synchronized public void renderScreen() {
		if (!rendering) {
			rendering = true;

			if (zone == null) {
				rendering = false;
				return;
			}

			background = zone.renderZone(sX, sY, screenWidth, screenHeight, rl);
			rendering = false;
			lastRender = System.currentTimeMillis();
		}
	}

	int wait_pos = 0;

	synchronized public void doDraw(Canvas c) {
		if (user == null) {
			return;
		}
		if (zone != null) {
			Position p;
			if (followPlayer) {
				user.showMe(this);
			}
			if (background == null) {
				renderScreen();
			}
			if (background == null) {
				return;
			}
			background.draw(c);
			// c.save();
			c.translate(sX, sY);

			p = user.getMovePositions();
			if (p != null) {
				c.drawPoint(zone.getXCoords(p.x), zone.getYCoords(p.y), mPaint);
			}

			// players
			Enumeration<Player> elements = players.elements();
			Bitmap playerImage;

			if (sortPlayers) {
				List<Player> keyList = Collections.list(elements);
				keyList.add(user);
				Collections.sort(keyList);
				elements = Collections.enumeration(keyList);
			} else {
				p = user.getOnscreenPosition(this.zone);
				playerImage = user.getPicture();
				if (playerImage == null) {
					c.drawRect(p.x, p.y, p.x + 96, p.y + 96, outOfMemoryPaint);
				} else {
					c.drawBitmap(playerImage, p.x, p.y, null);
				}
			}

			Player pl;
			while (elements.hasMoreElements()) {
				pl = elements.nextElement();
				if (user.position.inRange(pl.position, viewRange)) {
					pl.draw(c);
				}
			}
			if (drawRange) {
				drawRange(c);
			}
			// c.restore();
		}
		if (drawStats) {
			drawStats(c);
		}
		if (globalChat) {
			drawGlobalChat(c);
		}

	}

	private void drawGlobalChat(Canvas c) {
		// c.drawRect(0,0,screenWidth,120, outOfMemoryPaint);
		// Gui.drawLog(c);
		for (int i = 0; i < 10; i++) {
			// playerLabel.setAlpha((int)(100-1.4*i));
			new StyledText("firegnom: this is test " + i,
					0xffffffff - 0x10000000 * i, 10, 0 - sX, 10 + 10 * i - sY)
					.draw(c);
			// c.drawText("firegnom: this is test "+i , 0, 10+10*i,
			// playerLabel);
		}

	}

	private void drawRange(Canvas c) {
		fullInvalidate = true;
		Position p = user.getOnscreenPositionCenter(zone);
		c.drawOval(new RectF(p.x - viewRange * 32, p.y - viewRange * 24, p.x
				+ viewRange * 32, p.y + viewRange * 24), mPaint);

	}

	public void setScreenSize(int width, int height) {
		DisplayMetrics dm = new DisplayMetrics();
		((GameActivity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		Log.d(TAG, "screen size screenWidth:" + screenWidth + " screenHeight:"
				+ screenHeight);
		// screenWidth = width;
		// screenHeight = height;
		renderScreen();
	}

	NumberFormat format = NumberFormat.getInstance();

	public void drawStats(Canvas c) {
		format.setMaximumFractionDigits(2);
		StringBuffer text = new StringBuffer();
		text.append("Up:");
		text.append(format.format(networkUpload / 1024));
		text.append(" KB /D:");
		text.append(format.format(networkDownload / 1024));
		text.append(" KB");
		c.drawText(text, 0, text.length(), 0, 15, playerLabel);
		text = new StringBuffer();
		text.append("Images to download:");
		text.append(remainingImages);
		c.drawText(text, 0, text.length(), 0, 25, playerLabel);
	}

	public boolean doScroll(float distanceX, float distanceY, View view) {
		if (zone == null) {
			Log.w(TAG, "doScroll dropped : zone is null");
		}
		if (disableScroll) {
			return true;
		}

		int xmin = 0 - 96, xmax = zone.width * zone.tileWidth + screenWidth
				+ 96, ymin = 0 - 96, ymax = zone.height * zone.tileHeight
				+ screenHeight + 96;
		if (limitScrollDist > 0) {
			Position ppos = user.getOnscreenPosition(zone);
			ppos.x -= 96 / 2;
			ppos.y -= 96 / 2;

			xmin = ppos.x - (limitScrollDist * zone.tileWidth) - screenWidth
					/ 2 - user.pictureSize / 2;
			xmax = ppos.x + (limitScrollDist * zone.tileWidth) + screenWidth
					/ 2 - user.pictureSize / 2;
			ymin = ppos.y - (limitScrollDist * zone.tileHeight) - screenHeight
					/ 2 - user.pictureSize / 2;
			ymax = ppos.y + (limitScrollDist * zone.tileHeight) + screenHeight
					/ 2 - user.pictureSize / 2;
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

		renderScreen();
		view.invalidate();

		return true;
	}

	public boolean doSingleTapUp(MotionEvent e) {
		if (zone == null) {
			Log.w(TAG, "doSingleTapUp dropped zone is null");
			return false;
		}
		if (disableInput) {
			return false;
		}
		int x = (int) (((-1 * sX) + e.getX()) / zone.tileWidth * 2);
		int y = (int) (((-1 * sY) + e.getY()) / zone.tileHeight * 2);

		long startTime = System.currentTimeMillis();
		user.goTo(x, y, view);
		long stopTime = System.currentTimeMillis();
		Log.d(TAG, "!!!!!!!!!!!!Position =  " + user.position
				+ " Calculated path in :" + ((stopTime - startTime) / 1000)
				+ " secondsand " + ((stopTime - startTime) % 1000)
				+ " miliseconds");
		if (view != null) {
			view.invalidate();
		}
		return true;
	}

	public void toogleMoveMatrix() {
		SHOW_MOVE_MATRIX = !SHOW_MOVE_MATRIX;
		renderScreen();
		view.invalidate();
	}

	public void exit(Context context) {
		finishing = true;
		context.stopService(new Intent(ILoginService.class.getName()));
	}

	public void reloadPreferences() {
		Log.d(TAG, "reloadPreferences");
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SHOW_MOVE_MATRIX = pref.getBoolean(Preferences.IS_SHOW_MOVES, false);
		followPlayer = pref.getBoolean(Preferences.IS_FOLLOW_PLAYER, false);
		sortPlayers = !pref.getBoolean(Preferences.DISABLE_PLAYER_SORTING,
				false);
		drawRange = pref.getBoolean(Preferences.DRAW_VISIBILITY_RANGE, false);
		changeMoveImages = pref
				.getBoolean(Preferences.CHANGE_MOVE_IMAGES, true);
		jumpyMoves = pref.getBoolean(Preferences.JUMPY_MOVES, false);
		fullInvalidate = pref.getBoolean(Preferences.FULL_INVALIDATE, false);

	}

	public void removeDownloads() {
		new ResourceLoader().emptyCache();
	}

	public View getView() {
		return view;
	}

	public Context getContext() {
		return context;
	}

	IQueuleChangeListener.Stub downloadListener = new IQueuleChangeListener.Stub() {

		@Override
		public void remaining(int r) throws RemoteException {
			remainingImages = r;
			// if (r == 0&&!rendering&&System.currentTimeMillis()-lastRender
			// >500){
			// renderScreen();
			// }
			postInvalidate();
		}
	};

	public void onLongPress(MotionEvent e) {

		int x = (int) (((-1 * sX) + e.getX()) / zone.tileWidth * 2);
		int y = (int) (((-1 * sY) + e.getY()) / zone.tileHeight * 2);
		longPressPosition = new Position((int) e.getX(), (int) e.getY());
		longPressPositionMap = new Position(x, y);
		((GameActivity) context).showDialog(GameActivity.DIALOG_CONTEXT_ACTION);
		Log.d(TAG, "onLongPress :x=" + e.getX() + ",y=" + e.getY());

	}

	public String getString(int resId) {
		return context.getString(resId);
	}

}
