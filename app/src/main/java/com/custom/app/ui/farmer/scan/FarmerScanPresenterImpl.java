package com.custom.app.ui.farmer.scan;

import com.custom.app.ui.farmer.FarmerInteractor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FarmerScanPresenterImpl extends FarmerScanPresenter {

    private FarmerScanView view;
    private FarmerInteractor interactor;

    public FarmerScanPresenterImpl(FarmerInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(FarmerScanView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    public void verifyFarmer(String query) {
        showProgressDialog();

        disposable = interactor.verifyFarmerFromNetwork(query)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(farmer -> {
                    hideProgressDialog();

                    if (isViewAttached()) {
                        view.showFarmerDetail(farmer);
                    }
                }, error -> {
                    disposable = interactor.verifyFarmerFromDb(query)
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(farmer -> {
                                hideProgressDialog();

                                if (isViewAttached()) {
                                    view.showFarmerDetail(farmer);
                                }
                            }, e -> {
                                hideProgressDialog();

                                if (isViewAttached()) {
                                    view.showFarmerVerificationError();
                                }
                            });
                });
    }
}