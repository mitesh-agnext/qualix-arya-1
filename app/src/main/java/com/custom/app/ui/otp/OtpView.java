package com.custom.app.ui.otp;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.login.LoginResponse;

interface OtpView extends BaseView {

    void showQRCode(String key);

    void showLoginScreen();

    void showHomeScreen(LoginResponse response);

}