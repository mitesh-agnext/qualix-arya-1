package com.custom.app.data.model.bleScan.response;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Commodity {

    @SerializedName("commodity_id")
    @Expose
    private Integer commodityId;
    @SerializedName("commodity_name")
    @Expose
    private String commodityName;
    @SerializedName("varieties")
    @Expose
    private List<Variety> varieties = null;

    public Integer getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public List<Variety> getVarieties() {
        return varieties;
    }

    public void setVarieties(List<Variety> varieties) {
        this.varieties = varieties;
    }

}