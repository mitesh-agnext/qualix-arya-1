package com.specx.scan.data.model.assay;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class AssayResponse {

    boolean status;
    String message;
    int imgCount;
    float totalTime;
    String result_url;
    String classification;
    float avg_aspect_ratio;
    List<AssayResult> analysisList;

    public AssayResponse() {
    }

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getImgCount() {
        return imgCount;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public String getResult_url() {
        return result_url;
    }

    public String getClassification() {
        return classification;
    }

    public float getAvg_aspect_ratio() {
        return avg_aspect_ratio;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime = totalTime;
    }

    public List<AssayResult> getAnalysisList() {
        return analysisList;
    }
}