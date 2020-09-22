package com.specx.scan.data.model.analysis;

public class AnalysisRequest {

    private String userId;
    private String commodityId;

    public AnalysisRequest(String userId, String commodityId) {
        this.userId = userId;
        this.commodityId = commodityId;
    }
}