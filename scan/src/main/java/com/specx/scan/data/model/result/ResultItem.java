package com.specx.scan.data.model.result;

import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.sample.SampleItem;

import java.util.List;

public class ResultItem {

    private String id;
    private String userId;
    private String batchId;
    private String location;
    private SampleItem sample;
    private CommodityItem commodity;
    private String avgCsvPath;
    private String serialNumber;
    private String datetime;
    private List<AnalysisItem> analyses;
    private String farmerCode;
    private boolean isUploaded;

    private ResultItem(String id, String userId, String batchId, String location, SampleItem sample,
                       CommodityItem commodity, String avgCsvPath, String serialNumber,
                       String datetime, List<AnalysisItem> analyses, String farmerCode, boolean isUploaded) {
        this.id = id;
        this.userId = userId;
        this.sample = sample;
        this.batchId = batchId;
        this.location = location;
        this.commodity = commodity;
        this.avgCsvPath = avgCsvPath;
        this.serialNumber = serialNumber;
        this.datetime = datetime;
        this.analyses = analyses;
        this.farmerCode = farmerCode;
        this.isUploaded = isUploaded;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getBatchId() {
        return batchId;
    }

    public String getLocation() {
        return location;
    }

    public SampleItem getSample() {
        return sample;
    }

    public CommodityItem getCommodity() {
        return commodity;
    }

    public String getAvgCsvPath() {
        return avgCsvPath;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getDatetime() {
        return datetime;
    }

    public List<AnalysisItem> getAnalyses() {
        return analyses;
    }

    public String getFarmerCode() {
        return farmerCode;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public static class Builder {

        private String id;
        private String userId;
        private String batchId;
        private String location;
        private SampleItem sample;
        private CommodityItem commodity;
        private String avgCsvPath;
        private String serialNumber;
        private String datetime;
        private List<AnalysisItem> analyses;
        private String farmerCode;
        private boolean isUploaded;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setBatchId(String batchId) {
            this.batchId = batchId;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setSample(SampleItem sample) {
            this.sample = sample;
            return this;
        }

        public Builder setCommodity(CommodityItem commodity) {
            this.commodity = commodity;
            return this;
        }

        public Builder setAvgCsvPath(String avgCsvPath) {
            this.avgCsvPath = avgCsvPath;
            return this;
        }

        public Builder setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

       public Builder setDatetime(String datetime) {
            this.datetime = datetime;
            return this;
        }

        public Builder setAnalyses(List<AnalysisItem> analyses) {
            this.analyses = analyses;
            return this;
        }

        public Builder setFarmerCode(String farmerCode) {
            this.farmerCode = farmerCode;
            return this;
        }

        public Builder setIsUploaded(boolean isUploaded) {
            this.isUploaded = isUploaded;
            return this;
        }

        public ResultItem build() {
            return new ResultItem(id, userId, batchId, location, sample, commodity, avgCsvPath,
                    serialNumber, datetime, analyses, farmerCode, isUploaded);
        }
    }
}