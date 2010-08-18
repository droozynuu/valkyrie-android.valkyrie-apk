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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;

import com.firegnom.valkyrie.R;
import com.firegnom.valkyrie.net.AsyncDownload;
import com.firegnom.valkyrie.net.Download;
import com.firegnom.valkyrie.net.DownloadQueue;
import com.firegnom.valkyrie.util.PacksHelper;
import com.firegnom.valkyrie.util.ResourceLoader;

public final class ResourceLoaderService extends IResourceLoaderService.Stub {
	protected static final String TAG = ResourceLoaderService.class.getName();
	private static final int CUSTOM_VIEW_ID = 2;
	ValkyrieService service;
	ResourceLoader rl;
	boolean complete = false;
	private boolean error = false;

	Notification notification;
	DownloadQueue downloadQueue;
	IQueuleChangeListener listener = null;
	private boolean downloadingPacks = false;

	public ResourceLoaderService(final ValkyrieService service) {
		this.service = service;
		notification = new Notification(R.drawable.stat_sample, "",
				System.currentTimeMillis());
		rl = new ResourceLoader();
		downloadQueue = new DownloadQueue(rl.cacheLocation, true);
		downloadQueue.useObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				if (((Download) observable).getStatus() == Download.COMPLETE) {
					// Log.d(TAG, "Remaining : "+downloadQueue.size());
					service.serverConnection
							.logDownload(((Download) observable).getSize());
					if (listener != null) {
						try {
							listener.remaining(downloadQueue.size());
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	@Override
	public void download(final String name, ILoaderCallback callback)
			throws RemoteException {

		Log.d(TAG, "getBitmap");
		complete = false;
		error = false;
		boolean ret = rl.download(name, new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				error = ((AsyncDownload) observable).getStatus() == AsyncDownload.ERROR;
				complete = ((AsyncDownload) observable).getStatus() == AsyncDownload.COMPLETE;
				if (complete) {
					service.serverConnection
							.logDownload(((AsyncDownload) observable).getSize());
					Log.d(TAG, "download :" + ValkyrieServerConnection.download);
				}
				complete = complete || error;
				showNotification(
						(int) ((AsyncDownload) observable).getProgress(),
						"Downloading :" + name);
				Log.d(TAG,
						"Downloading  "
								+ name
								+ ", "
								+ ((AsyncDownload) observable).getProgress()
								+ "% ,size:"
								+ ((AsyncDownload) observable).getSize()
								+ " , status :"
								+ AsyncDownload.STATUSES[((AsyncDownload) observable)
										.getStatus()]);
			}
		});
		if (!ret) {
			while (!complete) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.d(TAG, "Stopped waiting");
		} else {
			Log.d(TAG, "Stopped waiting");
			callback.loadComplete();
			cancelNotification();
			return;
		}
		if (!error) {
			callback.loadComplete();
			cancelNotification();
			return;
		}
		callback.loadFailed();
		cancelNotification();
	}

	void showNotification(int percentage, String msg) {
		RemoteViews contentView = new RemoteViews(service.getPackageName(),
				R.layout.custom_notification);
		contentView.setProgressBar(R.id.custom_notification_progressbar, 100,
				percentage, false);
		contentView.setTextViewText(R.id.custom_notification_text, msg);
		notification.flags |= Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_NO_CLEAR;
		notification.contentView = contentView;
		Intent notificationIntent = new Intent(service, ValkyrieService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(service, 0,
				notificationIntent, 0);
		notification.contentIntent = contentIntent;
		service.mNM.notify(CUSTOM_VIEW_ID, notification);

	}

	void cancelNotification() {
		service.mNM.cancel(CUSTOM_VIEW_ID);
	}

	@Override
	public void addToDownloadQueue(String name) throws RemoteException {
		try {
			downloadQueue.add(new URL(rl.URL + name));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void registerQueuleChangeListener(IQueuleChangeListener listener)
			throws RemoteException {
		this.listener = listener;
	}

	@Override
	public void downloadPacks(IPackLoadListener listener)
			throws RemoteException {
		Log.d(TAG, "pownloadPacks");
		if (!downloadingPacks) {
			downloadingPacks = true;
			loadListener = listener;
			new TestRun(rl.cacheLocation).start();
		}
	}

	IPackLoadListener loadListener;

	class TestRun extends Thread {
		String path;

		public TestRun(String path) {
			this.path = path;
		}

		@Override
		public void run() {
			PacksHelper.downloadPacks(path, new Observer() {

				@Override
				public void update(Observable observable, Object data) {
					Download d = (Download) observable;
					showNotification((int) d.getProgress(), d.getUrl());
					if (d.getStatus() == Download.COMPLETE) {
						cancelNotification();
					}
					if (d.getStatus() == Download.ERROR) {
						// TODO error notification
						cancelNotification();
					}
				}
			});
			if (loadListener != null) {
				try {
					loadListener.finished();
					downloadingPacks = false;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
