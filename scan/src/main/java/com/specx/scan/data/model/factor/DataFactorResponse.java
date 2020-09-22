package com.specx.scan.data.model.factor;

public class DataFactorResponse {

    private boolean status;
    private String message;
    private DataFactorPayload payload;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public DataFactorPayload getPayload() {
        return payload;
    }
}