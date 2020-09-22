package com.custom.app.data.model.login;

import com.user.app.data.UserData;

import java.util.List;

public class LoginResponse {

    private String key;
    private String action;
    private UserData user;
    private String access_token;
    private List<String> permissions;

    public String getKey() {
        return key;
    }

    public String getAction() {
        return action;
    }

    public String getToken() {
        return access_token;
    }

    public UserData getUserData() {
        return user;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}