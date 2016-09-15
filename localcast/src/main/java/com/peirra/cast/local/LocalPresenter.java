package com.peirra.cast.local;

import android.view.Menu;

import com.peirra.cast.CastContract;
import com.peirra.cast.CastDevice;
import com.peirra.cast.CastPresenter;
import com.peirra.common.BasePresenter;
import com.peirra.http.HttpContract;
import com.peirra.http.HttpPresenter;
import com.peirra.http.IServerRequest;
import com.peirra.http.service.SimpleHttpInfo;
import com.peirra.http.service.SimpleHttpService;

/**
 * Created by kurt on 2016/09/15.
 */

public class LocalPresenter extends BasePresenter<LocalContract.View>
        implements LocalContract.Presenter, HttpContract.View, CastContract.View {

    private HttpPresenter http;
    private CastPresenter cast;
    private boolean keepAlive;

    public LocalPresenter(final IServerRequest httpRequest, final CastDevice device, boolean keepAlive) {
        http = new HttpPresenter(httpRequest);
        cast = new CastPresenter(device);
        this.keepAlive = keepAlive;
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
        http.detachView();
        cast.detachView();
    }

    @Override
    public void showHttpStatus(final int status, final SimpleHttpInfo info) {
        switch (status) {
            case SimpleHttpService.STATE_RUNNING:
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
        if(keepAlive) {
            http.startService();
        }else{
            http.bootup();
        }
        http.connect();
        if (isViewAttached()) {
            getView().showCastConnected(true);
        }
    }

    @Override
    public void showCastDisconnected() {
        http.disconnect();
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
}
