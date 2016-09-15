package com.peirra.cast;

import android.util.Log;
import android.view.Menu;

import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.Session;
import com.peirra.common.BasePresenter;

/**
 * Created by kurt on 2016/09/14.
 */

public class CastPresenter extends BasePresenter<CastContract.View> implements CastContract.Presenter {
    private static final String TAG = "CastPresenter";
    private final CastDevice device;

    private CastDevice.DeviceCallback deviceCallback = new CastDevice.DeviceCallback() {
        @Override
        public void onCastStateChanged(final int newState) {
            String state = "NOT_CONNECTED";
            switch (newState) {
                case CastState.CONNECTED:
                    state = "CONNECTED";
                    break;
                case CastState.CONNECTING:
                    state = "CONNECTING";
                    break;
                case CastState.NO_DEVICES_AVAILABLE:
                    state = "NO_DEVICES_AVAILABLE";
                    break;
                case CastState.NOT_CONNECTED:
                    state = "NOT_CONNECTED";
                    break;
            }
            Log.d(TAG, "onCastStateChanged() : [" + state + "]");

        }

        @Override
        public void onCastConnected(final Session session) {
            device.setupChannel(session);
            if (isViewAttached()) {
                getView().showCastConnected();
            }
        }

        @Override
        public void onCastDisconnected() {
            if (isViewAttached()) {
                getView().showCastDisconnected();
            }
        }

        @Override
        public void onMessagePosted(final String namespace, final String message) {
            Log.d(TAG, "onMessagePosted() : " + "namespace = [" + namespace + "], message = [" + message + "]");
        }

        @Override
        public void onMessageFailed(final String namespace, final String message) {
            Log.d(TAG, "onMessageFailed() : " + "namespace = [" + namespace + "], message = [" + message + "]");
        }

        @Override
        public void onMessageReceived(final String namespace, final String message) {
            if(isViewAttached()){
                getView().showMessageReceived(namespace,message);
            }
        }

        @Override
        public void onChannelAttachementFailed(final Exception e) {
            Log.d(TAG, "onChannelAttachementFailed() : " + "e = [" + e + "]");
        }
    };

    public CastPresenter(final CastDevice device) {
        this.device = device;
    }

    @Override
    public void attach() {
        if (isViewAttached()) {
            device.attach(deviceCallback);
        }
    }

    @Override
    public void attachMenu(final Menu menu, final int menuItemId) {
        device.attachMenu(menu, menuItemId);
    }

    @Override
    public void detach() {
        if (isViewAttached()) {
            device.detach();
        }
    }

    @Override
    public void post(final String json) {
        if (isViewAttached()) {
            device.post(json,deviceCallback);
        }
    }


    @Override
    public void attachView(final CastContract.View mvpView) {
        super.attachView(mvpView);
        attach();
    }

    @Override
    public void detachView() {
        super.detachView();
        detach();
    }
}
