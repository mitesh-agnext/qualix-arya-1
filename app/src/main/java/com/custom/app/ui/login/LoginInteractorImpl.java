package com.custom.app.ui.login;

import com.custom.app.data.model.login.LoginRequest;
import com.custom.app.data.model.login.LoginResponse;
import com.custom.app.data.model.oauth.OauthResponse;
import com.custom.app.network.RestService;
import com.squareup.sqlbrite3.BriteDatabase;
import com.user.app.data.UserManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class LoginInteractorImpl implements LoginInteractor {

    private RestService restService;
    private UserManager userManager;
    private BriteDatabase database;

    public LoginInteractorImpl(RestService restService, UserManager userManager, BriteDatabase database) {
        this.restService = restService;
        this.userManager = userManager;
        this.database = database;
    }

    @Override
    public Single<OauthResponse> auth() {
        Map<String, String> query = new HashMap<>();
        query.put("client_id", "client-mobile");
        query.put("response_type", "code");

        return restService.oauth(query)
                .map(LoginParser::oauth);
    }

    @Override
    public Single<LoginResponse> verifyOtp(String otp) {
        return restService.verifyOtp(otp)
                .map(LoginParser::otp);
    }

    @Override
    public Single<LoginResponse> login(String username, String password) {
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        requestBuilder.addFormDataPart("Signin", "Sign+In");
        requestBuilder.addFormDataPart("bearer", "mobile");
        requestBuilder.addFormDataPart("username", username);
        requestBuilder.addFormDataPart("password", password);
        RequestBody requestBody = requestBuilder.build();

        return restService.login(requestBody)
                .map(LoginParser::login);
    }

    @Override
    public Single<LoginResponse> fcmToken(LoginResponse response) {
        String fcmToken = String.format("Bearer %s", response.getToken());
        return restService.fcmToken(fcmToken, new LoginRequest(userManager.getFcmToken()))
                .map((ResponseBody body) -> LoginParser.fcm(response));
    }
}