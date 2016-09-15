package com.peirra.cast;

import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManagerListener;

/**
 * Created by kurt on 2016/09/09.
 */
public class CastSessionCallback implements SessionManagerListener {
    private static final String TAG = "CastSessionCallback";
    private final CastDevice.DeviceCallback connection;

    public CastSessionCallback(final CastDevice.DeviceCallback listener) {
        this.connection = listener;
    }

    @Override
    public void onSessionStarting(final Session session) {
    }

    @Override
    public void onSessionStarted(Session session, String sessionId) {
        onApplicationConnected(session);
    }

    @Override
    public void onSessionStartFailed(final Session session, final int i) {
        onApplicationDisconnected();
    }

    @Override
    public void onSessionEnding(final Session session) {
    }

    @Override
    public void onSessionResumed(Session session, boolean wasSuspended) {
        onApplicationConnected(session);
    }

    @Override
    public void onSessionResumeFailed(final Session session, final int i) {
        onApplicationDisconnected();
    }

    @Override
    public void onSessionSuspended(final Session session, final int i) {
    }

    @Override
    public void onSessionEnded(Session session, int error) {
        onApplicationDisconnected();
    }

    @Override
    public void onSessionResuming(final Session session, final String s) {
    }

    private void onApplicationConnected(Session session) {
        connection.onCastConnected(session);
    }

    private void onApplicationDisconnected() {
        connection.onCastDisconnected();
    }
}
