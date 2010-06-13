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

import android.os.Bundle;
import android.preference.PreferenceActivity;

// TODO: Auto-generated Javadoc
/**
 * The Class Preferences.
 */
public class Preferences extends PreferenceActivity {
	
	/** The Constant IS_SHOW_MOVES. */
	public final static String IS_SHOW_MOVES = "show_move_matrix";
	
	/** The Constant IS_FOLLOW_PLAYER. */
	public static final String IS_FOLLOW_PLAYER = "follow_player";
	
	/** The Constant DISABLE_PLAYER_SORTING. */
	public static final String DISABLE_PLAYER_SORTING = "disable_player_sorting";
	
	/** The Constant DRAW_VISIBILITY_RANGE. */
	public static final String DRAW_VISIBILITY_RANGE = "draw_visibility_range";
	
	/** The Constant CHANGE_MOVE_IMAGES. */
	public static final String CHANGE_MOVE_IMAGES = "change_move_images";
	
	/** The Constant JUMPY_MOVES. */
	public static final String JUMPY_MOVES = "jumpy_moves";
	
	/** The Constant FULL_INVALIDATE. */
	public static final String FULL_INVALIDATE = "full_invalidate";

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}

}
