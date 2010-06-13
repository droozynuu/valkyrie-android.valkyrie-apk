package com.firegnom.valkyrie.service;

oneway interface ILoaderCallback {
    void loadComplete();
    void loadFailed();
}
