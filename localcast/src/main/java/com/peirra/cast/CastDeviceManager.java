package com.peirra.cast;

import android.content.Context;
import android.view.Menu;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

/**
 * Created by kurt on 2016/09/14.
 */

public class CastDeviceManager implements CastDevice {

    private static final String TAG = "CastDeviceManager";

    private SessionManager sessionManager;
    private CastSession session;
    private CastContext castContext;
    private final Context context;
    private CastSessionCallback sessionCallback;
    private DeviceCallback deviceCallback;
    private final String nameSpace;
    private CastChannel channel;

    private CastStateListener stateCallback = new CastStateListener() {
        @Override
        public void onCastStateChanged(final int newState) {
            if (deviceCallback != null) {
                deviceCallback.onCastStateChanged(newState);
            }
        }
    };

    private CastChannel.MessageCallback channelCallback = new CastChannel.MessageCallback() {
        @Override
        public void onMessageReceived(final String namespace, final String message) {
            if (deviceCallback != null) {
                deviceCallback.onMessageReceived(namespace, message);
            }
        }

        public void onMessagePosted(final String namespace, final String message) {/*NA for this*/}

        public void onMessageFailed(final String namespace, final String message) {/*NA for this*/}
    };


    public CastDeviceManager(final Context context, final String nameSpace) {
        this.context = context;
        this.castContext = CastContext.getSharedInstance(context);
        this.nameSpace = nameSpace;
        sessionManager = this.castContext.getSessionManager();
        channel = new CastChannel(nameSpace, channelCallback);
    }


    @Override
    public void post(final String json, final DeviceCallback callback) {
        if (channel != null) {
            try {
                session.sendMessage(nameSpace, json).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status result) {
                        if (result != null) {
                            if (result.isSuccess()) {
                                callback.onMessagePosted(nameSpace, json);
                            } else {
                                callback.onMessageFailed(nameSpace, json);
                            }
                        } else {
                            callback.onMessageFailed(nameSpace, json);
                        }
                    }
                });
            } catch (Exception e) {
                callback.onMessageFailed(nameSpace, json);
            }
        } else {
            callback.onMessageFailed(nameSpace, json);
        }
    }

    @Override
    public void attach(final DeviceCallback callback) {
        deviceCallback = callback;
        session = sessionManager.getCurrentCastSession();
        castContext.addCastStateListener(stateCallback);
        sessionCallback = new CastSessionCallback(deviceCallback);
        sessionManager.addSessionManagerListener(sessionCallback);
    }

    @Override
    public void attachMenu(final Menu menu, final int menuItemId) {
        CastButtonFactory.setUpMediaRouteButton(context.getApplicationContext(), menu, menuItemId);
    }

    @Override
    public void detach() {
        castContext.removeCastStateListener(stateCallback);
        sessionManager.removeSessionManagerListener(sessionCallback);
        session = null;
    }

    @Override
    public void setupChannel(Session session) {
        this.session = (CastSession) session;
        try {
            this.session.setMessageReceivedCallbacks(nameSpace, channel);
        } catch (IOException e) {
            if (deviceCallback != null) {
                deviceCallback.onChannelAttachementFailed(e);
            }
        }
    }
}
