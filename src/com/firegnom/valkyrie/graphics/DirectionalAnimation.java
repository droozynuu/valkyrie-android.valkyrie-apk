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
package com.firegnom.valkyrie.graphics;

import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.firegnom.valkyrie.common.Dir;
import com.firegnom.valkyrie.map.Position;

public class DirectionalAnimation implements DrawableFeature {

	private ArrayList<ArrayList<Image>> dirFrames;
	private short position = 0;
	private short direction = 0;
	private short numOfFrames = 0;
	private int width;
	private int height;
	private int speed = 80;
	private String name;
	private Position pos = new Position();

	public DirectionalAnimation setX(int x) {
		pos.x = x;
		return this;
	}

	public DirectionalAnimation setY(int y) {
		pos.y = y;
		return this;
	}

	public DirectionalAnimation(String name, short numOfFrames, int width,
			int height, int xOffset, int yOffset) {
		this.setName(name);
		this.setNumOfFrames(numOfFrames);
		this.width = width;
		this.height = height;

		dirFrames = new ArrayList<ArrayList<Image>>();
		for (int i = 0; i < Dir.dirs; i++) {
			ArrayList<Image> frames = new ArrayList<Image>();
			for (int j = 0; j < numOfFrames; j++) {
				frames.add(new Image(name + "," + j + "," + i + ".png", width,
						height, xOffset, yOffset));
			}
			dirFrames.add(i, frames);
		}
	}

	public Image getBitmap() {
		return dirFrames.get(direction).get(position);
	}

	/**
	 * Use directions from class Dir in commons
	 * 
	 * @see Dir
	 */
	public void changeDir(short dir) {
		direction = dir;
	}

	public void setPosition(short pos) {
		position = pos;
	}

	private boolean forward = true;

	public short nextFrame() {
		position++;
		if (position >= getNumOfFrames()) {
			forward = false;
			position = 0;
		}
		return position;
	}

	public void resetFrame() {
		position = 0;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	AnimationTask animationTask;
	Future<?> submit;

	public void start(ScheduledThreadPoolExecutor executor) {
		// TODO Auto-generated method stub
		if (animationTask == null) {
			animationTask = new AnimationTask(this);
			submit = executor.scheduleAtFixedRate(animationTask, 0, speed,
					TimeUnit.MILLISECONDS);

		}

	}

	public void stop() {
		// there was a null pointer here it happend when in fight mode you click
		// on
		// your self or somewhere where you can't move
		if (submit != null) {
			submit.cancel(true);
		}
		animationTask = null;
		// resetFrame();
	}

	@Override
	public void draw(Canvas canvas) {
		getBitmap().setX(pos.x).setY(pos.y).draw(canvas);
	}

	@Override
	public Rect getBounds() {
		// TODO Auto-generated method stub

		return null;
	}

	/**
	 * @param numOfFrames
	 *            the numOfFrames to set
	 */
	public void setNumOfFrames(short numOfFrames) {
		this.numOfFrames = numOfFrames;
	}

	/**
	 * @return the numOfFrames
	 */
	public short getNumOfFrames() {
		return numOfFrames;
	}

}
