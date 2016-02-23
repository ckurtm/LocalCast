package com.peirr.localcast.mvp;

import android.content.Context;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.peirr.localcast.io.CastConnectionListener;
import com.peirr.localcast.io.CastDataMessageListener;
import com.peirr.localcast.io.CastVideoMessageListener;

import java.io.IOException;

/**
 * Created by kurt on 2015/11/24.
 */
public class CastRepositories implements CastRepository {
    String TAG = CastRepositories.class.getSimpleName();
    private final String namespace;
    private BaseCastManager manager;
    private boolean isCustom;
    private final Context context;

    public CastRepositories(Context context,String namespace,boolean isCustom) {
        this.namespace = namespace;
        this.context = context;
        this.isCustom = isCustom;
        if(isCustom) {
            manager = DataCastManager.getInstance();
        }else{
            manager = VideoCastManager.getInstance();
        }
    }

    @Override
    public boolean post(String json) {
        if(manager.isConnected()) {
            try {
                if(isCustom) {
                    ((DataCastManager)manager).sendDataMessage(json, namespace);
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void attach(CastConnectionListener connectionListener, CastDataMessageListener dataMessageListener, CastVideoMessageListener videoMessageListener) {
        if (dataMessageListener != null) {
            ((DataCastManager)manager).addDataCastConsumer(dataMessageListener);
        }else if(videoMessageListener != null){
            ((VideoCastManager)manager).addVideoCastConsumer(videoMessageListener);
        }
        manager.addBaseCastConsumer(connectionListener);
        manager.incrementUiCounter();
    }

    @Override
    public void detach() {
        manager.decrementUiCounter();
    }


    @Override
    public void play(MediaInfo info) {
        VideoCastManager.getInstance().startVideoCastControllerActivity(context,info,0,true);
    }

    public boolean isDatacentric() {
        return isCustom;
    }
}
