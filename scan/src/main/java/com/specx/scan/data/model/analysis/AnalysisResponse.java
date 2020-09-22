package com.specx.scan.data.model.analysis;

import java.util.List;

public class AnalysisResponse {

    private boolean status;
    private String message;
    private List<AnalysisItem> data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<AnalysisItem> getData() {
        return data;
    }
}