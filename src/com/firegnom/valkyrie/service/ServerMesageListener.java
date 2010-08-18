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
package com.firegnom.valkyrie.service;

import com.firegnom.valkyrie.net.protocol.ChangeGameMode;
import com.firegnom.valkyrie.net.protocol.ChatMessage;
import com.firegnom.valkyrie.net.protocol.ChatUserJoined;
import com.firegnom.valkyrie.net.protocol.ChatUserLeft;
import com.firegnom.valkyrie.net.protocol.ConfirmMove;
import com.firegnom.valkyrie.net.protocol.CreateUserMessage;
import com.firegnom.valkyrie.net.protocol.PlayerDisconnected;
import com.firegnom.valkyrie.net.protocol.PlayerInfoMessage;
import com.firegnom.valkyrie.net.protocol.PlayerMove;
import com.firegnom.valkyrie.net.protocol.PlayerPositionMessage;
import com.firegnom.valkyrie.net.protocol.PlayerPositionsMessage;
import com.firegnom.valkyrie.net.protocol.RequestPlayerInfoMessage;
import com.firegnom.valkyrie.net.protocol.RequestPlayersPositionMessage;
import com.firegnom.valkyrie.net.protocol.helper.MessageListener;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving serverMesage events.
 * The class that is interested in processing a serverMesage
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addServerMesageListener<code> method. When
 * the serverMesage event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ServerMesageEvent
 */
public class ServerMesageListener implements MessageListener {

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.PlayerMove)
	 */
	@Override
	public void received(PlayerMove customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.ConfirmMove)
	 */
	@Override
	public void received(ConfirmMove customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.PlayerDisconnected)
	 */
	@Override
	public void received(PlayerDisconnected customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.ChatMessage)
	 */
	@Override
	public void received(ChatMessage customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.ChatUserJoined)
	 */
	@Override
	public void received(ChatUserJoined customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.ChatUserLeft)
	 */
	@Override
	public void received(ChatUserLeft customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.CreateUserMessage)
	 */
	@Override
	public void received(CreateUserMessage customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.ChangeGameMode)
	 */
	@Override
	public void received(ChangeGameMode customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.PlayerPositionMessage)
	 */
	@Override
	public void received(PlayerPositionMessage customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.RequestPlayerInfoMessage)
	 */
	@Override
	public void received(RequestPlayerInfoMessage customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.PlayerInfoMessage)
	 */
	@Override
	public void received(PlayerInfoMessage customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.PlayerPositionsMessage)
	 */
	@Override
	public void received(PlayerPositionsMessage customType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.net.protocol.helper.MessageListener#received(com.firegnom.valkyrie.net.protocol.RequestPlayersPositionMessage)
	 */
	@Override
	public void received(RequestPlayersPositionMessage customType) {
		// TODO Auto-generated method stub

	}

}
