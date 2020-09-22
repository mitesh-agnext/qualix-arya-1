package com.custom.app.ui.password.forgot;

import com.base.app.ui.base.BasePresenter;

public abstract class ForgotPasswordPresenter extends BasePresenter<ForgotPasswordView> {

    abstract void callForgotPassword(String username);

}