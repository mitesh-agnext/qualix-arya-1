package com.custom.app.ui.otp;

import com.custom.app.ui.login.LoginInteractor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class OtpPresenterImpl extends OtpPresenter {

    private OtpView view;
    private LoginInteractor interactor;

    public OtpPresenterImpl(LoginInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(OtpView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    public void callVerifyOtp(String otp) {
        showProgressBar();

        disposable = interactor.verifyOtp(otp)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(response -> {
                    hideProgressBar();

                    if (isViewAttached()) {
                        if (response.getAction().equals("login")) {
                            view.showLoginScreen();
                        } else {
                            view.showHomeScreen(response);
                        }
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }
}