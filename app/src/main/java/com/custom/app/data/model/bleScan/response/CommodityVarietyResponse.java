package com.custom.app.data.model.bleScan.response;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommodityVarietyResponse {

    @SerializedName("commodity_details")
    @Expose
    private List<CommodityDetail> commodityDetails = null;

    public List<CommodityDetail> getCommodityDetails() {
        return commodityDetails;
    }

    public void setCommodityDetails(List<CommodityDetail> commodityDetails) {
        this.commodityDetails = commodityDetails;
    }

}