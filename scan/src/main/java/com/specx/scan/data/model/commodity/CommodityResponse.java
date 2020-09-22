package com.specx.scan.data.model.commodity;

import java.util.List;

public class CommodityResponse {

    private boolean status;
    private String message;
    private List<CommodityItem> data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<CommodityItem> getData() {
        return data;
    }
}