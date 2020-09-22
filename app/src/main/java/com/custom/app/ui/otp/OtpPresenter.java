package com.custom.app.ui.otp;

import com.base.app.ui.base.BasePresenter;

public abstract class OtpPresenter extends BasePresenter<OtpView> {

    abstract void callVerifyOtp(String otp);

}