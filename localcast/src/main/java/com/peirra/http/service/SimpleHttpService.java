package com.peirra.http.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.peirra.http.server.SocketThread;

import java.lang.ref.WeakReference;
import java.security.SecureRandom;

import mbanje.kurt.remote_service.RemoteService;
import mbanje.kurt.remote_service.RemoteServiceType;

/**
 * Created by kurt on 2015/11/23.
 */
@RemoteService(RemoteServiceType.STARTED_BOUND)
public class SimpleHttpService extends Service implements ISimpleHttpServiceClient {

    String TAG = SimpleHttpService.class.getSimpleName();

    public static final int STATE_RUNNING = 1;
    public static final int STATE_STOPPED = 2;
    public static final int STATE_ERROR = 3;

    public static final int REQUEST_START = 12;
    public static final int REQUEST_STOP = 14;
    public static final int REQUEST_INFO = 16;

    private static final int PORT_MIN = 9950;
    private static final int PORT_MAX = 9999;

    public static final String COMMAND = "command";
    public static final int CMD_SHUTDOWN = 20;
    public static final int CMD_BOOTUP = 22;


    private String ip;
    private static int port = -1;
    private SimpleHttpServiceConnector connector;
    private SocketThread server;
    private String serverRoot = "";
    private Handler serverHandler = new ServerHandler(this);
    private static SecureRandom random = new SecureRandom();
    private int currentState = STATE_STOPPED;
    private String message = null;

    public SimpleHttpService() {
        this.connector = new SimpleHttpServiceConnector(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return connector.getBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        port = generatePort();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final int command = intent.getIntExtra(COMMAND, -1);
            switch (command) {
                case CMD_BOOTUP:
                    bootup(port);
                    break;
                case CMD_SHUTDOWN:
                    shutdown();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void bootup(int port) {
        Log.d(TAG, "bootup()");
        try{
            if(port != 0){
                SimpleHttpService.port = port;
            }
            SimpleHttpInfo info = getInfo(this,ip, SimpleHttpService.port);
            ip = info.ip;
            server = new SocketThread(serverHandler, serverRoot,info.ip,info.port);
            server.start();
            currentState = STATE_RUNNING;
            Log.d(TAG, "boot success...");
        } catch (Exception e) {
            currentState = STATE_ERROR;
            message = e.getMessage();
            Log.d(TAG,"boot failure...");
            Log.e(TAG, Log.getStackTraceString(e));
        }
        connector.send(currentState,new SimpleHttpInfo(ip,port,message));
    }

    @Override
    public void shutdown() {
        Log.d(TAG,"shutdown()");
        currentState = STATE_STOPPED;
        if(server != null){
            server.stopServer();
        }
        SimpleHttpInfo info = getInfo(this,ip,port);
        this.ip = info.ip;
        connector.send(STATE_STOPPED, info);
        server = null;
        stopSelf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(currentState == STATE_RUNNING){
            shutdown();
        }
    }

    @Override
    public void info(int port) {
        if(port != 0){
            SimpleHttpService.port = port;
        }
        Log.d(TAG,"info()");
        SimpleHttpInfo info = getInfo(this,ip,port);
        this.ip = info.ip;
        connector.send(currentState, info);
    }

    public static String getIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() != SupplicantState.COMPLETED) {
            return null;
        }
        return intToIp(wifiInfo.getIpAddress());
    }

    public static int generatePort(){
        if(port != -1)
            return port;
        return randomPort(PORT_MIN,PORT_MAX);
    }

    public static SimpleHttpInfo getInfo(Context context, String ip, int port){
        if (TextUtils.isEmpty(ip)) {
            ip = getIp(context);
        }
        return new SimpleHttpInfo(ip, port,null);
    }

    private static String intToIp(int i) {
        return ((i) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }


    private static class ServerHandler extends Handler {
        final WeakReference<SimpleHttpService> reference;
        private ServerHandler(SimpleHttpService service) {
            this.reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private static int randomPort(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
