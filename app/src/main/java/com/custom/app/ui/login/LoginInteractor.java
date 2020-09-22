package com.custom.app.ui.login;

import com.custom.app.data.model.login.LoginResponse;
import com.custom.app.data.model.oauth.OauthResponse;

import io.reactivex.Single;

public interface LoginInteractor {

    Single<OauthResponse> auth();

    Single<LoginResponse> verifyOtp(String otp);

    Single<LoginResponse> login(String username, String password);

    Single<LoginResponse> fcmToken(LoginResponse response);

}