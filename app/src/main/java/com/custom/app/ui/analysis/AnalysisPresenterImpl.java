package com.custom.app.ui.analysis;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AnalysisPresenterImpl extends AnalysisPresenter {

    private AnalysisView view;
    private AnalysisInteractor interactor;

    public AnalysisPresenterImpl(AnalysisInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(AnalysisView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    void fetchAnalyses(String batchId) {
        showProgressBar();

        disposable = interactor.fetchAnalysesFromDb(batchId)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(analyses -> {
                    hideProgressBar();
                    if (isViewAttached()) {
                        view.showList(analyses);
                    }
                }, error -> {
                    showEmptyView();
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void removeAnalyses(String batchId) {
        showProgressDialog();

        disposable = interactor.removeAnalyses(batchId)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(msg -> {
                    showMessage(msg);
                    hideProgressDialog();
                    if (isViewAttached()) {
                        interactor.removeAnalysesFromDb(batchId);
                    }
                }, error -> {
                    hideProgressDialog();
                    showMessage(error.getMessage());
                });
    }
}