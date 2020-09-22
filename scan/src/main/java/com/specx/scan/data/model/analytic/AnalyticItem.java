package com.specx.scan.data.model.analytic;

import org.parceler.Parcel;

@Parcel
public class AnalyticItem {

    String analytic_name;
    String commodity_analytic_id;
    String expiredOn;
    int scanLeft;

    public AnalyticItem() {
    }

    public String getId() {
        return commodity_analytic_id;
    }

    public String getName() {
        return analytic_name;
    }

    public String getExpiredOn() {
        return expiredOn;
    }

    public int getScanLeft() {
        return scanLeft;
    }
}