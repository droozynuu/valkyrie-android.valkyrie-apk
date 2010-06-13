package com.firegnom.valkyrie.service;

oneway interface ILoginCallback {
    void loggedIn(in int activity);
    void loginFailed();
    void disconnected();
}
