package com.custom.app.ui.quality;

import com.custom.app.ui.commodity.CommodityInteractor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class QualityPresenterImpl extends QualityPresenter {

    private QualityMapView view;
    private QualityInteractor qualityInteractor;
    private CommodityInteractor commodityInteractor;

    public QualityPresenterImpl(QualityInteractor qualityInteractor, CommodityInteractor commodityInteractor) {
        this.qualityInteractor = qualityInteractor;
        this.commodityInteractor = commodityInteractor;
    }

    @Override
    public void setView(QualityMapView view) {
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
    void fetchAnalyses(String commodityId) {
        showProgressBar();

        disposable = commodityInteractor
                .analyses(commodityId)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(analyses -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showAnalyses(analyses);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    public void fetchQualityMap(String commodityId, String analysis, String from, String to) {
        showProgressBar();

        disposable = qualityInteractor
                .qualityMap(commodityId, analysis, from, to)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(qualities -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showQualityMap(qualities);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }
}