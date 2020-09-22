package com.custom.app.data.model.quantity;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class CenterDetailItem {

    public CenterDetailItem() {
    }

    String inst_center_id;
    String inst_center_name;
    String inst_center_type_id;
    String inst_center_type_name;
    String total_quantity;
    String quantity_unit;
    String difference;
    String difference_percentage;
    List<DeviceTypeItem> device_type_data;

    public String getInstCenterId() {
        return inst_center_id;
    }

    public String getInstCenterName() {
        return inst_center_name;
    }

    public String getInstCenterTypeId() {
        return inst_center_type_id;
    }

    public String getInstCenterTypeName() {
        return inst_center_type_name;
    }

    public String getTotalQuantity() {
        return total_quantity;
    }

    public String getQuantityUnit() {
        return quantity_unit;
    }

    public String getDifference() {
        return difference;
    }

    public String getDifferencePercentage() {
        return difference_percentage;
    }

    public List<DeviceTypeItem> getDeviceTypeData() {
        return device_type_data;
    }
}