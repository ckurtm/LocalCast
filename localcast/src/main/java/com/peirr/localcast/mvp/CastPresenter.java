package com.peirr.localcast.mvp;

import android.util.Log;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.MediaInfo;
import com.peirr.localcast.io.CastMessageUtils;
import com.peirr.localcast.io.CastConnectionListener;
import com.peirr.localcast.io.CastDataMessageListener;
import com.peirr.localcast.io.CastVideoMessageListener;

/**
 * Created by kurt on 2015/11/24.
 */
public class CastPresenter implements CastContract.ActionsListener {
    private String TAG = CastPresenter.class.getSimpleName();

    private final CastRepository repository;
    private final CastContract.View view;
    private CastDataMessageListener dataListener;
    private CastVideoMessageListener videoListener;

    private CastDataMessageListener dataMessageListener = new CastDataMessageListener(){
        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
            Log.d(TAG,"onMessageReceived() [message:"+ message + "]");
            if (dataListener != null) {
                dataListener.onMessageReceived(castDevice,namespace,message);
            }
        }
    };

    private CastVideoMessageListener videoMessageListener = new CastVideoMessageListener(){
        @Override
        public void onDataMessageReceived(String message) {
            super.onDataMessageReceived(message);
            Log.d(TAG,"onMessageReceived() [message:"+ message + "]");
            if (videoListener != null) {
                videoListener.onDataMessageReceived(message);
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

    public void setDataListener(CastDataMessageListener dataListener) {
        this.dataListener = dataListener;
    }

    public void setVideoListener(CastVideoMessageListener videoListener) {
        this.videoListener = videoListener;
    }

    public CastPresenter(CastRepository repository, CastContract.View view) {
        this.repository = repository;
        this.view = view;
    }

    @Override
    public void post(String json) {
        if(!repository.post(json)){
            view.showCastError();
        }
    }

    @Override
    public void play(MediaInfo info) {
        repository.play(info);
    }

    @Override
    public void attach() {
        repository.attach(connectionListener,(repository.isDatacentric()?dataMessageListener:null),(repository.isDatacentric()?null:videoMessageListener));
    }

    @Override
    public void detach() {
        repository.detach();
    }
}
