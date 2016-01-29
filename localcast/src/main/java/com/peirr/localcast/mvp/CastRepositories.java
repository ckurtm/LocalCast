package com.peirr.localcast.mvp;

import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.peirr.localcast.io.CastConnectionListener;
import com.peirr.localcast.io.CastMessageListener;


import java.io.IOException;

/**
 * Created by kurt on 2015/11/24.
 */
public class CastRepositories implements CastRepository {
    String TAG = CastRepositories.class.getSimpleName();
    private final String namespace;
    private DataCastManager manager;

    public CastRepositories(String namespace) {
        this.namespace = namespace;
        manager = DataCastManager.getInstance();
    }

    @Override
    public boolean post(String json) {
        if(manager.isConnected()) {
            try {
                manager.sendDataMessage(json, namespace);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void attach(CastConnectionListener connectionListener, CastMessageListener messageListener) {
        manager.addDataCastConsumer(messageListener);
        manager.addBaseCastConsumer(connectionListener);
        manager.incrementUiCounter();
    }

    @Override
    public void detach() {
        manager.decrementUiCounter();
    }
}
