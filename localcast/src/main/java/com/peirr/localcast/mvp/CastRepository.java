package com.peirr.localcast.mvp;


import com.google.android.gms.cast.MediaInfo;
import com.peirr.localcast.io.CastConnectionListener;
import com.peirr.localcast.io.CastDataMessageListener;
import com.peirr.localcast.io.CastVideoMessageListener;

/**
 * Created by kurt on 2015/11/24.
 */
public interface CastRepository {
    boolean post(String json);
    void attach(CastConnectionListener connectionListener, CastDataMessageListener dataListener, CastVideoMessageListener videoListener);
    void detach();
    void play(MediaInfo info);
    boolean isDatacentric();
}
