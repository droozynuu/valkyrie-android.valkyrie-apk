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

import android.content.Intent;

import com.firegnom.valkyrie.FightActivity;
import com.firegnom.valkyrie.GameActivity;
import com.firegnom.valkyrie.R;
import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.Player;

// TODO: Auto-generated Javadoc
/**
 * The Class AttackAction.
 */
public class AttackAction implements ActionTask {
	
	/** The action. */
	ContextAction action;

	/**
	 * Instantiates a new attack action.
	 *
	 * @param action the action
	 */
	public AttackAction(ContextAction action) {
		this.action = action;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.action.ActionTask#execute()
	 */
	@Override
	public void execute() {
		GameController gc = GameController.getInstance();
		Player p = gc.players.get(action.container);

		if (gc.gui.question(gc.getString(
				R.string.are_you_sure_you_want_to_attack_playername_).replace(
				"__playerName__", p.name))) {
			Intent i = new Intent(gc.context, FightActivity.class);
			i.putExtra(FightActivity.PLAYER_EXTRA, p.name);
			gc.fightController.enemy = p;
			gc.context.startActivity(i);
			((GameActivity) gc.context).finish();
		}
	}
}
