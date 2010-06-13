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
package com.firegnom.valkyrie.engine;

import java.util.concurrent.LinkedBlockingQueue;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firegnom.valkyrie.GameActivity;
import com.firegnom.valkyrie.R;
import com.firegnom.valkyrie.action.ActionTask;

// TODO: Auto-generated Javadoc
/**
 * The Class Gui.
 */
public class Gui {
	
	/** The Constant TAG. */
	private static final String TAG = Gui.class.getName();

	/**
	 * Draw log.
	 *
	 * @param c the c
	 */
	static void drawLog(final Canvas c) {

	}

	/**
	 * Toast.
	 *
	 * @param resource the resource
	 */
	public static void toast(final int resource) {
		toast(resource, GameController.getInstance().view,
				GameController.getInstance().context);
	}

	/**
	 * Toast.
	 *
	 * @param resource the resource
	 * @param view the view
	 * @param context the context
	 */
	public static void toast(final int resource, final View view,
			final Context context) {
		if (view == null) {
			Log.w(TAG,
					"toast - no view connected to controller dropping resource :"
							+ resource);
			return;
		}
		toast(view.getResources().getString(
				R.string.pathfinding_how_to_get_there), view, context);
	}

	/**
	 * Toast.
	 *
	 * @param msg the msg
	 */
	public static void toast(final String msg) {
		toast(msg, GameController.getInstance().view,
				GameController.getInstance().context);
	}

	/**
	 * Toast.
	 *
	 * @param msg the msg
	 * @param view the view
	 * @param context the context
	 */
	public static void toast(final String msg, final View view,
			final Context context) {

		if (view == null) {
			Log.w(TAG,
					"toast - no view connected to controller dropping message:"
							+ msg);
			return;
		}
		view.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	/** The answer. */
	private int answer = 0;

	/** The queue. */
	LinkedBlockingQueue<ActionTask> queue;

	// LinkedList<Message>

	/**
	 * Info.
	 *
	 * @param msg the msg
	 */
	public void info(final String msg) {
		info(null, msg, true);
	}

	/**
	 * Info.
	 *
	 * @param title the title
	 * @param msg the msg
	 * @param blocking the blocking
	 */
	public void info(final String title, final String msg,
			final boolean blocking) {
		GameController.getInstance().view.post(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						GameController.getInstance().context);
				builder.setMessage(msg)
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											final DialogInterface dialog,
											final int id) {
										answer = 1;
									}
								});
				if (title != null) {
					builder.setTitle(title);
				}
				final AlertDialog alert = builder.create();
				alert.setOwnerActivity((GameActivity) GameController
						.getInstance().context);
				alert.show();
			}
		});
		if (blocking) {
			while (answer == 0) {
				try {
					Thread.sleep(500);
				} catch (final InterruptedException e) {
					answer = 0;
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Info async.
	 *
	 * @param msg the msg
	 */
	public void infoAsync(final String msg) {
		info(null, msg, false);
	}

	/**
	 * Question.
	 *
	 * @param question the question
	 * @return true, if successful
	 */
	public boolean question(final String question) {
		GameController.getInstance().view.post(new Runnable() {
			@Override
			public void run() {
				// ((GameActivity)GameController.getInstance().context)
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						GameController.getInstance().context);
				builder.setMessage(question)
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											final DialogInterface dialog,
											final int id) {
										GameController.getInstance().gui.answer = 1;
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											final DialogInterface dialog,
											final int id) {
										GameController.getInstance().gui.answer = 2;
									}
								});
				final AlertDialog alert = builder.create();
				alert.setOwnerActivity((GameActivity) GameController
						.getInstance().context);
				alert.show();
			}
		});
		while (answer == 0) {
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				answer = 0;
				e.printStackTrace();
			}
		}
		if (answer == 1) {
			answer = 0;
			return true;
		}
		answer = 0;
		return false;
	}

}
