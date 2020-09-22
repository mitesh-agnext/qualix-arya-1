package com.custom.app.data.model.business;

import org.parceler.Parcel;

@Parcel
public class CommodityRateItem {

    public CommodityRateItem() {
    }

    String commodity_name;
    String unit;
    int total;
    int variance;
    int scan_count;
    int acceptance;

    public String getCommodityName() {
        return commodity_name;
    }

    public String getUnit() {
        return unit;
    }

    public int getTotal() {
        return total;
    }

    public int getVariance() {
        return variance;
    }

    public int getScanCount() {
        return scan_count;
    }

    public int getAcceptance() {
        return acceptance;
    }
}