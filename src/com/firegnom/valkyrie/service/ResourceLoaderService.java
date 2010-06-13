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

// TODO: Auto-generated Javadoc
/**
 * The Class ResourceLoaderService.
 */
public final class ResourceLoaderService extends IResourceLoaderService.Stub {
	
	/**
	 * The Class TestRun.
	 */
	class TestRun extends Thread {
		
		/** The path. */
		String path;

		/**
		 * Instantiates a new test run.
		 *
		 * @param path the path
		 */
		public TestRun(final String path) {
			this.path = path;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			PacksHelper.downloadPacks(path, new Observer() {

				@Override
				public void update(final Observable observable,
						final Object data) {
					final Download d = (Download) observable;
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
				} catch (final RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/** The Constant TAG. */
	protected static final String TAG = ResourceLoaderService.class.getName();
	
	/** The Constant CUSTOM_VIEW_ID. */
	private static final int CUSTOM_VIEW_ID = 2;
	
	/** The service. */
	ValkyrieService service;
	
	/** The rl. */
	ResourceLoader rl;
	
	/** The complete. */
	boolean complete = false;

	/** The error. */
	private boolean error = false;
	
	/** The notification. */
	Notification notification;
	
	/** The download queue. */
	DownloadQueue downloadQueue;
	
	/** The listener. */
	IQueuleChangeListener listener = null;

	/** The downloading packs. */
	private boolean downloadingPacks = false;

	/** The load listener. */
	IPackLoadListener loadListener;

	/**
	 * Instantiates a new resource loader service.
	 *
	 * @param service the service
	 */
	public ResourceLoaderService(final ValkyrieService service) {
		this.service = service;
		notification = new Notification(R.drawable.stat_sample, "",
				System.currentTimeMillis());
		rl = new ResourceLoader();
		downloadQueue = new DownloadQueue(ResourceLoader.cacheLocation, true);
		downloadQueue.useObserver(new Observer() {
			@Override
			public void update(final Observable observable, final Object data) {
				if (((Download) observable).getStatus() == Download.COMPLETE) {
					// Log.d(TAG, "Remaining : "+downloadQueue.size());
					service.serverConnection
							.logDownload(((Download) observable).getSize());
					if (listener != null) {
						try {
							listener.remaining(downloadQueue.size());
						} catch (final RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IResourceLoaderService#addToDownloadQueue(java.lang.String)
	 */
	@Override
	public void addToDownloadQueue(final String name) throws RemoteException {
		try {
			downloadQueue.add(new URL(ResourceLoader.URL + name));
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Cancel notification.
	 */
	void cancelNotification() {
		service.mNM.cancel(CUSTOM_VIEW_ID);
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IResourceLoaderService#download(java.lang.String, com.firegnom.valkyrie.service.ILoaderCallback)
	 */
	@Override
	public void download(final String name, final ILoaderCallback callback)
			throws RemoteException {

		Log.d(TAG, "getBitmap");
		complete = false;
		error = false;
		final boolean ret = rl.download(name, new Observer() {
			@Override
			public void update(final Observable observable, final Object data) {
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
				} catch (final InterruptedException e) {
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

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IResourceLoaderService#downloadPacks(com.firegnom.valkyrie.service.IPackLoadListener)
	 */
	@Override
	public void downloadPacks(final IPackLoadListener listener)
			throws RemoteException {
		Log.d(TAG, "pownloadPacks");
		if (!downloadingPacks) {
			downloadingPacks = true;
			loadListener = listener;
			new TestRun(ResourceLoader.cacheLocation).start();
		}
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.IResourceLoaderService#registerQueuleChangeListener(com.firegnom.valkyrie.service.IQueuleChangeListener)
	 */
	@Override
	public void registerQueuleChangeListener(
			final IQueuleChangeListener listener) throws RemoteException {
		this.listener = listener;
	}

	/**
	 * Show notification.
	 *
	 * @param percentage the percentage
	 * @param msg the msg
	 */
	void showNotification(final int percentage, final String msg) {
		final RemoteViews contentView = new RemoteViews(
				service.getPackageName(), R.layout.custom_notification);
		contentView.setProgressBar(R.id.custom_notification_progressbar, 100,
				percentage, false);
		contentView.setTextViewText(R.id.custom_notification_text, msg);
		notification.flags |= Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_NO_CLEAR;
		notification.contentView = contentView;
		final Intent notificationIntent = new Intent(service,
				ValkyrieService.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(service,
				0, notificationIntent, 0);
		notification.contentIntent = contentIntent;
		service.mNM.notify(CUSTOM_VIEW_ID, notification);

	}

}
