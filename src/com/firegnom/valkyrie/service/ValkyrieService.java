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
package com.firegnom.valkyrie.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.firegnom.valkyrie.LoginActivity;
import com.firegnom.valkyrie.R;
import com.firegnom.valkyrie.map.pathfinding.ParcelablePath;
import com.firegnom.valkyrie.share.constant.GameModes;

public class ValkyrieService extends Service {

	private final static String TAG = "ValkyrieService";
	public static final int MSG_LOGGED_IN = 0;
	public static final int MSG_LOGIN_FAILED = 1;

	IChatServiceCallback chatCallback;
	ICreateUserCallback createUserCallback;

	String username;
	private String password;

	NotificationManager mNM;
	Notification notification;

	ValkyrieServerConnection serverConnection;
	ResourceLoaderService resourceLoaderService;

	@Override
	public void onCreate() {
		serverConnection = new ValkyrieServerConnection(this);
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();
		resourceLoaderService = new ResourceLoaderService(this);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mNM.cancelAll();
		serverConnection.logout();
	}

	private void showNotification() {
		CharSequence text = getText(R.string.valkyrie_service_started);
		notification = new Notification(R.drawable.stat_sample, text,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_NO_CLEAR;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, LoginActivity.class), 0);
		notification.setLatestEventInfo(this,
				getText(R.string.valkyrie_service_label), text, contentIntent);
		mNM.notify(R.string.valkyrie_service_started, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (ILoginService.class.getName().equals(intent.getAction())) {
			Log.d(TAG, "ILoginService binded");
			return loginBinder;
		}
		if (IGameService.class.getName().equals(intent.getAction())) {
			Log.d(TAG, "IGameService binded");
			return gameBinder;
		}
		if (IChatService.class.getName().equals(intent.getAction())) {
			Log.d(TAG, "IChatService binded");
			return chatBinder;
		}
		if (IResourceLoaderService.class.getName().equals(intent.getAction())) {
			Log.d(TAG, "IResourceLoaderService binded");
			return resourceLoaderService;
		}
		return null;
	}

	private final ILoginService.Stub loginBinder = new ILoginService.Stub() {

		@Override
		public void login(ILoginCallback cb, String username, String password)
				throws RemoteException {
			serverConnection.login(cb, username, password);
		}

		@Override
		public boolean isLoggedIn() throws RemoteException {
			return serverConnection.loggedIn;
		}

		@Override
		public void unregisterCallback() throws RemoteException {
			serverConnection.loginCallback = null;
		}

		@Override
		public boolean isPlayerCreated() throws RemoteException {
			return serverConnection.gameMode != GameModes.CREATE_PLAYER_MODE;
		}
	};

	private final IGameService.Stub gameBinder = new IGameService.Stub() {
		@Override
		public void move(ParcelablePath p, int playerClass)
				throws RemoteException {
			serverConnection.move(p, playerClass);
		}

		@Override
		public String getUsername() throws RemoteException {
			return username;
		}

		@Override
		public void registerCallback(IGameServiceCallback callback)
				throws RemoteException {
			serverConnection.registerGameServiceCallback(callback);
		}

		@Override
		public void exit() throws RemoteException {
			stopSelf();
		}

		@Override
		public void createUser(ICreateUserCallback callback, int selectedClass)
				throws RemoteException {
			createUserCallback = callback;
			serverConnection.createUser(selectedClass);

		}

		@Override
		public void unregisterCallback() throws RemoteException {
			serverConnection.unregisterGameServiceCallback();

		}

		@Override
		public void unregisterCreateUserCallback() throws RemoteException {
			createUserCallback = null;

		}

		@Override
		public void requestPlayerInfo() throws RemoteException {
			serverConnection.sendRequestPlayerInfo();

		}

		@Override
		public void requestPlayersPositions() throws RemoteException {
			serverConnection.requestPlayersPositions();

		}

		@Override
		public void changeGameMode(int mode) throws RemoteException {
			serverConnection.sendChangeGameMode(mode);
		}

	};

	private final IChatService.Stub chatBinder = new IChatService.Stub() {
		@Override
		public void send(String message) throws RemoteException {
			serverConnection.sendChatMessage(message);

		}

		@Override
		public void registerCallback(IChatServiceCallback callback)
				throws RemoteException {
			chatCallback = callback;

		}

		@Override
		public String username() throws RemoteException {
			return username;
		}

		@Override
		public void joinChat() throws RemoteException {
			serverConnection.sendjoinChatMessage();

		}

		@Override
		public void leaveChat() throws RemoteException {
			serverConnection.sendleaveChatMessage();

		}

	};

}
