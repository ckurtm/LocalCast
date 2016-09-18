package com.peirr.localcast.demo;

import android.app.Activity;
import android.content.Context;

import com.peirr.cast.CastDevice;
import com.peirr.cast.CastDeviceManager;
import com.peirr.http.HttpServer;
import com.peirr.http.IServerRequest;
import com.peirr.http.service.SimpleHttpService;

/**
 * Created by kurt on 2016/09/15.
 */

public class Injection {

    public static CastDevice provideCastDevice(Context context, String nameSpace){
        return new CastDeviceManager(context, nameSpace);
    }

    public static IServerRequest provideHttpRequest(Activity activity){
        return new HttpServer(activity, SimpleHttpService.generatePort());
    }
}
