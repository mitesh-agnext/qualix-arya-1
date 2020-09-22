package com.custom.app.ui.login;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.login.LoginResponse;

interface LoginView extends BaseView {

    void showOtpDialog(LoginResponse response);

    void showHomeScreen(LoginResponse response);

}