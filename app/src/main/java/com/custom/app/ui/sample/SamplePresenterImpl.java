package com.custom.app.ui.sample;

import com.custom.app.ui.analysis.AnalysisInteractor;
import com.custom.app.ui.commodity.CommodityInteractor;
import com.specx.scan.data.model.commodity.CommodityItem;

import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class SamplePresenterImpl extends SamplePresenter {

    private SampleView view;
    private PublishSubject<String> analysesRequest;
    private AnalysisInteractor analysisInteractor;
    private CommodityInteractor commodityInteractor;

    public SamplePresenterImpl(AnalysisInteractor analysisInteractor, CommodityInteractor commodityInteractor) {
        this.analysisInteractor = analysisInteractor;
        this.commodityInteractor = commodityInteractor;
    }

    @Override
    public void setView(SampleView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    void fetchCommodities() {
        showProgressBar();

        disposable = commodityInteractor.fetchCommoditiesFromNetwork()
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(commodities -> {
                    hideProgressBar();
                    if (isViewAttached()) {
                        view.showCommodities(commodities);
                    }
                }, error -> {
                    disposable = commodityInteractor.fetchCommoditiesFromDb()
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(commodities -> {
                                hideProgressBar();
                                if (isViewAttached()) {
                                    view.showCommodities(commodities);
                                }
                            }, e -> {
                                showEmptyView();
                                hideProgressBar();
                                showMessage(e.getMessage());
                            });
                });
    }

    @Override
    void fetchAnalyses(String batchId) {
        if (analysesRequest == null) {
            analysesRequest = PublishSubject.create();
        }

        disposable = analysesRequest
                .switchMap(_batchId -> analysisInteractor.fetchAnalysesFromDb(_batchId))
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(analyses -> {
                    if (isViewAttached()) {
                        view.showAnalysis(analyses.get(0));
                    }
                }, error -> {
                    showMessage(error.getMessage());
                });

        updateAnalyses(batchId);
    }

    @Override
    void updateAnalyses(String batchId) {
        analysesRequest.onNext(batchId);
    }

    @Override
    void fetchVarieties(CommodityItem commodity) {
        showProgressBar();

        disposable = commodityInteractor.fetchVarietiesFromNetwork(commodity.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(varieties -> {
                    hideProgressBar();
                    if (isViewAttached()) {
                        view.showVarieties(varieties);
                    }
                }, error -> {
                    disposable = commodityInteractor.fetchVarietiesFromDb(commodity.getId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(varieties -> {
                                hideProgressBar();
                                if (isViewAttached()) {
                                    view.showVarieties(varieties);
                                }
                            }, e -> {
                                hideProgressBar();
                                if (isViewAttached()) {
                                    view.showVarieties(Collections.emptyList());
                                }
                            });
                });
    }
}