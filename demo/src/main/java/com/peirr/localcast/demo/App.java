package com.peirr.localcast.demo;

import android.app.Application;

import com.peirr.localcast.io.LocalCastManager;

/**
 * Created by kurt on 2016/01/29.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocalCastManager.initialize(this,getString(R.string.cast_app_id));
    }
}
