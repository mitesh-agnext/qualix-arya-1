package com.custom.app.ui.commodity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CommodityPresenterImpl extends CommodityPresenter {

    private CommodityView view;
    private CommodityInteractor interactor;

    public CommodityPresenterImpl(CommodityInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(CommodityView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    void fetchCommodities() {
        showProgressBar();

        disposable = interactor.fetchCommoditiesFromNetwork()
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(commodities -> {
                    hideProgressBar();
                    if (isViewAttached()) {
                        view.showList(commodities);
                    }
                }, error -> {
                    disposable = interactor.fetchCommoditiesFromDb()
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(commodities -> {
                                hideProgressBar();
                                if (isViewAttached()) {
                                    view.showList(commodities);
                                }
                            }, e -> {
                                showEmptyView();
                                hideProgressBar();
                                showMessage(e.getMessage());
                            });
                });
    }
}