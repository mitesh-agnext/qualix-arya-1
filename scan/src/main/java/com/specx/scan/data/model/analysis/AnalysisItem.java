package com.specx.scan.data.model.analysis;

import androidx.annotation.DrawableRes;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class AnalysisItem {

    String analysisId;
    String analysisName;
    String totalAmount;
    String amountUnit;
    String darkThumbUrl;
    String lightThumbUrl;
    boolean isAdulterant;
    transient int thumbnail;
    transient boolean isDone;
    transient String algorithm;
    transient String algoConfig;
    transient List<Double> betaMatrix;
    transient List<Double> meanMatrix;

    public AnalysisItem() {
    }

    public AnalysisItem(String analysisId, String analysisName, @DrawableRes int thumbnail, boolean isDone) {
        this.analysisId = analysisId;
        this.analysisName = analysisName;
        this.thumbnail = thumbnail;
        this.isDone = isDone;
    }

    public AnalysisItem(String analysisId, String analysisName, String totalAmount) {
        this.analysisId = analysisId;
        this.analysisName = analysisName;
        this.totalAmount = totalAmount;
    }

    public AnalysisItem(String analysisId, String analysisName, String totalAmount, String amountUnit,
                        String algorithm, String algoConfig, List<Double> betas, List<Double> means,
                        String darkThumbUrl, String lightThumbUrl) {
        this.analysisId = analysisId;
        this.analysisName = analysisName;
        this.totalAmount = totalAmount;
        this.amountUnit = amountUnit;
        this.algorithm = algorithm;
        this.algoConfig = algoConfig;
        this.betaMatrix = betas;
        this.meanMatrix = means;
        this.darkThumbUrl = darkThumbUrl;
        this.lightThumbUrl = lightThumbUrl;
    }

    public String getId() {
        return analysisId;
    }

    public String getName() {
        return analysisName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getAmountUnit() {
        return amountUnit;
    }

    public String getDarkThumbUrl() {
        return darkThumbUrl;
    }

    public String getLightThumbUrl() {
        return lightThumbUrl;
    }

    public boolean isAdulterant() {
        return isAdulterant;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getAlgoConfig() {
        return algoConfig;
    }

    public List<Double> getBetas() {
        return betaMatrix;
    }

    public List<Double> getMeans() {
        return meanMatrix;
    }
}