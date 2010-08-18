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

public class PacksHelper {
	// static String path = "/sdcard/com.firegnom.valkyrie/cache/";

	public static boolean packsAvilable(String path) {
		Download packsDownload;
		try {
			packsDownload = new Download(new URL(
					"http://valkyrie.firegnom.com/data/packs.json"), path, true);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return false;
		}
		if (!packsDownload.download()) {
			return false;
		}
		File packs = new File(path + "packs.json");
		JSONObject packsjson;

		try {
			packsjson = new JSONObject(
					Util.convertStreamToString(new FileInputStream(packs)));
			JSONArray packsArray = packsjson.getJSONArray("install");
			for (int i = 0; i < packsArray.length(); i++) {
				String pack = packsArray.getString(i);
				if (!new File(path + pack + ".json").exists()) {
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static void downloadPacks(String path) {
		downloadPacks(path, null);
	}

	public static void downloadPacks(String path, Observer o) {
		Download packsDownload;
		try {
			packsDownload = new Download(new URL(
					"http://valkyrie.firegnom.com/data/packs.json"), path, true);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return;
		}
		if (!packsDownload.download()) {
			return;
		}
		File packs = new File(path + "packs.json");
		JSONObject packsjson;

		try {
			packsjson = new JSONObject(
					Util.convertStreamToString(new FileInputStream(packs)));
			JSONArray packsArray = packsjson.getJSONArray("install");
			for (int i = 0; i < packsArray.length(); i++) {
				String pack = packsArray.getString(i);
				if (!new File(path + pack + ".json").exists()) {
					URL u = new URL("http://valkyrie.firegnom.com/data/" + pack
							+ ".zip");
					Download d;
					if (o != null) {
						d = new Download(u, path, true, o);
					} else {
						d = new Download(u, path, true);
					}

					System.out.println(d.download());
					System.out.println(path + pack + ".zip");
					NativeUnzip un = new NativeUnzip();

					String c = new String(path + pack + ".zip");
					String dj = new String(path);
					long time = System.currentTimeMillis();
					// un.dounzip(c,dj);

					ZipUtil.unzip(new File(path + pack + ".zip"),
							new File(path));
					System.out.println("time :"
							+ ((System.currentTimeMillis() - time) / 1000));

				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
			return;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

}
