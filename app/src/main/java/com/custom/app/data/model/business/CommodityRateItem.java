package com.custom.app.data.model.business;

import org.parceler.Parcel;

@Parcel
public class CommodityRateItem {

    public CommodityRateItem() {
    }

    String commodity_name;
    String unit;
    String total;
    String variance;
    String scan_count;
    String acceptance;

    public String getCommodityName() {
        return commodity_name;
    }

    public String getUnit() {
        return unit;
    }

    public String getTotal() {
        return total;
    }

    public String getVariance() {
        return variance;
    }

    public String getScanCount() {
        return scan_count;
    }

    public String getAcceptance() {
        return acceptance;
    }
}