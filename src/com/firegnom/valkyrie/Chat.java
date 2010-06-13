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

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.firegnom.valkyrie.service.IChatService;
import com.firegnom.valkyrie.service.IChatServiceCallback;

// TODO: Auto-generated Javadoc
/**
 * The Class Chat.
 */
public class Chat extends ListActivity implements OnKeyListener {

	/** The Constant TAG. */
	private static final String TAG = Chat.class.getName();

	/** The m user text. */
	private EditText mUserText;

	/** The m adapter. */
	private ArrayAdapter<String> mAdapter;

	/** The m strings. */
	private final ArrayList<String> mStrings = new ArrayList<String>();

	/** The chat service. */
	IChatService chatService;
	
	/** The username. */
	String username = null;

	/** The chat connection. */
	private final ServiceConnection chatConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName className,
				final IBinder service) {
			Log.d(TAG, "onServiceConnected");
			chatService = IChatService.Stub.asInterface(service);

			try {
				username = chatService.username();
			} catch (final RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				chatService.joinChat();
			} catch (final RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			setContentView(R.layout.chat);
			mAdapter = new ArrayAdapter<String>(Chat.this, R.layout.chat_line,
					mStrings);
			setListAdapter(mAdapter);
			mUserText = (EditText) findViewById(R.id.chat_userText);
			mUserText.setOnKeyListener(Chat.this);
			try {
				chatService.registerCallback(callback);
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void onServiceDisconnected(final ComponentName className) {
			Log.d(TAG, "onServiceDisconnected");
			chatService = null;
		}
	};

	/** The callback. */
	IChatServiceCallback.Stub callback = new IChatServiceCallback.Stub() {
		@Override
		public void chatUserJoined(final String username)
				throws RemoteException {
			send_u = username + " joined chat";
			send_m = "";
			hand.sendEmptyMessage(0);

		}

		@Override
		public void chatUserLeft(final String username) throws RemoteException {
			send_u = username + " left chat";
			send_m = "";
			hand.sendEmptyMessage(0);

		}

		@Override
		public void messageRecieaved(final String username, final String message)
				throws RemoteException {
			send_u = username;
			send_m = message;
			hand.sendEmptyMessage(0);
		}
	};

	/** The send_m. */
	String send_u, send_m;

	/** The hand. */
	Handler hand = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			writeMessage(send_u, send_m);
		}
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		bindService(new Intent(IChatService.class.getName()), chatConnection, 0);
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		try {
			chatService.leaveChat();
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unbindService(chatConnection);
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				sendText();
				return true;
			}
		}
		return false;
	}

	/**
	 * Send text.
	 */
	private void sendText() {
		try {
			final String text = mUserText.getText().toString();
			chatService.send(text);
			writeMessage(username, text);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Write message.
	 *
	 * @param username the username
	 * @param msg the msg
	 */
	private void writeMessage(final String username, final String msg) {
		Chat.this.mAdapter.add(username + ": " + msg);
		mUserText.setText(null);
	}

}
