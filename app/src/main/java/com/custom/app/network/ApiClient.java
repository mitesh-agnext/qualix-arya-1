package com.custom.app.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://70.37.95.226";
//    private static final String BASE_URL = "http://23.98.216.140";
    private static final String TEA_BASE_URL = "http://203.193.132.228:9000/";
//    private static final String FARMER_URl="http://23.98.216.140";
    private static final String FARMER_URl="http://70.37.95.226";

    private static Retrofit retrofit = null;
    private static Retrofit dcmRetrofit = null;
    private static Retrofit vmsRetrofit = null;
    private static Retrofit scmRetrofit = null;
    private static OkHttpClient okHttpClient = null;
    private static GsonConverterFactory gsonFactory = null;

    private static GsonConverterFactory getGsonFactory() {
        if (gsonFactory == null) {
            Gson gson = new GsonBuilder().setLenient().create();
            gsonFactory = GsonConverterFactory.create(gson);
        }
        return gsonFactory;
    }

    private static OkHttpClient getOkhttpClient() {
        if (okHttpClient == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor).build();
        }

        return okHttpClient;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(String.format("%s:9075", BASE_URL)) //8071
                    .addConverterFactory(getGsonFactory())
                    .client(getOkhttpClient())
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getDcmClient() {
        if (dcmRetrofit == null) {
            dcmRetrofit = new Retrofit.Builder()
                    .baseUrl(String.format("%s:9075", BASE_URL))
                    .addConverterFactory(getGsonFactory())
                    .client(getOkhttpClient())
                    .build();
        }

        return dcmRetrofit;
    }

    public static Retrofit getScmClient() {
        if (scmRetrofit == null) {
            scmRetrofit = new Retrofit.Builder()
                    .baseUrl(String.format("%s:9075", BASE_URL))
                    .addConverterFactory(getGsonFactory())
                    .client(getOkhttpClient())
                    .build();
        }

        return scmRetrofit;
    }

    private static Retrofit retrofitFlc = null;
    public static Retrofit getClientFlc() {
        if (retrofitFlc==null) {
            retrofitFlc = new Retrofit.Builder()
                    .baseUrl(TEA_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitFlc;
    }

    public static Retrofit getVmsClient() {
        if (vmsRetrofit == null) {
            vmsRetrofit = new Retrofit.Builder()
                    .baseUrl(String.format("%s:9075", BASE_URL))
                    .addConverterFactory(getGsonFactory())
                    .client(getOkhttpClient())
                    .build();
        }

        return vmsRetrofit;
    }

    //http://23.98.216.140:5679
    public static Retrofit getFarmerClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(String.format("%s:5679", FARMER_URl))
                    .addConverterFactory(getGsonFactory())
                    .client(getOkhttpClient())
                    .build();
        }

        return retrofit;
    }
}