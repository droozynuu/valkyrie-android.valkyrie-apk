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

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.RemoteException;
import android.util.Log;

import com.firegnom.valkyrie.map.pathfinding.ParcelablePath;
import com.firegnom.valkyrie.net.protocol.ChangeGameMode;
import com.firegnom.valkyrie.net.protocol.ChatMessage;
import com.firegnom.valkyrie.net.protocol.ChatUserJoined;
import com.firegnom.valkyrie.net.protocol.ChatUserLeft;
import com.firegnom.valkyrie.net.protocol.CreateUserMessage;
import com.firegnom.valkyrie.net.protocol.PlayerDisconnected;
import com.firegnom.valkyrie.net.protocol.PlayerInfoMessage;
import com.firegnom.valkyrie.net.protocol.PlayerMove;
import com.firegnom.valkyrie.net.protocol.PlayerPositionMessage;
import com.firegnom.valkyrie.net.protocol.PlayerPositionsMessage;
import com.firegnom.valkyrie.net.protocol.RequestPlayerInfoMessage;
import com.firegnom.valkyrie.net.protocol.RequestPlayersPositionMessage;
import com.firegnom.valkyrie.net.protocol.helper.Protocol;
import com.firegnom.valkyrie.share.constant.GameModes;
import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;

// TODO: Auto-generated Javadoc
/**
 * The Class ValkyrieServerConnection.
 */
