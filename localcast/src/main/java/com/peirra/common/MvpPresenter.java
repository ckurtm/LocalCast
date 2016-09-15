package com.peirra.common;


public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
