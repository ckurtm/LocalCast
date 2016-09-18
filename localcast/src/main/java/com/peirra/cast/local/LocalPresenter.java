package com.peirra.cast.local;

import android.view.Menu;

import com.peirr.cast.CastContract;
import com.peirr.cast.CastDevice;
import com.peirr.cast.CastPresenter;
import com.peirr.http.HttpContract;
import com.peirr.http.HttpPresenter;
import com.peirr.http.IServerRequest;
import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.SimpleHttpService;
import com.peirr.presentation.BasePresenter;


/**
 * Created by kurt on 2016/09/15.
 */

public class LocalPresenter extends BasePresenter<LocalContract.View>
        implements LocalContract.Presenter, HttpContract.View, CastContract.View {

    private HttpPresenter http;
    private CastPresenter cast;
    private boolean keepAlive;
    private SimpleHttpInfo info;
    private boolean castConnected;
    private final CastDevice device;

    public LocalPresenter(final IServerRequest httpRequest, final CastDevice device, boolean keepAlive) {
        http = new HttpPresenter(httpRequest);
        cast = new CastPresenter(device);
        this.keepAlive = keepAlive;
        this.device = device;
    }

    @Override
    public void addMenu(final Menu menu, final int menuItemid) {
        cast.attachMenu(menu, menuItemid);
    }

    @Override
    public void post(final String message) {
        cast.post(message);
    }

    @Override
    public void attachView(final LocalContract.View mvpView) {
        super.attachView(mvpView);
        http.attachView(this);
        cast.attachView(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        http.disconnect();
        http.detachView();
        cast.detachView();
    }

    @Override
    public void showHttpStatus(final int status, final SimpleHttpInfo info) {
        this.info = info;
        switch (status) {
            case SimpleHttpService.STATE_RUNNING:
                device.setHost(info.ip + ":" + info.port);
                if (isViewAttached()) {
                    getView().showServerConnected(true, info.ip + ":" + info.port);
                }
                break;
            case SimpleHttpService.STATE_STOPPED:
                if (isViewAttached()) {
                    getView().showServerConnected(false, "");
                }
                break;
            case SimpleHttpService.STATE_ERROR:
                if (isViewAttached()) {
                    getView().showServerConnected(false, "");
                }
                break;
        }
    }

    @Override
    public void showCastConnected() {
        castConnected = true;
        if (keepAlive) {
            http.startService();
        } else {
            http.bootup();
        }
        http.connect();
        if (isViewAttached()) {
            getView().showCastConnected(true);
        }
    }

    @Override
    public void showCastDisconnected() {
        castConnected = false;
        if (!keepAlive) {
            http.shutdown();
        }
        if (isViewAttached()) {
            getView().showCastConnected(false);
        }
    }

    @Override
    public void showMessageReceived(final String nameSpace, final String json) {
        if (isViewAttached()) {
            getView().showMessage(json);
        }
    }


    private boolean isCastConnected() {
        return castConnected;
    }

    public String getHost() {
        if (info != null) {
            return info.ip + ":" + info.port;
        }
        return "";
    }
}
