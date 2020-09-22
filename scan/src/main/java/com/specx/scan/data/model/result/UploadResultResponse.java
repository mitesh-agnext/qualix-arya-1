package com.specx.scan.data.model.result;

import com.specx.scan.data.model.analysis.AnalysisItem;

import java.util.List;

public class UploadResultResponse {

    private boolean status;
    private String message;
    private List<AnalysisItem> analyses;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<AnalysisItem> getAnalyses() {
        return analyses;
    }
}