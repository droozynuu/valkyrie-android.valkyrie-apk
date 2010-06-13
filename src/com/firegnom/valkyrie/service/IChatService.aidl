package com.firegnom.valkyrie.service;

import com.firegnom.valkyrie.service.IChatServiceCallback;



interface IChatService {
    void registerCallback(IChatServiceCallback callback);
    void send(String message);
    String username();
    void joinChat();
    void leaveChat();
}