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
 * The Interface FightAction.
 */
public interface FightAction {
	
	/**
	 * Activated.
	 */
	public void activated();

	/**
	 * Deactivated.
	 */
	public void deactivated();

	/**
	 * Finished.
	 */
	public void finished();

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive();

	/**
	 * On single tap up.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void onSingleTapUp(int x, int y);
}
