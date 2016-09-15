package com.peirra.cast;

import android.view.Menu;

import com.peirra.common.MvpPresenter;
import com.peirra.common.MvpView;


public interface CastContract {

    interface View extends MvpView {
        void showCastConnected();
        void showCastDisconnected();
        void showMessageReceived(String nameSpace, String json);
    }

    interface Presenter extends MvpPresenter<View> {
        void attach();
        void detach();
        void attachMenu(Menu menu, int menuItemId);
        void post(String json);
    }

}