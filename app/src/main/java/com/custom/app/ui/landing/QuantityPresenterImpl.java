package com.custom.app.ui.landing;

import java.util.Observable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class QuantityPresenterImpl extends QuantityPresenter {

    private LandingView view;
    private QuantityInteractor interactor;

    public QuantityPresenterImpl(QuantityInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(LandingView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    void fetchCategories() {
        showProgressBar();

        disposable = interactor.categories()
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(categories -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        view.showCategories(categories);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }

    @Override
    void fetchQuantityDetail(String categoryId, String from, String to) {
        showProgressBar();

        disposable = interactor.qualityDetail(categoryId, from, to)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(detail -> {
                            hideProgressBar();

                            if (isViewAttached()) {
                                view.showDetails(detail);
                            }
                        },
                        error -> {
                            hideProgressBar();

                            if (error.getMessage().equals("Network connection error!")) {
                                interactor.qualityDetail(categoryId, from, to).repeat(3);
                                showMessage(error.getMessage());
                            } else if (error.getMessage().equals("Unknown error occurred!")) {
                                showMessage("No record found");
                                view.showEmptyView();
                            } else {
                                showMessage(error.getMessage());
                            }
                        });
    }
}