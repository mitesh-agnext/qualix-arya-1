package com.custom.app.ui.login;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenterImpl extends LoginPresenter {

    private LoginView view;
    private LoginInteractor interactor;

    public LoginPresenterImpl(LoginInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(LoginView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    public void callLogin(String username, String password) {
        showProgressBar();

        disposable = interactor
                .auth()
                .flatMap(response -> {
                    if (response.getAction().equalsIgnoreCase("login")) {
                        return interactor.login(username, password);
                    } else {
                        throw new RuntimeException(response.getAction());
                    }
                })
                .flatMap(response -> {
                    if (response != null) {
                        return interactor.fcmToken(response);
                    } else {
                        throw new RuntimeException("Invalid login response!");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(response -> {
                    if (isViewAttached()) {
                        view.showHomeScreen(response);
                    }
                }, error -> {
                    hideProgressBar();
                    showMessage(error.getMessage());
                });
    }
}