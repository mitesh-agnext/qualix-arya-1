package com.custom.app.data.model.quantity;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class QuantityDetailRes {

    public QuantityDetailRes() {
    }

    String total_quantity;
    String quantity_unit;
    List<CenterDetailItem> inst_center_details;
    CollectionAvgRes collection;

    public String getTotalQuantity() {
        return total_quantity;
    }

    public String getQuantityUnit() {
        return quantity_unit;
    }

    public List<CenterDetailItem> getInstCenterDetails() {
        return inst_center_details;
    }

    public CollectionAvgRes getCollection() {
        return collection;
    }
}