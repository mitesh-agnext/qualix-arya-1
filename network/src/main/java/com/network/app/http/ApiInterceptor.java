package com.network.app.http;

import android.text.TextUtils;

import com.network.app.oauth.OAuthTokenManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class ApiInterceptor implements Interceptor {

    private ApiManager apiManager;
    private OAuthTokenManager tokenManager;

    ApiInterceptor(ApiManager apiManager, OAuthTokenManager tokenManager) {
        this.apiManager = apiManager;
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();

        String token = "";
        if (!url.endsWith("/api/auth/token") && !url.endsWith("/api/auth/login")
                && !url.contains("/api/vendor")) {
            token = tokenManager.getAccessToken();
        }

        if (url.contains("/api/vendor")) {
            HttpUrl newUrl = originalRequest.url().newBuilder()
                    .port(9075)
                    .build();

            originalRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build();
        }

        if (TextUtils.isEmpty(token) || originalRequest.header("Authorization") != null) {
            if (url.contains("/api/user/device/token")) {
                HttpUrl newUrl = originalRequest.url().newBuilder()
                        .port(9075)
                        .build();

                originalRequest = originalRequest.newBuilder()
                        .url(newUrl)
                        .build();
            }
            return chain.proceed(originalRequest);
        }

        Request authorisedRequest = originalRequest.newBuilder()
                .header("Authorization", String.format("Bearer %s", token))
                .build();

        if (apiManager != null && !ApiEndpoint.isMockMode(apiManager.getApiEndpoint())) {
            if (url.contains("/api/commodity") ||
                    url.contains("/api/user/device/token") || url.contains("/api/device/")) {
                HttpUrl newUrl = originalRequest.url().newBuilder()
                        .port(9075)
                        .build();

                authorisedRequest = authorisedRequest.newBuilder()
                        .url(newUrl)
                        .build();
            }

            if (!TextUtils.isEmpty(originalRequest.header("Chemical-Result"))) {
                HttpUrl newUrl = originalRequest.url().newBuilder()
                        .port(5000)
                        .build();

                originalRequest = originalRequest.newBuilder()
                        .url(newUrl)
                        .build();

                return chain
                        .withReadTimeout(10, TimeUnit.MINUTES)
                        .proceed(originalRequest);
            }

            if (url.contains("/api/scan") || url.contains("/api/analytics")) {
                HttpUrl newUrl = originalRequest.url().newBuilder()
                        .port(9075)
                        .build();

                authorisedRequest = authorisedRequest.newBuilder()
                        .url(newUrl)
                        .build();
            }
        }

        return chain.proceed(authorisedRequest);
    }
}