package com.firegnom.valkyrie.service;

import com.firegnom.valkyrie.service.ILoaderCallback;
import com.firegnom.valkyrie.service.IQueuleChangeListener;
import com.firegnom.valkyrie.service.IPackLoadListener;


interface IResourceLoaderService {
    void download(String name ,ILoaderCallback callback);
    void registerQueuleChangeListener(IQueuleChangeListener listener);
    void addToDownloadQueue(String name);
    void downloadPacks(IPackLoadListener listener);
}
