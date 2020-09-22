package com.custom.app.ui.logout;

import com.custom.app.network.RestService;
import com.network.app.oauth.OAuthTokenManager;
import com.user.app.data.UserManager;

import io.reactivex.Single;

public class LogoutInteractorImpl implements LogoutInteractor {

    private RestService restService;
    private UserManager userManager;
    private OAuthTokenManager tokenManager;

    public LogoutInteractorImpl(RestService restService, UserManager userManager,
                                OAuthTokenManager tokenManager) {
        this.restService = restService;
        this.userManager = userManager;
        this.tokenManager = tokenManager;
    }

    @Override
    public Single<String> logout() {

        String accessToken = tokenManager.getAccessToken();
        userManager.clearData();

        tokenManager.revokeAccessToken();

        return restService.logout(accessToken)
                .map(LogoutParser::parse);
    }
}