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
package com.firegnom.valkyrie.engine.fight;

// TODO: Auto-generated Javadoc
/**
 * The Class TurnThread.
 */
public class TurnThread extends Thread {
	
	/** The progress. */
	public int progress = 0;
	
	/** The speed. */
	public int speed = 20;
	
	/** The observer. */
	ProgressObserver observer;
	
	/** The runing. */
	private final boolean runing = true;

	/**
	 * Instantiates a new turn thread.
	 */
	public TurnThread() {
		start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (runing) {
			if (progress >= 0) {
				progress -= speed;
				if (observer != null) {
					observer.progressChanged(progress);
				}
			}
			try {
				sleep(200);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

		}
	}

	/**
	 * Start turn.
	 *
	 * @param po the po
	 * @param progress the progress
	 */
	public void startTurn(final ProgressObserver po, final int progress) {
		observer = po;
		this.progress = progress;

	}
}
