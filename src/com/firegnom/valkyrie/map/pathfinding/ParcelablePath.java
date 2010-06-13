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
package com.firegnom.valkyrie.map.pathfinding;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

// TODO: Auto-generated Javadoc
/**
 * The Class ParcelablePath.
 */
public class ParcelablePath implements Parcelable {

	/** The path. */
	public Path path;
	
	/** The Constant CREATOR. */
	public static final Parcelable.Creator<ParcelablePath> CREATOR = new Parcelable.Creator<ParcelablePath>() {

		@Override
		public ParcelablePath createFromParcel(final Parcel source) {

			return readFromParcel(source);
		}

		@Override
		public ParcelablePath[] newArray(final int size) {
			return new ParcelablePath[size];
		}

	};

	/**
	 * Read from parcel.
	 *
	 * @param in the in
	 * @return the parcelable path
	 */
	static public ParcelablePath readFromParcel(final Parcel in) {
		final ParcelablePath ret = new ParcelablePath(new Path());
		final ArrayList<Integer> x = new ArrayList<Integer>();
		final ArrayList<Integer> y = new ArrayList<Integer>();
		in.readList(x, Integer.class.getClassLoader());
		in.readList(y, Integer.class.getClassLoader());
		ret.path = new Path();
		for (int i = 0; i < x.size(); i++) {
			ret.path.appendStep(x.get(i), y.get(i));
		}
		return ret;
	}

	/**
	 * Instantiates a new parcelable path.
	 *
	 * @param p the p
	 */
	public ParcelablePath(final com.firegnom.valkyrie.net.protocol.Path p) {
		path = new Path();
		for (int i = 0; i < p.step.length; i++) {
			path.steps.add(new Step(p.step[i].x, p.step[i].y));
		}
	}

	/**
	 * Instantiates a new parcelable path.
	 *
	 * @param in the in
	 */
	public ParcelablePath(final Parcel in) {
		path = readFromParcel(in).path;
	}

	/**
	 * Instantiates a new parcelable path.
	 *
	 * @param p the p
	 */
	public ParcelablePath(final Path p) {
		path = p;
	}

	// TODO: not optimised
	/**
	 * Convert to net path.
	 *
	 * @return the com.firegnom.valkyrie.net.protocol. path
	 */
	public com.firegnom.valkyrie.net.protocol.Path convertToNetPath() {
		final com.firegnom.valkyrie.net.protocol.Path p = new com.firegnom.valkyrie.net.protocol.Path();
		final com.firegnom.valkyrie.net.protocol.Step[] st = new com.firegnom.valkyrie.net.protocol.Step[path.steps
				.size()];
		for (int i = 0; i < st.length; i++) {
			st[i] = new com.firegnom.valkyrie.net.protocol.Step();
			st[i].setX((short) path.steps.get(i).position.x);
			st[i].setY((short) path.steps.get(i).position.y);
		}
		p.setStep(st);
		return p;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	// public void readFromParcel(Parcel in) {
	// Parcelable [] steps =
	// in.readParcelableArray(Step.class.getClassLoader());
	// add(steps);
	// }

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		final ArrayList<Integer> x = new ArrayList<Integer>();
		final ArrayList<Integer> y = new ArrayList<Integer>();
		for (int i = 0; i < path.getLength(); i++) {
			x.add(path.getX(i));
			y.add(path.getY(i));
		}
		dest.writeList(x);
		dest.writeList(y);
	}

}
