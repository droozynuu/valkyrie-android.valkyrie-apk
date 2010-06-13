package com.firegnom.valkyrie.service;

import com.firegnom.valkyrie.service.ILoginCallback;

interface ILoginService {

    void login(ILoginCallback cb,String username,String password);
    boolean isLoggedIn();
    boolean isPlayerCreated();
    void unregisterCallback();
    
}
