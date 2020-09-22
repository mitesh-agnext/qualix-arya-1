package com.custom.app.data.model.quantity;

import org.parceler.Parcel;

import java.util.Map;

@Parcel
public class DeviceTypeItem {

    public DeviceTypeItem() {
    }

    String device_type_name;
    String total_quantity;
    String quantity_unit;
    String difference;
    String difference_percentage;
    Map<String, Float> device_daily_data;

    public String getDeviceTypeName() {
        return device_type_name;
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

    public Map<String, Float> getDeviceDailyData() {
        return device_daily_data;
    }
}