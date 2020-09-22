package com.custom.app.data.model.password.change;

public class ChangePasswordRequest {

    private String old_password;
    private String new_password;

    public ChangePasswordRequest(String old_password, String new_password) {
        this.old_password = old_password;
        this.new_password = new_password;
    }
}