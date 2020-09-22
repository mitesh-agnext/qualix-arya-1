package com.specx.scan.data.model.assay;

import org.parceler.Parcel;

@Parcel
public class AssayResult {

    String analysisName;
    int analysisResult;

    public AssayResult() {
    }

    public String getAnalysisName() {
        return analysisName;
    }

    public int getAnalysisResult() {
        return analysisResult;
    }
}