public class ValkyrieServerConnection extends ServerMesageListener implements
		SimpleClientListener {
	// protected static final String SERVER_PORT = "1139";
	/** The Constant SERVER_PORT. */
	protected static final String SERVER_PORT = "59000";
	
	/** The Constant SERVER_IP. */
	private static final String SERVER_IP = "74.213.176.198";
	// private static final String SERVER_IP = "192.168.1.5";

	// private static final String SERVER_IP = "9.161.215.65";
	// private static final String SERVER_IP = "84.203.32.34";

	/** The Constant TAG. */
	private static final String TAG = "ValkyrieServerConnection";
	
	/** The service. */
	ValkyrieService service;

	/** The upload. */
	static int upload = 0;
	
	/** The download. */
	static int download = 0;
	
	/** The protocol. */
	Protocol protocol;
	
	/** The logged in. */
	boolean loggedIn = false;
	
	/** The game mode. */
	public int gameMode;
	
	/** The simple client. */
	SimpleClient simpleClient;

	// callbacks
	/** The login callback. */
	ILoginCallback loginCallback = null;
	
	/** The gc. */
	IGameServiceCallback gc = null;

	/** The channel number sequence. */
	protected final AtomicInteger channelNumberSequence = new AtomicInteger(1);

	/**
	 * Instantiates a new valkyrie server connection.
	 *
	 * @param service the service
	 */
	public ValkyrieServerConnection(final ValkyrieService service) {
		this.service = service;
		protocol = new Protocol();
		protocol.registerMessageListener(this);
	}

	/**
	 * Creates the user.
	 *
	 * @param selectedClass the selected class
	 */
	public void createUser(final int selectedClass) {
		final CreateUserMessage cum = new CreateUserMessage();
		cum.playerClass = selectedClass;
		try {
			simpleClient.send(Protocol.encode(cum));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.sun.sgs.client.ServerSessionListener#disconnected(boolean, java.lang.String)
	 */
	@Override
	public void disconnected(final boolean arg0, final String arg1) {
		loggedIn = false;
		Log.d(TAG, "disconnected");
		// service.simpleClient.;
		if (gc != null) {
			try {
				gc.disconnected();
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (loginCallback != null) {
			try {
				loginCallback.disconnected();
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/* (non-Javadoc)
	 * @see com.sun.sgs.client.simple.SimpleClientListener#getPasswordAuthentication()
	 */
	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		final Random random = new Random();
		String player;
		if (service.username == null || service.username.trim().equals("")) {
			player = "guest-" + random.nextInt(10000);
			service.username = player;
		} else {
			player = service.username;
			service.username = player;
		}

		final String password = "guest";
		return new PasswordAuthentication(player, password.toCharArray());
	}

	/* (non-Javadoc)
	 * @see com.sun.sgs.client.ServerSessionListener#joinedChannel(com.sun.sgs.client.ClientChannel)
	 */
	@Override
	public ClientChannelListener joinedChannel(final ClientChannel channel) {
		return null;
	}

	/**
	 * Log download.
	 *
	 * @param bytes the bytes
	 */
	void logDownload(final int bytes) {
		download += bytes;
		if (gc == null) {
			return;
		}
		try {

			gc.downloadChanged(download);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.sun.sgs.client.simple.SimpleClientListener#loggedIn()
	 */
	@Override
	public void loggedIn() {
		loggedIn = true;
		Log.d(TAG, "loggedIn");
	}

	/**
	 * Login.
	 *
	 * @param cb the cb
	 * @param username the username
	 * @param password the password
	 */
	public void login(final ILoginCallback cb, final String username,
			final String password) {
		loginCallback = cb;
		simpleClient = new SimpleClient(this);
		service.username = username;
		try {
			final Properties connectProps = new Properties();
			connectProps.put("host", SERVER_IP);
			connectProps.put("port", SERVER_PORT);
			simpleClient.login(connectProps);
		} catch (final Exception e) {
			e.printStackTrace();
			sendException(e);
		}

	}

	/* (non-Javadoc)
	 * @see com.sun.sgs.client.simple.SimpleClientListener#loginFailed(java.lang.String)
	 */
	@Override
	public void loginFailed(final String arg0) {
		loggedIn = false;
		Log.d(TAG, "loginFailed");
		if (loginCallback != null) {
			try {
				loginCallback.loginFailed();
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Logout.
	 */
	public void logout() {
		if (loggedIn) {
			simpleClient.logout(true);
			loggedIn = false;
		}
	}

	/**
	 * Log upload.
	 *
	 * @param bytes the bytes
	 */
	void logUpload(final int bytes) {
		upload += bytes;
		if (gc == null) {
			return;
		}
		try {
			gc.uploadChanged(upload);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Move.
	 *
	 * @param p the p
	 * @param playerClass the player class
	 * @throws RemoteException the remote exception
	 */
	public void move(final ParcelablePath p, final int playerClass)
			throws RemoteException {
		Log.d(TAG, p.toString());
		final PlayerMove pm = new PlayerMove();
		pm.playerName = service.username;
		pm.playerClass = playerClass;
		pm.setPath(p.convertToNetPath());
		try {
			final ByteBuffer bb = Protocol.encode(pm);
			logUpload(bb.remaining());
			simpleClient.send(bb);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendException(e);
		}

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.ServerMesageListener#received(com.firegnom.valkyrie.net.protocol.ChangeGameMode)
	 */
	@Override
	public void received(final ChangeGameMode customType) {
		Log.d(TAG, "received ChangeGameMode");
		gameMode = customType.getType();
		if (customType.getType() == GameModes.CREATE_PLAYER_MODE) {
			try {
				loginCallback.loggedIn(GameModes.CREATE_PLAYER_MODE);
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		if (customType.getType() == GameModes.MAP_MODE) {
			try {
				if (loginCallback != null) {
					loginCallback.loggedIn(GameModes.MAP_MODE);
				}
				if (service.createUserCallback != null) {
					service.createUserCallback.goToMap();
				}

			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.ServerMesageListener#received(com.firegnom.valkyrie.net.protocol.ChatMessage)
	 */
	@Override
	public void received(final ChatMessage cm) {
		if (service.chatCallback == null) {
			Log.d(TAG, "received Chat message but it is dropped");
		}
		try {
			Log.d(TAG, "received Chat message");
			if (cm.username.equals(service.username)) {
				Log.d(TAG, "received Chat message from myself");
				return;
			}
			service.chatCallback.messageRecieaved(cm.username, cm.message);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.ServerMesageListener#received(com.firegnom.valkyrie.net.protocol.ChatUserJoined)
	 */
	@Override
	public void received(final ChatUserJoined cuj) {
		if (service.chatCallback == null) {
			Log.d(TAG, "received Chat message but it is dropped");
		}
		if (cuj.username.equals(service.username)) {
			return;
		}
		try {
			service.chatCallback.chatUserJoined(cuj.username);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.ServerMesageListener#received(com.firegnom.valkyrie.net.protocol.ChatUserLeft)
	 */
	@Override
	public void received(final ChatUserLeft cul) {
		if (service.chatCallback == null) {
			Log.d(TAG, "received Chat message but it is dropped");
		}
		if (cul.username.equals(service.username)) {
			return;
		}
		try {
			service.chatCallback.chatUserLeft(cul.username);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.ServerMesageListener#received(com.firegnom.valkyrie.net.protocol.PlayerDisconnected)
	 */
	@Override
	public void received(final PlayerDisconnected customType) {
		Log.d(TAG, "received - PlayerDisconnected");
		try {
			gc.playerDisconnected(customType.playerName);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendException(e);
		}

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.ServerMesageListener#received(com.firegnom.valkyrie.net.protocol.PlayerInfoMessage)
	 */
	@Override
	public void received(final PlayerInfoMessage pim) {
		Log.d(TAG, "received PlayerInfoMessage");
		try {
			gc.playerInfoReciaved(pim.playerClass, pim.zoneName,
					pim.position.x, pim.position.y);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.ServerMesageListener#received(com.firegnom.valkyrie.net.protocol.PlayerMove)
	 */
	@Override
	public void received(final PlayerMove pm) {
		if (pm.playerName.equals(service.username)) {
			Log.d(TAG, "Recieaved my move message");
			return;
		}

		Log.d(TAG, "" + pm.playerName + " moved path length = "
				+ pm.path.step.length);
		if (gc != null) {
			try {
				gc.playerMoved(pm.playerName, pm.playerClass,
						new ParcelablePath(pm.path));
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.ServerMesageListener#received(com.firegnom.valkyrie.net.protocol.PlayerPositionMessage)
	 */
	@Override
	public void received(final PlayerPositionMessage pos) {
		Log.d(TAG, "received PlayerPositionMessage");
		try {
			gc.positionChanged(pos.userName, pos.x, pos.y, pos.playerClass);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.firegnom.valkyrie.service.ServerMesageListener#received(com.firegnom.valkyrie.net.protocol.PlayerPositionsMessage)
	 */
	@Override
	public void received(final PlayerPositionsMessage customType) {
		Log.d(TAG, "recieaved PlayerPositionsMessage");
		if (gc == null) {
			return;
		}
		final String[] userNames = new String[customType.playerPositionMessage.length];
		final int[] x = new int[customType.playerPositionMessage.length];
		final int[] y = new int[customType.playerPositionMessage.length];
		final int[] playerClass = new int[customType.playerPositionMessage.length];

		for (int i = 0; i < userNames.length; i++) {
			userNames[i] = customType.playerPositionMessage[i].userName;
			x[i] = customType.playerPositionMessage[i].x;
			y[i] = customType.playerPositionMessage[i].y;
			playerClass[i] = customType.playerPositionMessage[i].playerClass;
		}
		try {
			gc.playerPositionsMessageRecieaved(userNames, x, y, playerClass);
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.sun.sgs.client.ServerSessionListener#receivedMessage(java.nio.ByteBuffer)
	 */
	@Override
	public void receivedMessage(final ByteBuffer arg0) {
		Log.d(TAG, "receivedMessage");
		logDownload(arg0.remaining());
		protocol.decode(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.sgs.client.ServerSessionListener#reconnected()
	 */
	@Override
	public void reconnected() {
		Log.d(TAG, "reconnected");
	}

	/* (non-Javadoc)
	 * @see com.sun.sgs.client.ServerSessionListener#reconnecting()
	 */
	@Override
	public void reconnecting() {
		Log.d(TAG, "reconnecting");

	}

	/**
	 * Register game service callback.
	 *
	 * @param callback the callback
	 */
	public void registerGameServiceCallback(final IGameServiceCallback callback) {
		gc = callback;

	}

	/**
	 * Request players positions.
	 */
	public void requestPlayersPositions() {
		final RequestPlayersPositionMessage rppm = new RequestPlayersPositionMessage();
		try {
			simpleClient.send(Protocol.encode(rppm));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Send change game mode.
	 *
	 * @param type the type
	 */
	public void sendChangeGameMode(final int type) {
		final ChangeGameMode cgm = new ChangeGameMode();
		cgm.type = type;
		try {
			simpleClient.send(Protocol.encode(cgm));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Send chat message.
	 *
	 * @param message the message
	 */
	public void sendChatMessage(final String message) {
		final ChatMessage cm = new ChatMessage();
		cm.username = service.username;
		cm.message = message;
		try {
			final ByteBuffer bb = Protocol.encode(cm);
			logUpload(bb.remaining());
			simpleClient.send(bb);
		} catch (final IOException e) {
			e.printStackTrace();
			sendException(e);
		}

	}

	/**
	 * Send exception.
	 *
	 * @param e the e
	 */
	private void sendException(final Exception e) {
		// TODO Auto-generated method stub
		Log.d(TAG, "FIXME !!!!  handle sendException", e);
	}

	/**
	 * Sendjoin chat message.
	 */
	public void sendjoinChatMessage() {
		final ChatUserJoined cuj = new ChatUserJoined();
		cuj.username = service.username;
		try {
			final ByteBuffer bb = Protocol.encode(cuj);
			logUpload(bb.remaining());
			simpleClient.send(bb);
		} catch (final IOException e) {
			e.printStackTrace();
			sendException(e);
		}
	}

	/**
	 * Sendleave chat message.
	 */
	public void sendleaveChatMessage() {
		final ChatUserLeft cul = new ChatUserLeft();
		cul.username = service.username;
		try {
			final ByteBuffer bb = Protocol.encode(cul);
			logUpload(bb.remaining());
			simpleClient.send(bb);
		} catch (final IOException e) {
			e.printStackTrace();
			sendException(e);
		}
	}

	/**
	 * Send request player info.
	 */
	public void sendRequestPlayerInfo() {
		final RequestPlayerInfoMessage cum = new RequestPlayerInfoMessage();
		try {
			simpleClient.send(Protocol.encode(cum));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Unregister game service callback.
	 */
	public void unregisterGameServiceCallback() {
		gc = null;

	}

}
