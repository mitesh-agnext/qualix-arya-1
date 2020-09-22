package com.custom.app.data.model.farmer.verify;

import com.custom.app.data.model.farmer.upload.FarmerItem;

public class VerifyFarmerResponse {

    private boolean status;
    private String message;
    private FarmerItem payload;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public FarmerItem getPayload() {
        return payload;
    }
}