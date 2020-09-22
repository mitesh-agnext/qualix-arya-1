package com.specx.scan.data.model.factor;

import com.specx.scan.data.model.analysis.AnalysisPayload;

import java.util.List;

public class DataFactorPayload {

    private List<Double> scaleFactor;
    private List<AnalysisPayload> analysisList;

    public List<Double> getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(List<Double> scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public List<AnalysisPayload> getAnalysisList() {
        return analysisList;
    }

    public void setAnalysisList(List<AnalysisPayload> analysisList) {
        this.analysisList = analysisList;
    }
}