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

// TODO: Auto-generated Javadoc
/**
 * The Class GameController.
 */
public class GameController {
	
	/**
	 * Gets the single instance of GameController.
	 *
	 * @return single instance of GameController
	 */
	public static GameController getInstance() {
		if (me == null) {
			me = new GameController();
		}
		return me;
	}

	/** The zone. */
	public Zone zone;
	
	/** The user. */
	public User user;

	/** The players. */
	public PlayersTable players;

	/** The fight controller. */
	public FightController fightController;
	
	/** The service. */
	public IGameService service = null;

	/** The login service. */
	public ILoginService loginService = null;
	
	/** The s y. */
	public int sX = 0, sY = 0;
	
	/** The screen width. */
	public int screenWidth;

	/** The screen height. */
	public int screenHeight;
	// stats
	/** The network upload. */
	static double networkUpload = 0;

	/** The network download. */
	static double networkDownload = 0;
	
	/** The background. */
	private Picture background;
	
	/** The m paint. */
	private final Paint mPaint;
	
	/** The out of memory paint. */
	public Paint outOfMemoryPaint;

	/** The player label. */
	Paint playerLabel = new Paint();
	
	/** The pa. */
	android.graphics.Path pa = new android.graphics.Path();
	
	/** The long press position. */
	public Position longPressPosition;
	
	/** The long press position map. */
	public Position longPressPositionMap;
	
	/** The parser. */
	public ParserImpl parser;
	
	/** The zone loading. */
	public boolean zoneLoading = false;
	// if 0 no limit
	/** The limit scroll dist. */
	public static int limitScrollDist = 3;

	/** The rendering. */
	private boolean rendering = false;
	
	/** The disable input. */
	public static boolean disableInput = false;
	
	/** The disable scroll. */
	public static boolean disableScroll = false;

	/** The disable context. */
	public static boolean disableContext = false;
	// preferences
	/** The follow player. */
	public static boolean followPlayer = false;
	
	/** The SHO w_ mov e_ matrix. */
	public static boolean SHOW_MOVE_MATRIX = false;
	
	/** The global chat. */
	public static boolean globalChat = false;
	// performance
	/** The sort players. */
	public static boolean sortPlayers = true;
	
	/** The draw range. */
	public static boolean drawRange = true;

	/** The draw stats. */
	public static boolean drawStats = false;
	
	/** The change move images. */
	public static boolean changeMoveImages = true;
	
	/** The jumpy moves. */
	public static boolean jumpyMoves = false;

	/** The full invalidate. */
	public static boolean fullInvalidate = true;
	
	/** The Constant TAG. */
	private static final String TAG = "GameController";

	/** The me. */
	private static GameController me;
	
	/** The view. */
	public View view;

	/** The context. */
	public Context context;
	
	/** The finishing. */
	public boolean finishing = false;
	// TileSet wait;
	/** The remaining images. */
	private int remainingImages = 0;

	/** The rl. */
	public ResourceLoader rl;
	
	/** The papks loading. */
	public boolean papksLoading = false;
	
	/** The download packages. */
	public boolean downloadPackages = true;
	
	/** The packs downloading. */
	public boolean packsDownloading = false;

	/** The Constant viewRange. */
	public static final int viewRange = (int) (Constants.VISIBILITY_RANGE * 0.8);
	
	/** The gui. */
	public Gui gui;

	/** The action manager. */
	public ActionManager actionManager;

	/** The resource loader service. */
	public IResourceLoaderService resourceLoaderService;

