package com.peirr.localcast.io;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.peirr.http.mvp.HttpContract;
import com.peirr.http.mvp.HttpPresenter;
import com.peirr.http.mvp.HttpRepositories;
import com.peirr.http.mvp.HttpRepository;
import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.SimpleHttpService;
import com.peirr.localcast.mvp.CastContract;
import com.peirr.localcast.mvp.CastPresenter;
import com.peirr.localcast.mvp.CastRepositories;
import com.peirr.localcast.mvp.CastRepository;

/**
 * Created by kurt on 2016/01/29.
 */
public class LocalCastManager implements HttpContract.View, CastContract.View {

    private static final String TAG = "LocalCastManager";

    private CastConnectionListener castConnectionListener;
    private HttpConnectionListener httpConnectionListener;
    private boolean castConnected;
    private HttpPresenter http;
    private CastPresenter cast;
    private SimpleHttpInfo httpInfo;
    private int version = -1;

    public LocalCastManager(Activity activity, String castNamespace) {
        HttpRepository repository = new HttpRepositories(activity, SimpleHttpService.generatePort());
        CastRepository castRepository = new CastRepositories(castNamespace);
        http = new HttpPresenter(repository, this);
        cast = new CastPresenter(castRepository, this);
        getVersionCode(activity);
    }

    public LocalCastManager(Activity activity, String castNamespace, int port) {
        HttpRepository repository = new HttpRepositories(activity, port);
        CastRepository castRepository = new CastRepositories(castNamespace);
        http = new HttpPresenter(repository, this);
        cast = new CastPresenter(castRepository, this);
        getVersionCode(activity);
    }

    public interface CastConnectionListener {
        void onCastConnectionChanged(boolean connected, Exception exception);
    }

    public interface HttpConnectionListener {
        void onServerConnectionChanged(int status, SimpleHttpInfo info);
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
        }else{
            httpInfo = null;
        }
        if (httpConnectionListener != null) {
            httpConnectionListener.onServerConnectionChanged(status, info);
        }
    }

    public void send(CastMessageUtils.CastMessage message){
        cast.post(message);
    }

    private void playMedia(CastMessageUtils.CastType type,String url){
        if (httpInfo != null) {
            String host = "http://" + httpInfo.ip + ":" + httpInfo.port;
            CastMessageUtils.CastMessage message = CastMessageUtils.play(type,url,host,version);
            cast.post(message);
        }
    }

    /**
     * cast a local video to a connected cast device
     * @param url
     */
    public void castVideo(String url){
        playMedia(CastMessageUtils.CastType.VIDEO,url);
    }

    /**
     * cast a local image to a connected cast device
     * @param url
     */
    public void castImage(String url){
        playMedia(CastMessageUtils.CastType.IMAGE,url);
    }


    /**
     * cast a local audio file to a connected cast device
     * @param url
     */
    public void castAudio(String url){
        playMedia(CastMessageUtils.CastType.AUDIO,url);

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


    /**
     * disconnect the manager from your activity/fragment using this
     */
    public void disconnect() {
        http.disconnect();
        cast.detach();
        unregisterCastConnectionListener();
        unregisterHttpConnectionListener();
    }

    public static void initialize(Context context, String castAppId){
        CastConfiguration.Builder builder = new CastConfiguration.Builder(castAppId);
        builder.enableAutoReconnect();
        builder.enableDebug();
        builder.enableLockScreen();
        builder.enableWifiReconnection();
        builder.enableNotification();
        builder.addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_PLAY_PAUSE, true);
        builder.addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_DISCONNECT, true);
        CastConfiguration configuration = builder.build();
        DataCastManager.initialize(context,configuration);
    }


}
