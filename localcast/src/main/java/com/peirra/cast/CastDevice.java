package com.peirra.cast;

import android.view.Menu;

import com.google.android.gms.cast.framework.Session;

/**
 * Created by kurt on 2016/09/14.
 */

public interface CastDevice {

    interface DeviceCallback {
        void onCastStateChanged(int state);
        void onCastConnected(Session session);
        void onCastDisconnected();
        void onMessagePosted(String namespace, String message);
        void onMessageFailed(String namespace, String message);
        void onMessageReceived(String namespace, String message);
        void onChannelAttachementFailed(Exception e);
    }

    void post(String json, DeviceCallback callback);
    void attach(DeviceCallback callback);
    void attachMenu(Menu menu, int menuItemId);
    void detach();
    void setupChannel(Session session);
}
