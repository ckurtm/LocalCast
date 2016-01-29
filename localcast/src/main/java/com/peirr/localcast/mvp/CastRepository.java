package com.peirr.localcast.mvp;


import com.peirr.localcast.io.CastConnectionListener;
import com.peirr.localcast.io.CastMessageListener;

/**
 * Created by kurt on 2015/11/24.
 */
public interface CastRepository {
    boolean post(String json);
    void attach(CastConnectionListener connectionListener, CastMessageListener messageListener);
    void detach();
}
