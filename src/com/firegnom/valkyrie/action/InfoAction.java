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

import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.Gui;

// TODO: Auto-generated Javadoc
/**
 * The Class InfoAction.
 */
public class InfoAction implements ActionTask {
	
	/** The action. */
	ContextAction action;

	/**
	 * Instantiates a new info action.
	 *
	 * @param action the action
	 */
	public InfoAction(ContextAction action) {
		this.action = action;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.action.ActionTask#execute()
	 */
	@Override
	public void execute() {
		GameController gc = GameController.getInstance();
		if (gc.gui.question("are you sure ?")) {
			Gui.toast("player info !!!!");
		}
	}

}
