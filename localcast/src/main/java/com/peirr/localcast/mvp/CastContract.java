package com.peirr.localcast.mvp;


import com.peirr.localcast.io.CastMessageUtils;

/**
 * Created by kurt on 2015/11/24.
 */
public class CastContract {

    public interface View {
        void showCastConnected(boolean connected);
        void showCastError();
    }

    public interface ActionsListener {
        void post(CastMessageUtils.CastMessage message);
        void attach();
        void detach();
    }
}
