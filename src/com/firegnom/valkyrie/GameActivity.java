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

public class GameActivity extends ValkyrieActivity implements
		OnGestureListener, OnDoubleTapListener {
	public static final int DIALOG_TEXT_ENTRY = 0;
	public static final int DIALOG_CONTEXT_ACTION = 1;
	public static final String MESSAGE_CHARSET = "UTF-8";
	IGameService gService = null;
	protected static final String TAG = GameActivity.class.getName();
	private GestureDetector gestureScanner;
	private GameView m;
	GameController gc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause action");
		super.onPause();
	}

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

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop action");
		gc.disconnectView();
		System.gc();

	}

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

	private void exit() {
		gc.exit(GameActivity.this);
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureScanner.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		try {
			Thread.sleep(16);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.d(TAG, "onDown");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.d(TAG, "onFling");
		System.gc();
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

		if (GameController.disableContext) {
			return;
		}
		gc.onLongPress(e);
		try {
			Thread.sleep(16);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return gc.doScroll(distanceX, distanceY, m);
	}

	@Override
	public void onShowPress(MotionEvent e) {
		try {
			Thread.sleep(16);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.d(TAG, "onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		try {
			Thread.sleep(16);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return gc.doSingleTapUp(e);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		try {
			Thread.sleep(16);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_TEXT_ENTRY:
			return new ScriptEditorDialog(GameActivity.this);
		case DIALOG_CONTEXT_ACTION:
			return new ContextActionDialog(GameActivity.this);
		}
		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(0, 5, 0, "DEV-Free memory");
		// menu.add(0, 4, 0, "DEV-Remove downloads");
		// menu.add(0, 3, 0, "Chat");
		menu.add(0, 2, 0, "Preferences");
		menu.add(0, 1, 0, "Exit");
		menu.add(0, 7, 0, "DEV-Scripting");
		// menu.add(0, 4, 0, "About");
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
