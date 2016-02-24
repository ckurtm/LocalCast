package com.peirr.localcast.io;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.peirr.http.mvp.HttpContract;
import com.peirr.http.mvp.HttpPresenter;

import com.peirr.http.mvp.HttpServer;
import com.peirr.http.mvp.IServerRequest;
import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.SimpleHttpService;
import com.peirr.localcast.mvp.CastContract;
import com.peirr.localcast.mvp.CastPresenter;
import com.peirr.localcast.mvp.CastDevice;
import com.peirr.localcast.mvp.ICastRequest;

import java.util.Locale;

/**
 * Created by kurt on 2016/01/29.
 * Handles the state toggling between the cast device and the local http service. When a cast device is connected , this auto starts
 * the local http server. It shuts down the httpserver as soon as the cast device is disconnected.
 */
public class LocalCastManager implements HttpContract.View, CastContract.View {

    private static final String TAG = "LocalCastManager";

    private CastConnectionListener castConnectionListener;
    private HttpConnectionListener httpConnectionListener;
    private boolean castConnected;
    private HttpPresenter http;
    private CastPresenter cast;
    private SimpleHttpInfo httpInfo;
    private boolean isCustom;
    private int version = -1;

    public LocalCastManager(Activity activity, String castNamespace,boolean isCustom) {
        this.isCustom = isCustom;
        IServerRequest server = new HttpServer(activity, SimpleHttpService.generatePort());
        ICastRequest castRequest = new CastDevice(activity,castNamespace, isCustom);
        http = new HttpPresenter(server, this);
        cast = new CastPresenter(castRequest, this);
        getVersionCode(activity);
    }

    public LocalCastManager(Activity activity, String castNamespace, int port,boolean isCustom) {
        this.isCustom = isCustom;
        IServerRequest server = new HttpServer(activity, port);
        ICastRequest castRequest = new CastDevice(activity,castNamespace,isCustom);
        http = new HttpPresenter(server, this);
        cast = new CastPresenter(castRequest, this);
        getVersionCode(activity);
    }

    /**
     * Call for connection state on a cast device
     */
    public interface CastConnectionListener {
        void onCastConnectionChanged(boolean connected, Exception exception);
    }

    /**
     * Callback for state of the local http server
     */
    public interface HttpConnectionListener {
        void onHttpConnectionChanged(int status, SimpleHttpInfo info);
    }

    /**
     * Use this to listen to cast connection state
     *
     * @param castConnectionListener
     */
    public void registerCastConnectionListener(CastConnectionListener castConnectionListener) {
        this.castConnectionListener = castConnectionListener;
    }


    public void unregisterCastConnectionListener() {
        castConnectionListener = null;
    }

    /**
     * set this to listen to changes in local http server state
     *
     * @param httpConnectionListener
     */
    public void registerHttpConnectionListener(HttpConnectionListener httpConnectionListener) {
        this.httpConnectionListener = httpConnectionListener;
    }

    public void unregisterHttpConnectionListener() {
        httpConnectionListener = null;
    }

    @Override
    public void showCastConnected(boolean connected) {
        Log.d(TAG, "showCastConnected() : " + "connected = [" + connected + "]");
        this.castConnected = connected;
        if(connected){
            http.bootup();
        }else{
            http.shutdown();
        }
        if (castConnectionListener != null) {
            castConnectionListener.onCastConnectionChanged(connected, null);
        }
    }

    public boolean isCastConnected() {
        return castConnected;
    }

    @Override
    public void showCastError() {
        if (castConnectionListener != null) {
            castConnectionListener.onCastConnectionChanged(castConnected, new Exception("Cast failed"));
        }
    }

    @Override
    public void showHttpStatus(int status, SimpleHttpInfo info) {
        if(status == SimpleHttpService.STATE_RUNNING){
            httpInfo = info;
            Log.d(TAG,"" + getEndpoint());
        }else{
            httpInfo = null;
        }
        if (httpConnectionListener != null) {
            httpConnectionListener.onHttpConnectionChanged(status, info);
        }
    }

    public String getEndpoint(){
        return "http://" + httpInfo.ip + ":" + httpInfo.port;
    }

    public void post(String message){
        cast.post(message);
    }

    public void play(MediaInfo info){
        cast.play(info);
    }

    private void getVersionCode(Activity activity) {
        if (activity != null) {
            try {
                PackageInfo info  = activity.getPackageManager().getPackageInfo(activity.getPackageName(),0);
                if (info != null) {
                    version = info.versionCode;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG,"failed to determine version code",e);
            }
        }
    }

    /**
     * use this in your activity's onCreate / onResume to bootup this manager
     */
    public void connect() {
        http.connect();
        cast.attach();
    }


    public void addMediaRouterButton(Menu menu, int menuResourceId){
        if(isCustom){
            DataCastManager.getInstance().addMediaRouterButton(menu, menuResourceId);
        }else{
            VideoCastManager.getInstance().addMediaRouterButton(menu, menuResourceId);
        }
    }

    /**
     * disconnect the manager from your activity/fragment using this
     */
    public void disconnect() {
        http.disconnect();
        cast.detach();
        unregisterCastConnectionListener();
        unregisterHttpConnectionListener();
    }

    public static void initialize(Context context, String castAppId,boolean isData){
        CastConfiguration.Builder builder = new CastConfiguration.Builder(castAppId);
        CastConfiguration configuration =  builder.enableAutoReconnect()
                .enableCaptionManagement()
                .enableDebug()
                .enableLockScreen()
                .enableNotification()
                .enableWifiReconnection()
                .setCastControllerImmersive(true)
                .setLaunchOptions(false, Locale.getDefault())
                .setNextPrevVisibilityPolicy(CastConfiguration.NEXT_PREV_VISIBILITY_POLICY_DISABLED)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_REWIND, false)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_PLAY_PAUSE, true)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_DISCONNECT, true)
                .setForwardStep(10)
                .build();
        if(isData){
            DataCastManager.initialize(context,configuration);
        }else{
            VideoCastManager.initialize(context,configuration);
        }
    }


}
