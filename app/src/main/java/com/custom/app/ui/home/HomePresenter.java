package com.custom.app.ui.home;

import com.base.app.ui.base.BasePresenter;

public abstract class HomePresenter extends BasePresenter<HomeView> {

    abstract void fetchCount();

}