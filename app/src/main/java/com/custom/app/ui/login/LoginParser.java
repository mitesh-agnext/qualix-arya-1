package com.custom.app.ui.login;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.custom.app.data.model.login.LoginResponse;
import com.custom.app.data.model.oauth.OauthResponse;

class LoginParser {

    @NonNull
    static OauthResponse oauth(OauthResponse body) throws NullPointerException {
        if (!TextUtils.isEmpty(body.getAction())) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static LoginResponse otp(LoginResponse body) throws NullPointerException {
        if (!TextUtils.isEmpty(body.getAction())) {
            return body;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static LoginResponse login(LoginResponse body) throws NullPointerException {
        return body;
    }

    @NonNull
    static LoginResponse fcm(LoginResponse body) throws NullPointerException {
        return body;
    }
}