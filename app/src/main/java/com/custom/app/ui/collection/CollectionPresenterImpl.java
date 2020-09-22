package com.custom.app.ui.collection;

import com.custom.app.ui.commodity.CommodityInteractor;
import com.custom.app.ui.landing.QuantityInteractor;
import com.custom.app.ui.quality.QualityInteractor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CollectionPresenterImpl extends CollectionPresenter {

    private CollectionView view;
    private QualityInteractor qualityInteractor;
    private QuantityInteractor quantityInteractor;
    private CommodityInteractor commodityInteractor;

    public CollectionPresenterImpl(QualityInteractor qualityInteractor,
                                   QuantityInteractor quantityInteractor,
                                   CommodityInteractor commodityInteractor) {
        this.qualityInteractor = qualityInteractor;
        this.quantityInteractor = quantityInteractor;
        this.commodityInteractor = commodityInteractor;
    }

    @Override
    public void setView(CollectionView view) {
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
    void fetchQuality(String commodityId, String analysis, String from, String to, String... filter) {
        showProgressBar();

        disposable = qualityInteractor.quality(commodityId, analysis, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(quality -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showQuality(quality);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchQualityOverTime(String commodityId, String analysis, String from, String to, String... filter) {
        showProgressBar();

        disposable = qualityInteractor.qualityOverTime(commodityId, analysis, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(qualities -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showQualityOverTime(qualities);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchQuantity(String commodityId, String from, String to, String... filter) {
        showProgressBar();

        disposable = quantityInteractor.quantity(commodityId, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(quantity -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showQuantity(quantity);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchCollectionByCenter(String commodityId, String from, String to, String... filter) {
        showProgressBar();

        disposable = quantityInteractor.collectionByCenter(commodityId, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(collections -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showCollectionByCenter(collections);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchCollectionOverTime(String commodityId, String from, String to, String... filter) {
        showProgressBar();

        disposable = quantityInteractor.collectionOverTime(commodityId, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(collections -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showCollectionOverTime(collections);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchCollectionRegion(String commodityId, String regionId, String centerId,
                               String from, String to, String... filter) {
        showProgressBar();

        disposable = quantityInteractor.collectionRegion(commodityId, regionId, centerId, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(collections -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showCollectionRegion(collections);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchCollectionWeekly(String commodityId, String from, String to, String... filter) {
        showProgressBar();

        disposable = quantityInteractor.collectionWeekly(commodityId, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(collection -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showCollection(collection);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }
}