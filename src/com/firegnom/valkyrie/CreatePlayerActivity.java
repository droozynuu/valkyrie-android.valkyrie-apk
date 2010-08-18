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
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.firegnom.valkyrie.service.ICreateUserCallback;
import com.firegnom.valkyrie.service.IGameService;

public class CreatePlayerActivity extends ValkyrieActivity {
	private int selectedClass = 0;
	private static final String TAG = CreatePlayerActivity.class.getName();
	IGameService mService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.please_wait);
		bindService(new Intent(IGameService.class.getName()), mConnection, 0);
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		if (mService != null) {
			try {
				mService.unregisterCreateUserCallback();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		unbindService(mConnection);
		super.onDestroy();
	}

	private OnClickListener createButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				((Button) findViewById(R.id.create_player_button))
						.setVisibility(View.INVISIBLE);
				mService.createUser(callback, selectedClass);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	ICreateUserCallback callback = new ICreateUserCallback.Stub() {
		@Override
		public void goToMap() throws RemoteException {
			startGame();
		}
	};

	private void startGame() {
		Log.d(TAG, "startGame");
		Intent myIntent = new Intent(getApplicationContext(),
				GameActivity.class);
		startActivity(myIntent);
		finish();
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = IGameService.Stub.asInterface(service);
			setContentView(R.layout.create_player);
			((Button) findViewById(R.id.create_player_button))
					.setOnClickListener(createButtonListener);
			Gallery g = (Gallery) findViewById(R.id.gallery);
			// Set the adapter to our custom adapter (below)
			g.setAdapter(new ImageAdapter(CreatePlayerActivity.this));

			// Set a item click listener, and just Toast the clicked position
			g.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView parent, View v,
						int position, long id) {
					selectedClass = position;
					switch (position) {
					case 0:
						((TextView) findViewById(R.id.create_player_desc_id))
								.setText(R.string.player_create_knight_desc);
						break;
					case 1:
						((TextView) findViewById(R.id.create_player_desc_id))
								.setText(R.string.player_create_archer_desc);
						break;
					case 2:
						((TextView) findViewById(R.id.create_player_desc_id))
								.setText(R.string.player_create_barbarian_desc);
						break;
					case 3:
						((TextView) findViewById(R.id.create_player_desc_id))
								.setText(R.string.player_create_ninja_desc);
						break;
					case 4:
						((TextView) findViewById(R.id.create_player_desc_id))
								.setText(R.string.player_create_wizard_desc);
						break;
					}

				}
			});
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;

		public ImageAdapter(Context c) {
			mContext = c;
			// See res/values/attrs.xml for the <declare-styleable> that defines
			// Gallery1.
			TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Gallery1_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			i.setImageResource(mImageIds[position]);
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setLayoutParams(new Gallery.LayoutParams(110, 110));

			// The preferred Gallery item background
			i.setBackgroundResource(mGalleryItemBackground);

			return i;
		}

		private Context mContext;

		private Integer[] mImageIds = {

		R.drawable.knight, R.drawable.archer, R.drawable.barbarian,
				R.drawable.ninja, R.drawable.wizard };

	}
}
