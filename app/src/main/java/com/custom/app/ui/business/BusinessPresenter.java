package com.custom.app.ui.business;

import com.base.app.ui.base.BasePresenter;

public abstract class BusinessPresenter extends BasePresenter<BusinessView> {

    abstract void fetchScanCount(String categoryId, String from, String to, String... filter);

    abstract void fetchVarianceAvg(String categoryId, String from, String to, String... filter);

    abstract void fetchAcceptedAvg(String categoryId, String from, String to, String... filter);

}