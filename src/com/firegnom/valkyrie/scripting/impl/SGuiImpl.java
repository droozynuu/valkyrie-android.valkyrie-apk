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
package com.firegnom.valkyrie.scripting.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.firegnom.valkyrie.GameActivity;
import com.firegnom.valkyrie.scripting.SGui;

// TODO: Auto-generated Javadoc
/**
 * The Class SGuiImpl.
 */
public class SGuiImpl implements SGui {
	
	/** The context. */
	private final Context context;
	
	/** The v. */
	private final View v;
	
	/** The answer. */
	private int answer = 0;

	/**
	 * Instantiates a new s gui impl.
	 *
	 * @param context the context
	 * @param v the v
	 */
	public SGuiImpl(Context context, View v) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.v = v;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SGui#toast(java.lang.String)
	 */
	@Override
	public void toast(final String msg) {
		v.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SGui#question(java.lang.String)
	 */
	@Override
	public boolean question(final String question) {
		v.post(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(question)
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										answer = 1;
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										answer = 2;
									}
								});
				AlertDialog alert = builder.create();
				alert.setOwnerActivity((GameActivity) context);
				alert.show();
			}
		});
		while (answer == 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				answer = 0;
				e.printStackTrace();
			}
		}
		if (answer == 1) {
			answer = 0;
			return true;
		}

		return false;
		//

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SGui#info(java.lang.String, java.lang.String)
	 */
	@Override
	public void info(final String title, final String msg) {
		v.post(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(msg)
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										answer = 1;
									}
								});
				if (title != null) {
					builder.setTitle(title);
				}
				AlertDialog alert = builder.create();
				alert.setOwnerActivity((GameActivity) context);
				alert.show();
			}
		});
		while (answer == 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				answer = 0;
				e.printStackTrace();
			}
		}

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.scripting.SGui#info(java.lang.String)
	 */
	public void info(final String msg) {
		info(null, msg);
	}

}
