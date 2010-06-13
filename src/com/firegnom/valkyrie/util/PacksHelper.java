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
package com.firegnom.valkyrie.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.firegnom.valkyrie.net.Download;

// TODO: Auto-generated Javadoc
/**
 * The Class PacksHelper.
 */
public class PacksHelper {
	// static String path = "/sdcard/com.firegnom.valkyrie/cache/";

	/**
	 * Download packs.
	 *
	 * @param path the path
	 */
	public static void downloadPacks(final String path) {
		downloadPacks(path, null);
	}

	/**
	 * Download packs.
	 *
	 * @param path the path
	 * @param o the o
	 */
	public static void downloadPacks(final String path, final Observer o) {
		Download packsDownload;
		try {
			packsDownload = new Download(new URL(
					"http://valkyrie.firegnom.com/data/packs.json"), path, true);
		} catch (final MalformedURLException e1) {
			e1.printStackTrace();
			return;
		}
		if (!packsDownload.download()) {
			return;
		}
		final File packs = new File(path + "packs.json");
		JSONObject packsjson;

		try {
			packsjson = new JSONObject(
					Util.convertStreamToString(new FileInputStream(packs)));
			final JSONArray packsArray = packsjson.getJSONArray("install");
			for (int i = 0; i < packsArray.length(); i++) {
				final String pack = packsArray.getString(i);
				if (!new File(path + pack + ".json").exists()) {
					final URL u = new URL("http://valkyrie.firegnom.com/data/"
							+ pack + ".zip");
					Download d;
					if (o != null) {
						d = new Download(u, path, true, o);
					} else {
						d = new Download(u, path, true);
					}

					System.out.println(d.download());
					System.out.println(path + pack + ".zip");
					final NativeUnzip un = new NativeUnzip();

					final String c = new String(path + pack + ".zip");
					final String dj = new String(path);
					final long time = System.currentTimeMillis();
					// un.dounzip(c,dj);

					ZipUtil.unzip(new File(path + pack + ".zip"),
							new File(path));
					System.out.println("time :"
							+ ((System.currentTimeMillis() - time) / 1000));

				}

			}

		} catch (final JSONException e) {
			e.printStackTrace();
			return;

		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (final MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Packs avilable.
	 *
	 * @param path the path
	 * @return true, if successful
	 */
	public static boolean packsAvilable(final String path) {
		Download packsDownload;
		try {
			packsDownload = new Download(new URL(
					"http://valkyrie.firegnom.com/data/packs.json"), path, true);
		} catch (final MalformedURLException e1) {
			e1.printStackTrace();
			return false;
		}
		if (!packsDownload.download()) {
			return false;
		}
		final File packs = new File(path + "packs.json");
		JSONObject packsjson;

		try {
			packsjson = new JSONObject(
					Util.convertStreamToString(new FileInputStream(packs)));
			final JSONArray packsArray = packsjson.getJSONArray("install");
			for (int i = 0; i < packsArray.length(); i++) {
				final String pack = packsArray.getString(i);
				if (!new File(path + pack + ".json").exists()) {
					return true;
				}
			}
		} catch (final JSONException e) {
			e.printStackTrace();

		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

}