	/** The resource loader connection. */
	private final ServiceConnection resourceLoaderConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(final ComponentName name,
				final IBinder service) {
			Log.d(TAG, "resourceLoaderService onServiceConnected");
			resourceLoaderService = IResourceLoaderService.Stub
					.asInterface(service);
			rl = new ResourceLoader(resourceLoaderService);
			ImageManager.getInstance().setResourceLoader(rl);
			try {
				resourceLoaderService
						.registerQueuleChangeListener(downloadListener);
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(final ComponentName name) {
			Log.d(TAG, "resourceLoaderService onServiceDisconnected");
			resourceLoaderService = null;
		}

	};

	/** The game service connection. */
	private final ServiceConnection gameServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName className,
				final IBinder service) {
			Log.d(TAG, "Game controler connected to game service");
			GameController.this.service = IGameService.Stub
					.asInterface(service);
			try {
				GameController.this.service
						.registerCallback(iGameServiceCallback);
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (user == null) {
				try {
					user = new User(GameController.this.service.getUsername());
				} catch (final RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					GameController.this.service.requestPlayerInfo();
				} catch (final RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			changeGamemode(GameModes.MAP_MODE);

		}

		@Override
		public void onServiceDisconnected(final ComponentName className) {
			Log.d(TAG, "gameServiceConnection onServiceDisconnected");
			// goToLogin();
			service = null;
			if (context == null) {
				return;
			}

			// do something

		}
	};
	
	/** The login service connection. */
	private final ServiceConnection loginServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName className,
				final IBinder service) {
			Log.d(TAG, "loginServiceConnection onServiceConnected");
			GameController.this.loginService = ILoginService.Stub
					.asInterface(service);
			try {
				if (!GameController.this.loginService.isLoggedIn()) {
					goToLogin();
				}
			} catch (final RemoteException e) {
				throw new ValkyrieRuntimeException(e);
			}

		}

		@Override
		public void onServiceDisconnected(final ComponentName className) {
			Log.d(TAG, "loginServiceConnection onServiceDisconnected");
			GameController.this.loginService = null;
		}
	};

	/** The i game service callback. */
	IGameServiceCallback.Stub iGameServiceCallback;

	/** The last render. */
	long lastRender = 0;

	/** The wait_pos. */
	int wait_pos = 0;

	/** The format. */
	NumberFormat format = NumberFormat.getInstance();

	/** The download listener. */
	IQueuleChangeListener.Stub downloadListener = new IQueuleChangeListener.Stub() {

		@Override
		public void remaining(final int r) throws RemoteException {
			remainingImages = r;
			// if (r == 0&&!rendering&&System.currentTimeMillis()-lastRender
			// >500){
			// renderScreen();
			// }
			postInvalidate();
		}
	};

	/**
	 * Instantiates a new game controller.
	 */
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

