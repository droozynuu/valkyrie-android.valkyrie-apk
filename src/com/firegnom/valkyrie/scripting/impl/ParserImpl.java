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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.util.Log;
import android.widget.Toast;
import bsh.Interpreter;

import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.map.Position;
import com.firegnom.valkyrie.scripting.Parser;

// TODO: Auto-generated Javadoc
/**
 * The Class ParserImpl.
 */
public class ParserImpl implements Parser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.firegnom.valkyrie.scripting.impl.Parser#parse(java.lang.String)
	 */
	public void parse(final String src) {
		try {
			parse(src.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			postMessage(e.toString());
		}
	}

	/**
	 * Post message.
	 *
	 * @param msg the msg
	 */
	public void postMessage(final String msg) {
		GameController.getInstance().getView().post((new Runnable() {
			public void run() {
				Toast.makeText(
						GameController.getInstance().getView().getContext(),
						msg.toString(), 20000).show();
			}
		}));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.firegnom.valkyrie.scripting.impl.Parser#parse(byte[])
	 */
	public void parse(final byte[] src) {
		(new Thread() {
			@Override
			public void run() {
				try {
					GameController sc = GameController.getInstance();
					Position eventScreenPosition = sc.longPressPosition;
					Position eventMapPosition = null;
					if (eventScreenPosition != null) {
						eventMapPosition = sc.zone.getMapPosition(
								eventScreenPosition.x, eventScreenPosition.y,
								sc.sX, sc.sY);
					}
					Interpreter i = new Interpreter();
					i.set("sc",
							new SGameControllerImpl(sc, sc.getContext(), sc
									.getView()));
					i.set("gui", new SGuiImpl(sc.getContext(), sc.getView()));
					i.set("player", new SPlayerImpl(sc.getView()));
					i.set("p", eventMapPosition);
					i.eval(new InputStreamReader(new ByteArrayInputStream(src)));
				} catch (bsh.EvalError e) {
					postMessage(e.toString());
					Log.d("console", e.toString());
				}
			}
		}).start();
	}
}
