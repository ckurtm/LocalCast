package com.peirra.cast.local;

import android.view.Menu;

import com.peirra.common.MvpPresenter;
import com.peirra.common.MvpView;

/**
 * Created by kurt on 2016/09/15.
 */

public class LocalContract {

    public interface View extends MvpView {
        void showCastConnected(boolean connected);
        void showServerConnected(boolean connected,String endpoint);
        void showMessage(String message);
    }

    interface Presenter extends MvpPresenter<View> {
        void addMenu(Menu menu,int menuItemid);
        void post(String message);
    }
}
