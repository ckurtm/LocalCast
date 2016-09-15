package com.peirra.cast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;

/**
 * Created by kurt on 2016/09/14.
 */

public class CastChannel implements Cast.MessageReceivedCallback {

    public interface MessageCallback {
        void onMessageReceived(String namespace, String message);
    }

    private final String namespace;
    private final MessageCallback callback;

    public CastChannel(final String namespace, final MessageCallback callback) {
        this.namespace = namespace;
        this.callback = callback;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public void onMessageReceived(final CastDevice castDevice, final String namespace, final String json) {
        callback.onMessageReceived(namespace, json);
    }
}
