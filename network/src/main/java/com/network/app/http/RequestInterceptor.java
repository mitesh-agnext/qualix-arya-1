package com.network.app.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();
/*
        if (url.endsWith("/authorize")) {
            originalRequest = originalRequest.newBuilder()
                    .removeHeader("Cookie")
                    .build();
        }
*/

        if (url.endsWith("/login")) {
            originalRequest = originalRequest.newBuilder()
                    .url(originalRequest.url().newBuilder()
                            .addQueryParameter("bearer", "mobile")
                            .build())
                    .build();

            return chain.proceed(originalRequest);
        }

        return chain.proceed(originalRequest);
    }
}