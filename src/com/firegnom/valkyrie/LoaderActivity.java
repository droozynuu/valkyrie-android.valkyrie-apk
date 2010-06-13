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

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

// TODO: Auto-generated Javadoc
/**
 * The Class LoaderActivity.
 */
public class LoaderActivity extends ValkyrieActivity {
	
	/**
	 * The Class Loader.
	 */
	class Loader extends Thread {
		
		/** The name. */
		String name;
		
		/** The x. */
		private int x;
		
		/** The y. */
		private int y;

		/**
		 * Name.
		 *
		 * @param name the name
		 * @return the loader
		 */
		public Loader name(final String name) {
			this.name = name;
			return this;
		}

		/**
		 * Pos.
		 *
		 * @param x the x
		 * @param y the y
		 * @return the loader
		 */
		public Loader pos(final int x, final int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			isLoading = true;
			// GameController.getInstance()._performLoad(name,
			// getApplicationContext(),x,y);
			// Intent myIntent = new Intent(getApplicationContext(),
			// GameActivity.class);
			isLoading = false;
			// startActivityForResult(myIntent, 0);
			finish();

		}
	}

	/** The Constant MAP_NAME. */
	public static final String MAP_NAME = "MAP_NAME";
	
	/** The Constant X_NAME. */
	public static final String X_NAME = "X_NAME";
	
	/** The Constant Y_NAME. */
	public static final String Y_NAME = "Y_NAME";
	
	/** The is loading. */
	public static boolean isLoading = false;

	// private GameView m ;
	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.ValkyrieActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = getIntent();
		final String name = intent.getStringExtra(MAP_NAME);
		final int x = intent.getIntExtra(X_NAME, 0);
		final int y = intent.getIntExtra(Y_NAME, 0);

		if (name == null || x == 0 || y == 0) {
			finish();
		}
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//
		//
		// SplashView s = new SplashView(getApplicationContext());
		if (!isLoading) {
			setContentView(R.layout.please_wait);
		}
	}

}
