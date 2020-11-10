package com.network.app.http;

public enum ApiEndpoint {

    MOCK("https://private-409127-qualixapi.apiary-mock.com"),

//    RELEASE("http://23.98.216.140:8071"),
//RELEASE("http://70.37.95.226:9075"),
RELEASE("http://13.71.36.247:9991"),
    CUSTOM();

    public String url;

    ApiEndpoint() {
    }

    ApiEndpoint(String url) {
        this.url = url;
    }

    public static ApiEndpoint from(String endpoint) {
        for (ApiEndpoint value : values()) {
            if (value.url != null && value.url.equals(endpoint)) {
                return value;
            }
        }
        return CUSTOM;
    }

    public static boolean isMockMode(String endpoint) {
        return from(endpoint) == MOCK;
    }
}