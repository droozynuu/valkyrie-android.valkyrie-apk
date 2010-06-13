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

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firegnom.valkyrie.engine.ValkyrieRuntimeException;
import com.firegnom.valkyrie.service.ILoginCallback;
import com.firegnom.valkyrie.service.ILoginService;
import com.firegnom.valkyrie.share.constant.GameModes;

// TODO: Auto-generated Javadoc
/**
 * The Class LoginActivity.
 */
public class LoginActivity extends ValkyrieActivity {
	
	/** The Constant TAG. */
	private static final String TAG = LoginActivity.class.getName();
	
	/** The is exit. */
	private boolean isExit = false;

	/** The loginbutton listener. */
	private final OnClickListener loginbuttonListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			try {
				final String username = ((EditText) (LoginActivity.this
						.findViewById(R.id.login_username_edit))).getText()
						.toString();
				LoginActivity.this.findViewById(R.id.login_login_button)
						.setVisibility(View.INVISIBLE);
				mService.login(mCallback, username, "");
			} catch (final RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Class for interacting with the main interface of the service.
	 */
	ILoginService mService = null;;

	/** Another interface we use on the service. */
	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName className,
				final IBinder service) {
			mService = ILoginService.Stub.asInterface(service);
			try {
				if (mService.isLoggedIn()) {
					if (mService.isPlayerCreated()) {
						startGame();
					} else {
						createPlayer();
					}
				} else {
					setContentView(R.layout.login);
					final Button button = (Button) findViewById(R.id.login_login_button);
					button.setOnClickListener(loginbuttonListener);
				}

			} catch (final RemoteException e) {
			}
		}

		@Override
		public void onServiceDisconnected(final ComponentName className) {
			mService = null;
		}
	};

	/** The message. */
	String message = "";

	/** The m callback. */
	protected ILoginCallback.Stub mCallback = new ILoginCallback.Stub() {

		@Override
		public void disconnected() throws RemoteException {
			Log.d(TAG, "disconnected");
			message = "Server not Working at the moment please again later";
			hand.sendEmptyMessage(0);
		}

		@Override
		public void loggedIn(final int activity) throws RemoteException {
			Log.d(TAG, "loggedIn");
			if (activity == GameModes.MAP_MODE) {
				startGame();
				return;
			} else if (activity == GameModes.CREATE_PLAYER_MODE) {
				createPlayer();
				return;
			}
			throw new ValkyrieRuntimeException("Not implemented !!!");

		}

		@Override
		public void loginFailed() throws RemoteException {
			Log.d(TAG, "loginFailed");
			message = "Login failed";
			hand.sendEmptyMessage(0);
		}

	};
	
	/** The hand. */
	Handler hand = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			LoginActivity.this.findViewById(R.id.login_login_button)
					.setVisibility(View.VISIBLE);
			Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT)
					.show();
		}
	};

	/**
	 * Creates the player.
	 */
	private void createPlayer() {
		Log.d(TAG, "createPlayer");
		final Intent myIntent = new Intent(getApplicationContext(),
				CreatePlayerActivity.class);
		startActivity(myIntent);
		finish();
	}

	/**
	 * Exit.
	 */
	private void exit() {
		isExit = true;
		finish();
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.ValkyrieActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		LoginActivity.this.setContentView(R.layout.please_wait);
		startService(new Intent(ILoginService.class.getName()));
		bindService(new Intent(ILoginService.class.getName()), mConnection, 0);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, 1, 0, "Exit");
		// menu.add(0, 4, 0, "Help");
		// menu.add(0, -1, 0, "Crash");
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		if (mService != null) {
			try {
				mService.unregisterCallback();
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		unbindService(mConnection);
		if (isExit) {
			Log.d(TAG, "exiting");
			stopService(new Intent(ILoginService.class.getName()));
		}
		super.onDestroy();
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
		case -1:
			throw new ValkyrieRuntimeException("aaaaaaaaaaaa");
		}
		return false;

	}

	/**
	 * Start game.
	 */
	private void startGame() {
		Log.d(TAG, "startGame");
		final Intent myIntent = new Intent(getApplicationContext(),
				GameActivity.class);
		startActivity(myIntent);
		finish();
	}
}
