package com.user.app.data;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class UserData {

    String user_id;
    String first_name;
    String last_name;
    String email;
    String profile;
    String username;
    String contact_number;
    String customer_id;
    List<String> roles;

    public UserData() {
    }

    public String getId() {
        return user_id;
    }

    public void setId(String id) {
        this.user_id = id;
    }

    public String getName() {
        return String.format("%s %s", first_name, last_name);
    }

    public String getEmail() {
        return email;
    }

    public String getProfile() {
        return profile;
    }

    public String getUsername() {
        return getEmail();
    }

    public String getMobile() {
        return contact_number;
    }

    public String getCustomerId() {
        return customer_id;
    }

    public List<String> getRoles() {
        return roles;
    }
}