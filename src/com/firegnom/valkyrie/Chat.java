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

public class Chat extends ListActivity implements OnKeyListener {

	private static final String TAG = Chat.class.getName();

	private EditText mUserText;

	private ArrayAdapter<String> mAdapter;

	private ArrayList<String> mStrings = new ArrayList<String>();

	IChatService chatService;
	String username = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		bindService(new Intent(IChatService.class.getName()), chatConnection, 0);
	}

	private void sendText() {
		try {
			String text = mUserText.getText().toString();
			chatService.send(text);
			writeMessage(username, text);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeMessage(String username, String msg) {
		Chat.this.mAdapter.add(username + ": " + msg);
		mUserText.setText(null);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
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

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		try {
			chatService.leaveChat();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unbindService(chatConnection);
		super.onDestroy();
	};

	private ServiceConnection chatConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			chatService = IChatService.Stub.asInterface(service);

			try {
				username = chatService.username();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				chatService.joinChat();
			} catch (RemoteException e1) {
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
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "onServiceDisconnected");
			chatService = null;
		}
	};

	IChatServiceCallback.Stub callback = new IChatServiceCallback.Stub() {
		@Override
		public void messageRecieaved(String username, String message)
				throws RemoteException {
			send_u = username;
			send_m = message;
			hand.sendEmptyMessage(0);
		}

		@Override
		public void chatUserJoined(String username) throws RemoteException {
			send_u = username + " joined chat";
			send_m = "";
			hand.sendEmptyMessage(0);

		}

		@Override
		public void chatUserLeft(String username) throws RemoteException {
			send_u = username + " left chat";
			send_m = "";
			hand.sendEmptyMessage(0);

		}
	};
	String send_u, send_m;
	Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			writeMessage(send_u, send_m);
		}
	};

}
