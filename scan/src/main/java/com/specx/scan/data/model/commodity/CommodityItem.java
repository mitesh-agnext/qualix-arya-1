package com.specx.scan.data.model.commodity;

import com.specx.scan.data.model.analytic.AnalyticItem;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class CommodityItem {

    String commodity_id;
    String commodity_code;
    String commodity_name;
    String commodity_category_id;
    String commodity_category_name;
    List<AnalyticItem> analyticsProcured;
    String amountUnit;
    String totalAmount;
    int count;
    transient boolean isUploaded;

    public CommodityItem() {
    }

    public CommodityItem(String commodity_id, String commodity_name) {
        this.commodity_id = commodity_id;
        this.commodity_name = commodity_name;
    }

    public String getId() {
        return commodity_id;
    }

    public String getName() {
        return commodity_name;
    }

    public String getCategory() {
        return commodity_category_name;
    }

    public List<AnalyticItem> getAnalyticsProcured() {
        return analyticsProcured;
    }

    public String getAmountUnit() {
        return amountUnit;
    }

    public void setAmountUnit(String amountUnit) {
        this.amountUnit = amountUnit;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}