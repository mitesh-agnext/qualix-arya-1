package com.specx.scan.data.model.analysis;

import java.util.List;

public class AnalysisPayload {

    private String cropId;
    private String algorithm;
    private String algoConfig;
    private String analysisId;
    private String analysisName;
    private String darkThumbUrl;
    private String lightThumbUrl;
    private List<Double> betaMatrix;
    private List<Double> meanMatrix;

    private AnalysisPayload(String cropId, String algorithm, String algoConfig,
                            String analysisId, String analysisName, String darkThumbUrl,
                            String lightThumbUrl, List<Double> betaMatrix, List<Double> meanMatrix) {
        this.cropId = cropId;
        this.algorithm = algorithm;
        this.algoConfig = algoConfig;
        this.analysisId = analysisId;
        this.analysisName = analysisName;
        this.darkThumbUrl = darkThumbUrl;
        this.lightThumbUrl = lightThumbUrl;
        this.betaMatrix = betaMatrix;
        this.meanMatrix = meanMatrix;
    }

    public String getCropId() {
        return cropId;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getAlgoConfig() {
        return algoConfig;
    }

    public String getId() {
        return analysisId;
    }

    public String getName() {
        return analysisName;
    }

    public String getDarkThumbUrl() {
        return darkThumbUrl;
    }

    public String getLightThumbUrl() {
        return lightThumbUrl;
    }

    public List<Double> getBetaMatrix() {
        return betaMatrix;
    }

    public List<Double> getMeanMatrix() {
        return meanMatrix;
    }

    public static class Builder {

        private String cropId;
        private String algorithm;
        private String algoConfig;
        private String analysisId;
        private String analysisName;
        private String darkThumbUrl;
        private String lightThumbUrl;
        private List<Double> betaMatrix;
        private List<Double> meanMatrix;

        public Builder setCropId(String cropId) {
            this.cropId = cropId;
            return this;
        }

        public Builder setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder setAlgoConfig(String algoConfig) {
            this.algoConfig = algoConfig;
            return this;
        }

        public Builder setAnalysisId(String analysisId) {
            this.analysisId = analysisId;
            return this;
        }

        public Builder setAnalysisName(String analysisName) {
            this.analysisName = analysisName;
            return this;
        }

        public Builder setDarkThumbUrl(String darkThumbUrl) {
            this.darkThumbUrl = darkThumbUrl;
            return this;
        }

        public Builder setLightThumbUrl(String lightThumbUrl) {
            this.lightThumbUrl = lightThumbUrl;
            return this;
        }

        public Builder setBetaMatrix(List<Double> betaMatrix) {
            this.betaMatrix = betaMatrix;
            return this;
        }

        public Builder setMeanMatrix(List<Double> meanMatrix) {
            this.meanMatrix = meanMatrix;
            return this;
        }

        public AnalysisPayload build() {
            return new AnalysisPayload(cropId, algorithm, algoConfig, analysisId,
                    analysisName, darkThumbUrl, lightThumbUrl, betaMatrix, meanMatrix);
        }
    }
}