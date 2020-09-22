package com.custom.app.ui.supplier;

import com.custom.app.ui.commodity.CommodityInteractor;
import com.custom.app.ui.landing.QuantityInteractor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SupplierPresenterImpl extends SupplierPresenter {

    private SupplierView view;
    private QuantityInteractor quantityInteractor;
    private CommodityInteractor commodityInteractor;

    public SupplierPresenterImpl(QuantityInteractor quantityInteractor,
                                 CommodityInteractor commodityInteractor) {
        this.quantityInteractor = quantityInteractor;
        this.commodityInteractor = commodityInteractor;
    }

    @Override
    public void setView(SupplierView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    void fetchCommodity(String... categoryId) {
        showProgressBar();

        disposable = commodityInteractor
                .fetchCommoditiesFromNetwork(categoryId)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(commodities -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showCommodities(commodities);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchSuppliers(String commodityId, String regionId, String from, String to) {
        showProgressBar();

        disposable = quantityInteractor.suppliers(commodityId, regionId, from, to)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(suppliers -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showList(suppliers);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }
}