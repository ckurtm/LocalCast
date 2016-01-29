package com.peirr.localcast.mvp;

import android.util.Log;

import com.google.android.gms.cast.CastDevice;
import com.peirr.localcast.io.CastMessageUtils;
import com.peirr.localcast.io.CastConnectionListener;
import com.peirr.localcast.io.CastMessageListener;

/**
 * Created by kurt on 2015/11/24.
 */
public class CastPresenter implements CastContract.ActionsListener {
    private String TAG = CastPresenter.class.getSimpleName();

    private final CastRepository repository;
    private final CastContract.View view;
    private CastMessageListener listener;

    private CastMessageListener messageListener = new CastMessageListener(){
        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
            Log.d(TAG,"onMessageReceived() [message:"+ message + "]");
            if (listener != null) {
                listener.onMessageReceived(castDevice,namespace,message);
            }
        }
    };

    private CastConnectionListener connectionListener = new CastConnectionListener(){
        @Override
        public void onConnected() {
            view.showCastConnected(true);
        }

        @Override
        public void onDisconnected() {
            view.showCastConnected(false);
        }
    };

    public void setListener(CastMessageListener listener) {
        this.listener = listener;
    }

    public CastPresenter(CastRepository repository, CastContract.View view) {
        this.repository = repository;
        this.view = view;
    }

    @Override
    public void post(CastMessageUtils.CastMessage message) {
        if(!repository.post(CastMessageUtils.toJson(message))){
            view.showCastError();
        }
    }

    @Override
    public void attach() {
        repository.attach(connectionListener,messageListener);
    }

    @Override
    public void detach() {
        repository.detach();
    }
}
