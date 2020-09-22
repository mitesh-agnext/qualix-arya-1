package com.custom.app.data.model.business;

import java.util.List;

public class AcceptedAvgRes {

    private int total_scan_count;
    private int total_avg_variance_rate;
    private int total_avg_accepted_rate;
    private List<PerDayAccepted> per_day_variance;
    private List<PerDayAccepted> per_day_accepted;
    private List<CommodityRateItem> commodity_scan;
    private List<CommodityRateItem> commodity_variance;
    private List<CommodityRateItem> commodity_accepted_rate;

    public int getTotal_scan_count() {
        return total_scan_count;
    }

    public int getTotalAvgVarianceRate() {
        return total_avg_variance_rate;
    }

    public int getTotalAvgAcceptedRate() {
        return total_avg_accepted_rate;
    }

    public List<PerDayAccepted> getPerDayVariance() {
        return per_day_variance;
    }

    public List<PerDayAccepted> getPerDayAccepted() {
        return per_day_accepted;
    }

    public List<CommodityRateItem> getCommodityScan() {
        return commodity_scan;
    }

    public List<CommodityRateItem> getCommodityVariance() {
        return commodity_variance;
    }

    public List<CommodityRateItem> getCommodityAcceptedRate() {
        return commodity_accepted_rate;
    }
}