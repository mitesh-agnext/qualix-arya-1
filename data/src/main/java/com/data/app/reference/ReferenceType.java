package com.data.app.reference;

public enum ReferenceType {

    FACTORY(1),

    SPECTRALON(2),

    COMPONENT(3),

    ADULTERANT(4),

    VETA(5),

    PREVIOUS();

    public int code;

    ReferenceType() {
    }

    ReferenceType(int code) {
        this.code = code;
    }

    public static ReferenceType from(int code) {
        for (ReferenceType type : values()) {
            if (type.code != 0 && type.code == code) {
                return type;
            }
        }
        return PREVIOUS;
    }
}