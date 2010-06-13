package com.firegnom.valkyrie.service;

import com.firegnom.valkyrie.map.pathfinding.ParcelablePath;
import com.firegnom.valkyrie.service.IGameServiceCallback;
import com.firegnom.valkyrie.service.ICreateUserCallback;


interface IGameService {

    void move(in ParcelablePath p,int playerClass);
    String getUsername();
    void registerCallback(IGameServiceCallback callback);
    void unregisterCallback();
    void unregisterCreateUserCallback();
    void exit();
    void createUser(ICreateUserCallback callback,int selectedClass);
    void requestPlayerInfo();
    void requestPlayersPositions();
    void changeGameMode(int mode);
}