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
package com.firegnom.valkyrie.action;

import java.util.concurrent.LinkedBlockingQueue;

// TODO: Auto-generated Javadoc
/**
 * The Class ActionManager.
 */
public class ActionManager extends Thread {
	
	/** The Constant INFO. */
	public static final int INFO = 0;
	
	/** The Constant ATTACK. */
	public static final int ATTACK = 1;
	
	/** The queue. */
	LinkedBlockingQueue<ActionTask> queue;

	/**
	 * Instantiates a new action manager.
	 */
	public ActionManager() {
		queue = new LinkedBlockingQueue<ActionTask>();
		start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				ActionTask task = queue.take();
				task.execute();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			;

		}
	}

	/**
	 * Execute.
	 *
	 * @param action the action
	 */
	public void execute(ContextAction action) {
		switch (action.actionId) {
		case INFO:
			queue.add(new InfoAction(action));
			break;
		case ATTACK:

			queue.add(new AttackAction(action));
			break;

		default:
			break;
		}

	}
}
