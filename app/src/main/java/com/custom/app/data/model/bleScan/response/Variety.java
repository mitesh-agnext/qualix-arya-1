package com.custom.app.data.model.bleScan.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Variety {

    @SerializedName("variety_id")
    @Expose
    private Integer varietyId;
    @SerializedName("variety_name")
    @Expose
    private String varietyName;

    public Integer getVarietyId() {
        return varietyId;
    }

    public void setVarietyId(Integer varietyId) {
        this.varietyId = varietyId;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }

}