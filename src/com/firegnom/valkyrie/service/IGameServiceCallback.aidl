package com.firegnom.valkyrie.service;

import com.firegnom.valkyrie.map.pathfinding.ParcelablePath;

oneway interface IGameServiceCallback {
    void playerMoved(in String username,in int playerClass, in ParcelablePath p);
    void disconnected();
    void playerDisconnected(in String username);
    void uploadChanged(in int bytes);
    void downloadChanged(in int bytes);
    void positionChanged(in String userName,in int x ,in int y,in int playerClass);
    void playerInfoReciaved(in int playerClass,in String zone,in int x ,in int y);
    void playerPositionsMessageRecieaved(in String [] userNames ,in int [] x ,in int [] y,in int[] playerClass);
}
