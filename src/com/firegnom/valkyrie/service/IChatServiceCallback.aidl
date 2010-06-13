package com.firegnom.valkyrie.service;


oneway interface IChatServiceCallback {
  void messageRecieaved(String username,String message);
  void chatUserJoined(String username);
  void chatUserLeft(String username);
}