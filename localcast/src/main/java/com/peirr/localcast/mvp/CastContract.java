package com.peirr.localcast.mvp;


import com.google.android.gms.cast.MediaInfo;

/**
 * Created by kurt on 2015/11/24.
 */
public class CastContract {

    public interface View {
        void showCastConnected(boolean connected);
        void showCastError();
    }

    public interface ActionsListener {
        void post(String json);
        void play(MediaInfo info);
        void attach();
        void detach();
    }
}
