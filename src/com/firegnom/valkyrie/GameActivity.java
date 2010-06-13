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
package com.firegnom.valkyrie;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.service.IGameService;
import com.firegnom.valkyrie.util.PacksHelper;
import com.firegnom.valkyrie.util.ResourceLoader;
import com.firegnom.valkyrie.view.ContextActionDialog;
import com.firegnom.valkyrie.view.GameView;
import com.firegnom.valkyrie.view.ScriptEditorDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class GameActivity.
 */
public class GameActivity extends ValkyrieActivity implements
		OnGestureListener, OnDoubleTapListener {
	
	/** The Constant DIALOG_TEXT_ENTRY. */
	public static final int DIALOG_TEXT_ENTRY = 0;
	
	/** The Constant DIALOG_CONTEXT_ACTION. */
	public static final int DIALOG_CONTEXT_ACTION = 1;
	
	/** The Constant MESSAGE_CHARSET. */
	public static final String MESSAGE_CHARSET = "UTF-8";
	
	/** The g service. */
	IGameService gService = null;
	
	/** The Constant TAG. */
	protected static final String TAG = GameActivity.class.getName();
	
	/** The gesture scanner. */
	private GestureDetector gestureScanner;
	
	/** The m. */
	private GameView m;
	
	/** The gc. */
	GameController gc;

	/**
	 * Exit.
	 */
	private void exit() {
		gc.exit(GameActivity.this);
		finish();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.ValkyrieActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate action");
		gc = GameController.getInstance();
		if (gc.downloadPackages
				&& PacksHelper.packsAvilable(ResourceLoader.getPath())) {
			gc.papksLoading = true;
			startActivity(new Intent(this, PackageLoaderActivity.class));
			finish();
			return;
		}
		gc.packsDownloading = false;
		gc.papksLoading = false;
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		gestureScanner = new GestureDetector(this);
		m = new GameView(this.getApplicationContext());
		setContentView(m);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case DIALOG_TEXT_ENTRY:
			return new ScriptEditorDialog(GameActivity.this);
		case DIALOG_CONTEXT_ACTION:
			return new ContextActionDialog(GameActivity.this);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// menu.add(0, 5, 0, "DEV-Free memory");
		// menu.add(0, 4, 0, "DEV-Remove downloads");
		// menu.add(0, 3, 0, "Chat");
		menu.add(0, 2, 0, "Preferences");
		menu.add(0, 1, 0, "Exit");
		menu.add(0, 7, 0, "DEV-Scripting");
		// menu.add(0, 4, 0, "About");
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy action");
		gc.unbindServices(GameActivity.this);
		// if (isFinishing())Process.killProcess(Process.myPid());
		if (gc.finishing) {
			Process.killProcess(Process.myPid());
		}
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnDoubleTapListener#onDoubleTap(android.view.MotionEvent)
	 */
	@Override
	public boolean onDoubleTap(final MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnDoubleTapListener#onDoubleTapEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onDoubleTapEvent(final MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
	 */
	@Override
	public boolean onDown(final MotionEvent e) {
		try {
			Thread.sleep(16);
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.d(TAG, "onDown");
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onFling(final MotionEvent e1, final MotionEvent e2,
			final float velocityX, final float velocityY) {
		Log.d(TAG, "onFling");
		System.gc();
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
	 */
	@Override
	public void onLongPress(final MotionEvent e) {

		if (GameController.disableContext) {
			return;
		}
		gc.onLongPress(e);
		try {
			Thread.sleep(16);
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			exit();
			return true;
		case 2:
			startActivity(new Intent(this, Preferences.class));
			return true;
		case 3:
			startActivity(new Intent(this, Chat.class));
			finish();
			return true;
		case 4:
			gc.removeDownloads();
			return true;
		case 5:
			ResourceLoader.emptyBitmapCache();
			return true;
		case 7:
			showDialog(DIALOG_TEXT_ENTRY);

		}
		return false;

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause action");
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume action");
		if (gc.papksLoading) {
			return;
		}
		gc.connectView(m, GameActivity.this);
		m.invalidate();

	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onScroll(final MotionEvent e1, final MotionEvent e2,
			final float distanceX, final float distanceY) {
		return gc.doScroll(distanceX, distanceY, m);
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
	 */
	@Override
	public void onShowPress(final MotionEvent e) {
		try {
			Thread.sleep(16);
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.d(TAG, "onShowPress");
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnDoubleTapListener#onSingleTapConfirmed(android.view.MotionEvent)
	 */
	@Override
	public boolean onSingleTapConfirmed(final MotionEvent e) {
		try {
			Thread.sleep(16);
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 */
	@Override
	public boolean onSingleTapUp(final MotionEvent e) {
		try {
			Thread.sleep(16);
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return gc.doSingleTapUp(e);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart action");
		gc.bindServices(this);
		if (gc.papksLoading) {
			return;
		}
		gc.connectView(m, GameActivity.this);
		gc.reloadPreferences();

	}

	// @Override
	// protected void onRestart() {
	// //change game mode
	//
	//
	// }

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop action");
		gc.disconnectView();
		System.gc();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		return gestureScanner.onTouchEvent(event);
	}

}
