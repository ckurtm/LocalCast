package com.peirr.localcast.demo;

import android.app.Activity;
import android.content.Context;

import com.peirra.cast.CastDevice;
import com.peirra.cast.CastDeviceManager;
import com.peirra.http.HttpServer;
import com.peirra.http.IServerRequest;
import com.peirra.http.service.SimpleHttpService;

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
