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

import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.firegnom.valkyrie.engine.GameController;

// TODO: Auto-generated Javadoc
/**
 * The Class AnimationTask.
 */
public class AnimationTask implements Runnable {
	
	/** The pl. */
	DirectionalAnimation pl;
	
	/** The e. */
	ScheduledThreadPoolExecutor e;

	/**
	 * Instantiates a new animation task.
	 *
	 * @param pl the pl
	 */
	public AnimationTask(DirectionalAnimation pl) {
		this.pl = pl;
		// this.e = e;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		pl.nextFrame();
		GameController.getInstance().fightController.postInvalidate();
		// GameController.getInstance().fightController.postInvalidate(pl.getBounds());
		// e.schedule(this, 100, TimeUnit.MILLISECONDS);
	}

}
