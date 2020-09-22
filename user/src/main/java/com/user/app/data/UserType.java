package com.user.app.data;

public enum UserType {

    CLIENT(1),

    MANAGER(2),

    FIELD_STAFF(3);

    public final int role;

    UserType(int role) {
        this.role = role;
    }

    public static UserType from(int role) {
        for (UserType userType : values()) {
            if (userType.role == role) {
                return userType;
            }
        }
        return FIELD_STAFF;
    }
}