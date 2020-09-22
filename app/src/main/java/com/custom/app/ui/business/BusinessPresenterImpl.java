package com.custom.app.ui.business;

import com.custom.app.ui.landing.QuantityInteractor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BusinessPresenterImpl extends BusinessPresenter {

    private BusinessView view;
    private QuantityInteractor interactor;

    public BusinessPresenterImpl(QuantityInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(BusinessView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    void fetchScanCount(String categoryId, String from, String to, String... filter) {
        showProgressBar();

        disposable = interactor.scanCount(categoryId, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(detail -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showScanCount(detail);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchVarianceAvg(String categoryId, String from, String to, String... filter) {
        showProgressBar();

        disposable = interactor.varianceAvg(categoryId, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(detail -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showVarianceAvg(detail);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchAcceptedAvg(String categoryId, String from, String to, String... filter) {
        showProgressBar();

        disposable = interactor.acceptedAvg(categoryId, from, to, filter)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(detail -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showAcceptedAvg(detail);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }
}