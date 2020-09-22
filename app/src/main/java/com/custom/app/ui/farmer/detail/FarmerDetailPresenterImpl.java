package com.custom.app.ui.farmer.detail;

import android.text.TextUtils;

import com.custom.app.data.model.farmer.upload.FarmerItem;
import com.custom.app.ui.farmer.FarmerInteractor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FarmerDetailPresenterImpl extends FarmerDetailPresenter {

    private FarmerDetailView view;
    private FarmerInteractor interactor;

    public FarmerDetailPresenterImpl(FarmerInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(FarmerDetailView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    public void uploadFarmer(FarmerItem farmer) {
        showProgressDialog();

        disposable = interactor.uploadFarmer(farmer)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(farmerCode -> {
                    hideProgressDialog();

                    if (isViewAttached()) {
                        view.showQRCode(farmerCode);
                    }
                }, error -> {
                    hideProgressDialog();

                    if (isViewAttached()) {
                        view.showAddFarmerDialog(farmer);
                    }
                });
    }

    @Override
    void addFarmerInDb(FarmerItem farmer) {
        showProgressDialog();

        String farmerCode = interactor.addFarmerInDb(farmer);

        hideProgressDialog();

        if (!TextUtils.isEmpty(farmerCode)) {
            if (isViewAttached()) {
                view.showQRCode(farmerCode);
            }
        } else {
            showMessage("Whoops! Some unknown error occurred!");
        }
    }
}