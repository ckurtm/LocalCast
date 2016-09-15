package com.peirra.http;

import android.app.Activity;
import android.content.Intent;

import com.peirra.http.service.ISimpleHttpServiceServer;
import com.peirra.http.service.SimpleHttpInfo;
import com.peirra.http.service.SimpleHttpService;
import com.peirra.http.service.SimpleHttpServiceClient;


/**
 * Created by kurt on 2015/11/24.
 */
public class HttpServer implements IServerRequest,ISimpleHttpServiceServer {

    private final Activity activity;
    private SimpleHttpServiceClient http;
    private ISimpleHttpServiceServer listener;
    private final int port;

    public HttpServer(Activity activity, int port) {
        this.activity = activity;
        this.port = port;
        http =  SimpleHttpServiceClient.createStub(activity,this);
    }

    @Override
    public void startService() {
        Intent intent = new Intent(activity.getApplicationContext(), SimpleHttpService.class);
        intent.putExtra(SimpleHttpService.COMMAND, SimpleHttpService.CMD_BOOTUP);
        activity.startService(intent);
    }

    @Override
    public void stopService() {
        Intent intent = new Intent(activity.getApplicationContext(), SimpleHttpService.class);
        intent.putExtra(SimpleHttpService.COMMAND, SimpleHttpService.CMD_SHUTDOWN);
        activity.startService(intent);
    }

    @Override
    public void bootup() {
        http.bootup(port);
    }

    @Override
    public void shutdown() {
        http.shutdown();
    }

    @Override
    public void info() {
        http.info(port);
    }

    @Override
    public void connect() {
        http.connect();
    }

    @Override
    public void disconnect() {
        http.disconnect();
    }

    @Override
    public void onHttpServerStateChanged(int state, SimpleHttpInfo info) {
        if (listener != null) {
            listener.onHttpServerStateChanged(state,info);
        }
    }

    @Override
    public void onBoundServiceConnectionChanged(boolean connected) {
        if (listener != null) {
            listener.onBoundServiceConnectionChanged(connected);
        }
    }

    public void setListener(ISimpleHttpServiceServer listener) {
        this.listener = listener;
    }
}