	/**
	 * _perform load.
	 *
	 * @param name the name
	 * @param x the x
	 * @param y the y
	 */
	public void _performLoad(final String name, final int x, final int y) {
		Log.d(TAG, "jestem");
		// ((GameActivity)context).setContentView(R.layout.please_wait);
		disableContext = true;
		disableInput = true;
		disableScroll = true;
		final ZoneLoader zl = new TiledZoneLoader(context);
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

	/**
	 * Bind services.
	 *
	 * @param c the c
	 */
	public void bindServices(final Context c) {
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

	/**
	 * Change gamemode.
	 *
	 * @param mode the mode
	 */
	public void changeGamemode(final int mode) {
		if (service == null) {
			Log.d(TAG, "FAILED - changeGamemode :" + mode);
			return;
		}
		Log.d(TAG, "changeGamemode :" + mode);
		try {
			service.changeGameMode(mode);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Connect view.
	 *
	 * @param view the view
	 * @param c the c
	 */
	public void connectView(final View view, final Context c) {
		this.view = view;
		this.context = c;
		final DisplayMetrics dm = new DisplayMetrics();
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

	/**
	 * Disconnect view.
	 */
	public void disconnectView() {

		this.view = null;
		this.context = null;
		pa.reset();

		System.gc();
	}

	/**
	 * Do draw.
	 *
	 * @param c the c
	 */
	synchronized public void doDraw(final Canvas c) {
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
				final List<Player> keyList = Collections.list(elements);
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

	/**
	 * Do scroll.
	 *
	 * @param distanceX the distance x
	 * @param distanceY the distance y
	 * @param view the view
	 * @return true, if successful
	 */
	public boolean doScroll(final float distanceX, final float distanceY,
			final View view) {
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
			final Position ppos = user.getOnscreenPosition(zone);
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

	/**
	 * Do single tap up.
	 *
	 * @param e the e
	 * @return true, if successful
	 */
	public boolean doSingleTapUp(final MotionEvent e) {
		if (zone == null) {
			Log.w(TAG, "doSingleTapUp dropped zone is null");
			return false;
		}
		if (disableInput) {
			return false;
		}
		final int x = (int) (((-1 * sX) + e.getX()) / zone.tileWidth * 2);
		final int y = (int) (((-1 * sY) + e.getY()) / zone.tileHeight * 2);

		final long startTime = System.currentTimeMillis();
		user.goTo(x, y, view);
		final long stopTime = System.currentTimeMillis();
		Log.d(TAG, "!!!!!!!!!!!!Position =  " + user.position
				+ " Calculated path in :" + ((stopTime - startTime) / 1000)
				+ " secondsand " + ((stopTime - startTime) % 1000)
				+ " miliseconds");
		if (view != null) {
			view.invalidate();
		}
		return true;
	}

	/**
	 * Draw global chat.
	 *
	 * @param c the c
	 */
	private void drawGlobalChat(final Canvas c) {
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

	/**
	 * Draw range.
	 *
	 * @param c the c
	 */
	private void drawRange(final Canvas c) {
		fullInvalidate = true;
		final Position p = user.getOnscreenPositionCenter(zone);
		c.drawOval(new RectF(p.x - viewRange * 32, p.y - viewRange * 24, p.x
				+ viewRange * 32, p.y + viewRange * 24), mPaint);

	}

	/**
	 * Draw stats.
	 *
	 * @param c the c
	 */
	public void drawStats(final Canvas c) {
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

	/**
	 * Exit.
	 *
	 * @param context the context
	 */
	public void exit(final Context context) {
		finishing = true;
		context.stopService(new Intent(ILoginService.class.getName()));
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Gets the string.
	 *
	 * @param resId the res id
	 * @return the string
	 */
	public String getString(final int resId) {
		return context.getString(resId);
	}

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	/**
	 * Go to login.
	 */
	void goToLogin() {
		if (finishing || context == null) {
			return;
		}
		Log.d(TAG, "goToLogin");
		// un(context);
		this.zone = null;
		this.user = null;
		final Intent i = new Intent(context, LoginActivity.class);
		((GameActivity) context).startActivityForResult(i, 0);
		((GameActivity) context).finish();
	}

	/**
	 * On long press.
	 *
	 * @param e the e
	 */
	public void onLongPress(final MotionEvent e) {

		final int x = (int) (((-1 * sX) + e.getX()) / zone.tileWidth * 2);
		final int y = (int) (((-1 * sY) + e.getY()) / zone.tileHeight * 2);
		longPressPosition = new Position((int) e.getX(), (int) e.getY());
		longPressPositionMap = new Position(x, y);
		((GameActivity) context).showDialog(GameActivity.DIALOG_CONTEXT_ACTION);
		Log.d(TAG, "onLongPress :x=" + e.getX() + ",y=" + e.getY());

	}

	/**
	 * Post invalidate.
	 */
	public void postInvalidate() {

		if (view == null) {
			return;
		}
		view.postInvalidate();
	}

	/**
	 * Post invalidate.
	 *
	 * @param i the i
	 */
	public void postInvalidate(final int i) {
		if (view == null) {
			return;
		}
		view.postInvalidateDelayed(i);
	}

	/**
	 * Post invalidate scroll.
	 *
	 * @param rLeft the r left
	 * @param rTop the r top
	 * @param rRight the r right
	 * @param rBottom the r bottom
	 */
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

	/**
	 * Reload preferences.
	 */
	public void reloadPreferences() {
		Log.d(TAG, "reloadPreferences");
		final SharedPreferences pref = PreferenceManager
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

	/**
	 * Removes the downloads.
	 */
	public void removeDownloads() {
		new ResourceLoader().emptyCache();
	}

	/**
	 * Render screen.
	 */
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

	/**
	 * Sets the screen size.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void setScreenSize(final int width, final int height) {
		final DisplayMetrics dm = new DisplayMetrics();
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

	/**
	 * Toogle move matrix.
	 */
	public void toogleMoveMatrix() {
		SHOW_MOVE_MATRIX = !SHOW_MOVE_MATRIX;
		renderScreen();
		view.invalidate();
	}

	/**
	 * Unbind services.
	 *
	 * @param context the context
	 */
	public void unbindServices(final Context context) {
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

}
