package com.specx.scan.data.model.factor;

public class DataFactorRequest {

    private String userId;
    private String serialNumber;

    public DataFactorRequest(String userId, String serialNumber) {
        this.userId = userId;
        this.serialNumber = serialNumber;
    }
}