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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.service.IPackLoadListener;
import com.firegnom.valkyrie.service.IResourceLoaderService;

public class PackageDownloadActivity extends ValkyrieActivity {

	protected static final String TAG = PackageDownloadActivity.class.getName();
	protected static final int GO_TO_GAME = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.please_wait_1);
		if (resourceLoaderService == null) {
			bindService(new Intent(IResourceLoaderService.class.getName()),
					resourceLoaderConnection, Context.BIND_AUTO_CREATE);
		}

	}

	@Override
	protected void onDestroy() {
		unbindService(resourceLoaderConnection);
		resourceLoaderService = null;
		finish();
		super.onDestroy();
	}

	public IResourceLoaderService resourceLoaderService;
	private ServiceConnection resourceLoaderConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "resourceLoaderService onServiceConnected");
			resourceLoaderService = IResourceLoaderService.Stub
					.asInterface(service);
			try {
				GameController.getInstance().packsDownloading = true;
				resourceLoaderService.downloadPacks(loadListener);
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
	IPackLoadListener.Stub loadListener = new IPackLoadListener.Stub() {
		@Override
		public void finished() throws RemoteException {
			mHandler.sendEmptyMessage(GO_TO_GAME);
		}
	};
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_TO_GAME:
				GameController.getInstance().papksLoading = false;
				GameController.getInstance().packsDownloading = false;
				startActivity(new Intent(PackageDownloadActivity.this,
						GameActivity.class));
				finish();
				break;

			}
		}
	};

}
